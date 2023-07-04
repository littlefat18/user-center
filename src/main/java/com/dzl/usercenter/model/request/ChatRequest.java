package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: dzl
 * @Date: 2023年04月11日 11:55
 * @Version: 1.0
 * @Description:
 */
@Data
public class ChatRequest implements Serializable {
    private static final long serialVersionUID = 1445805872513828206L;

    /**
     * 队伍聊天室id
     */
    private Long teamId;

    /**
     * 接收消息id
     */
    private Long toId;

}
