package com.dzl.usercenter.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.constant.enums.TeamStatusEnum;
import com.dzl.usercenter.exception.BusinessException;
import com.dzl.usercenter.model.domain.Team;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.domain.UserTeam;
import com.dzl.usercenter.model.dto.TeamQuery;
import com.dzl.usercenter.model.request.TeamJoinRequest;
import com.dzl.usercenter.model.request.TeamQuitRequest;
import com.dzl.usercenter.model.request.TeamUpdateRequest;
import com.dzl.usercenter.model.vo.TeamUserVO;
import com.dzl.usercenter.model.vo.UserVO;
import com.dzl.usercenter.service.TeamService;
import com.dzl.usercenter.mapper.TeamMapper;
import com.dzl.usercenter.service.UserService;
import com.dzl.usercenter.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.CalendarUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.dzl.usercenter.constant.RedisConstant.JOIN_TEAM_KEY;

/**
 * @author genius
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-05-15 13:44:11
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {

    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        // 1.请求参数是否为空
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2.是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final long userId = loginUser.getId();
        // 3.校验信息
        // 3.1 队伍人数>1 且 <= 20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        // 3.2 队伍标题 <= 20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍标题不满足要求");
        }
        // 3.3 描述 <= 512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述过长");
        }
        // 3.4 status 是否公开(int) 不传默认0 (公开)
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户状态不满足要求");
        }
        // 3.5 如果status是加密状态 一定要有密码 且密码 <= 32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || password.length() > 32) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确");
            }
        }
        // 3.6 超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "超时时间 > 当前时间");
        }
        // 3.7 检验用户最多创建五个队伍
        // TODO 有bug 需加锁
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多创建5个队伍");
        }
        // 4.插入队伍信息到队伍表
        team.setId(null);
        //System.out.println("teamId == "+team.getId());
        team.setUserId(userId);
        boolean result = save(team);
        //System.out.println("teamId == "+team.getId());
        Long teamId = team.getId();
        if (!result || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");

        }
        // 5.插入用户 => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建队伍失败");

        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 组合查询条件
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            List<Long> idList = teamQuery.getIdList();
            if (CollectionUtil.isNotEmpty(idList)){
                queryWrapper.in("id",idList);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
            }
            String teamName = teamQuery.getName();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("name", teamName);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(teamName)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQuery.getUserId();
            // 根据创建人查询
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            // 根据状态查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        List<Team> teamList = list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 关联查询用户信息
        // 1.自己写SQL
        // 查询队伍和创建人的信息
        // select * from team t left join user u on t.userId = u.id
        // 查询队伍和已加入队伍成员的信息
        // select * from team t join user_team ut on t.id = user_team.teamId

        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        // 关联查询创建人的用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue;
            }
            User user = userService.getById(userId);
            //User safetyUser = userService.getSafetyUser(user);
            TeamUserVO teamUserVO = BeanUtil.copyProperties(team, TeamUserVO.class);
            // 脱敏用户信息
            if (user != null) {
                UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        if (loginUser.getId() != oldTeam.getUserId() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(teamUpdateRequest.getDescription())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "加密房间必须设置密码");
            }
        }
        Team updateTeam = BeanUtil.copyProperties(teamUpdateRequest, Team.class);
        return updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        if (team.getExpireTime() != null && team.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已过期");
        }
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "禁止加入私有队伍");
        }
        String password = teamJoinRequest.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        long userId = loginUser.getId();
        RLock lock = redissonClient.getLock(JOIN_TEAM_KEY);
        try {
            // 抢到锁并执行
            int i = 0 ;
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    //System.out.println("getLock: " + Thread.currentThread().getId());
                    QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    long hasJoinNum = userTeamService.count(userTeamQueryWrapper);
                    if (hasJoinNum > 5) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建和加入 5 个队伍");
                    }
                    // 不能重复加入已加入的队伍
                    userTeamQueryWrapper = new QueryWrapper<>();
                    userTeamQueryWrapper.eq("userId", userId);
                    userTeamQueryWrapper.eq("teamId", teamId);
                    long hasUserJoinTeam = userTeamService.count(userTeamQueryWrapper);
                    if (hasUserJoinTeam > 0) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已加入该队伍");
                    }
                    // 已加入队伍的人数
                    long teamHasJoinNum = this.countTeamUserByTeamId(teamId);
                    if (teamHasJoinNum >= team.getMaxNum()) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已满");
                    }
                    // 修改队伍信息
                    UserTeam userTeam = new UserTeam();
                    userTeam.setUserId(userId);
                    userTeam.setTeamId(teamId);
                    userTeam.setJoinTime(new Date());
                    return userTeamService.save(userTeam);
                }else {
                    i++;
                    if (i == 10){
                        return false;
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
            return false;
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                //System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验队伍参数
        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);
        // 校验用户参数
        long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"未加入队伍");
        }
        long teamHasJoinNum = countTeamUserByTeamId(teamId);
        // 队伍只剩下一人，解散
        if (teamHasJoinNum == 1){
            // 解散队伍
            removeById(teamId);
        }else {
            // 队伍还剩至少两人
            // 是否为队长
            if (team.getUserId() == userId){
                // 把队伍转移给最早加入的用户
                // 1. 查询已加入队伍的所有用户和加入时间
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId);
                userTeamQueryWrapper.last("order by id asc limit 2");
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();
                // 更新当前队伍的队长
                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);
                boolean result = updateById(updateTeam);
                if (!result){
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新队伍队长失败");
                }
            }
            // 不是队长
        }
        // 移除关系
        return userTeamService.remove(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id , User loginUser) {
        // 1.校验请求
        // 2.校验队伍是否存在
        Team team = getTeamById(id);
        long teamId = team.getId();
        // 3.校验你是不是队伍的队长
        if (team.getUserId() != loginUser.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH,"无权限");
        }
        // 4.移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(userTeamQueryWrapper);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍关联信息失败");
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 获取队伍当前人数
     * @param teamId
     * @return
     */
    private long countTeamUserByTeamId(long teamId){
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId" , teamId);
        return userTeamService.count(userTeamQueryWrapper);
    }

    /**
     * 通过id获取队伍
     * @param teamId
     * @return
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        return team;
    }
}




