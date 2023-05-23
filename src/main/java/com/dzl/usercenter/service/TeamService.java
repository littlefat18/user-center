package com.dzl.usercenter.service;

import com.dzl.usercenter.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.dto.TeamQuery;
import com.dzl.usercenter.model.request.TeamJoinRequest;
import com.dzl.usercenter.model.request.TeamQuitRequest;
import com.dzl.usercenter.model.request.TeamUpdateRequest;
import com.dzl.usercenter.model.vo.TeamUserVO;

import java.util.List;

/**
* @author genius
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-05-15 13:44:11
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍
     * @param team
     * @return
     */
    long addTeam(Team team , User loginUser);

    /**
     * 查询队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser);

    /**
     * 加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest,User loginUser);

    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser);

    /**
     * 解散队伍
     * @param id
     * @return
     */
    boolean deleteTeam(long id ,User loginUser );
}
