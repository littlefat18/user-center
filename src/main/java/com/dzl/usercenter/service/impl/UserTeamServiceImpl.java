package com.dzl.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzl.usercenter.model.domain.UserTeam;
import com.dzl.usercenter.service.UserTeamService;
import com.dzl.usercenter.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author genius
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-05-15 13:45:08
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




