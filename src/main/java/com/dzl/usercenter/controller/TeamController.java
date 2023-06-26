package com.dzl.usercenter.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzl.usercenter.common.BaseResponse;
import com.dzl.usercenter.common.DeleteRequest;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.common.ResultUtils;
import com.dzl.usercenter.exception.BusinessException;
import com.dzl.usercenter.model.domain.Team;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.domain.UserTeam;
import com.dzl.usercenter.model.dto.TeamQuery;
import com.dzl.usercenter.model.request.TeamAddRequest;
import com.dzl.usercenter.model.request.TeamJoinRequest;
import com.dzl.usercenter.model.request.TeamQuitRequest;
import com.dzl.usercenter.model.request.TeamUpdateRequest;
import com.dzl.usercenter.model.vo.TeamUserVO;
import com.dzl.usercenter.service.TeamService;
import com.dzl.usercenter.service.UserService;
import com.dzl.usercenter.service.UserTeamService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户接口
 * @author dzl
 */
@RestController
@RequestMapping("/team")
@Api(tags = "用户管理接口")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @Resource
    private UserTeamService userTeamService;
//    @Resource
//    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Team team = BeanUtil.copyProperties(teamAddRequest, Team.class);
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(true);
    }
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.joinTeam(teamJoinRequest,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"加入失败");
        }
        return ResultUtils.success(result);
    }
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(team);
    }

    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery , HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery , isAdmin);
        // 判断当前用户是否已经加入队伍
        // teamIdList 是查询出来的team的id的集合
        // userTeamList 是查出来当前用户加入的team集合
        // hasJoinTeamIdSet 是当前用户加入team的id的集合
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        User loginUser = userService.getLoginUser(request);
        userTeamQueryWrapper.eq("userId",loginUser.getId());
        userTeamQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        // 已加入的队伍id集合
        Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
        teamList.forEach(team ->{
            boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
            team.setHasJoin(hasJoin);
        });
        // 查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList1 = userTeamService.list(userTeamJoinQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList1.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
            int hasJoinNum = teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size();
            team.setHasJoinNum(hasJoinNum);
        });
        return ResultUtils.success(teamList);
    }
    // TODO 分页
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        boolean isAdmin = userService.isAdmin(request);
//        List<TeamUserVO> teamList = teamService.listTeams(teamQuery , isAdmin);
//        // 判断当前用户是否已经加入队伍
//        // teamIdList 是查询出来的team的id的集合
//        // userTeamList 是查出来当前用户加入的team集合
//        // hasJoinTeamIdSet 是当前用户加入team的id的集合
//        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
//        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
//        User loginUser = userService.getLoginUser(request);
//        userTeamQueryWrapper.eq("userId",loginUser.getId());
//        userTeamQueryWrapper.in("teamId",teamIdList);
//        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
//        // 已加入的队伍id集合
//        Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
//        teamList.forEach(team ->{
//            boolean hasJoin = hasJoinTeamIdSet.contains(team.getId());
//            team.setHasJoin(hasJoin);
//        });
//        // 查询已加入队伍的人数
//        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
//        userTeamJoinQueryWrapper.in("teamId",teamIdList);
//        List<UserTeam> userTeamList1 = userTeamService.list(userTeamJoinQueryWrapper);
//        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList1.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
//        teamList.forEach(team -> {
//            int hasJoinNum = teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size();
//            team.setHasJoinNum(hasJoinNum);
//        });
        Team team = BeanUtil.copyProperties(teamQuery, Team.class);
        Page<Team> page = new Page<>(teamQuery.getPageNum(),teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> teamPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(teamPage);
    }
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Boolean result = teamService.quitTeam(teamQuitRequest,loginUser);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"退出队伍失败");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (deleteRequest == null || deleteRequest.getId()<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        User loginUser = userService.getLoginUser(request);
        boolean remove = teamService.deleteTeam(id,loginUser);
        if (!remove){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"删除失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 获取我创建的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> listMyCreateTeams(TeamQuery teamQuery , HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        teamQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery , true);
        // 创建的队伍的id集合
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList1 = userTeamService.list(userTeamJoinQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList1.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
            int hasJoinNum = teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size();
            team.setHasJoinNum(hasJoinNum);
        });
        return ResultUtils.success(teamList);
    }

    /**
     * 获取我加入的队伍
     * @param teamQuery
     * @param request
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery , HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId" ,loginUser.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍id
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        // 已加入的队伍id集合
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery , true);
        // 查询已加入队伍的人数
        QueryWrapper<UserTeam> userTeamJoinQueryWrapper = new QueryWrapper<>();
        userTeamJoinQueryWrapper.in("teamId",idList);
        List<UserTeam> userTeamList1 = userTeamService.list(userTeamJoinQueryWrapper);
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList1.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> {
            int hasJoinNum = teamIdUserTeamList.getOrDefault(team.getId(), new ArrayList<>()).size();
            team.setHasJoinNum(hasJoinNum);
        });
        return ResultUtils.success(teamList);
    }
}
