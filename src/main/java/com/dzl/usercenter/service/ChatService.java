package com.dzl.usercenter.service;

import com.dzl.usercenter.model.domain.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.request.ChatRequest;
import com.dzl.usercenter.model.vo.MessageVo;

import java.util.Date;
import java.util.List;

/**
* @author genius
* @description 针对表【chat(聊天消息表)】的数据库操作Service
* @createDate 2023-06-26 13:09:18
*/
public interface ChatService extends IService<Chat> {

    /**
     * 保存缓存
     *
     * @param redisKey
     * @param id
     * @param messageVos
     */
    void saveCache(String redisKey, String id, List<MessageVo> messageVos);

    /**
     * 获取缓存
     *
     * @param redisKey
     * @param id
     * @return
     */
    List<MessageVo> getCache(String redisKey, String id);

    /**
     * 获取私聊聊天内容
     *
     * @param chatRequest
     * @param chatType
     * @param loginUser
     * @return
     */
    List<MessageVo> getPrivateChat(ChatRequest chatRequest, int chatType, User loginUser);

    /**
     * 获取大厅聊天纪录
     *
     * @param chatType
     * @param loginUser
     * @return
     */
    List<MessageVo> getHallChat(int chatType, User loginUser);

    /**
     * 聊天记录映射
     *
     * @param fromId
     * @param toId
     * @param text
     * @param chatType
     * @param createTime
     * @return
     */
    MessageVo chatResult(Long fromId, Long toId, String text, Integer chatType, Date createTime);

    /**
     * 队伍聊天室
     *
     * @param chatRequest
     * @param chatType
     * @param loginUser
     * @return
     */
    List<MessageVo> getTeamChat(ChatRequest chatRequest, int chatType, User loginUser);


    /**
     * 删除key
     *
     * @param key
     * @param id
     */
    void deleteKey(String key, String id);
}
