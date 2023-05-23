package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dzl
 * @date 2023/5/16 12:14
 */
@Data
public class TeamJoinRequest implements Serializable {


    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
