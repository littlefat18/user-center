package com.dzl.usercenter.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private long id;

    /**
     * 用户昵称

     */
    private String username;

    /**
     * 用户账户

     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 简介
     */
    private String profile;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态 0正常
     */
    private Integer userStatus;

    /**
     * 用户类型 0普通用户 1管理员
     */
    private Integer userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 添加的好友
     */
    private String userIds;

    /**
     * 星球编号
     */
    private String tags;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
