package com.dzl.usercenter.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户包装类
 * @author dzl
 * @date 2023/5/15 17:14
 */
@Data
public class UserVO implements Serializable {

    /**
     *
     */
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
     * 性别
     */
    private Integer gender;


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
     * 星球编号
     */
    private String tags;
}
