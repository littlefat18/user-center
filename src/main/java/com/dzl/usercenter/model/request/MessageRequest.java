package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: dzl
 * @Version: 1.0
 * @Description:
 */
@Data
public class MessageRequest implements Serializable {
    private Long toId;
    private Long teamId;
    private String text;
    private Integer chatType;
    private boolean isAdmin;
}
