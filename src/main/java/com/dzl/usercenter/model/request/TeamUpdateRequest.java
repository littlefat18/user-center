package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author dzl
 * @date 2023/5/16 12:14
 */
@Data
public class TeamUpdateRequest implements Serializable {


    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
