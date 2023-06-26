package com.dzl.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzl.usercenter.model.domain.Friends;
import com.dzl.usercenter.service.FriendsService;
import com.dzl.usercenter.mapper.FriendsMapper;
import org.springframework.stereotype.Service;

/**
* @author genius
* @description 针对表【friends(好友申请管理表)】的数据库操作Service实现
* @createDate 2023-06-26 13:11:00
*/
@Service
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends>
    implements FriendsService{

}




