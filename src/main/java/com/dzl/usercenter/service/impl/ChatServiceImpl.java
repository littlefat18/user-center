package com.dzl.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzl.usercenter.model.domain.Chat;
import com.dzl.usercenter.service.ChatService;
import com.dzl.usercenter.mapper.ChatMapper;
import org.springframework.stereotype.Service;

/**
* @author genius
* @description 针对表【chat(聊天消息表)】的数据库操作Service实现
* @createDate 2023-06-26 13:09:18
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService{

}




