package com.dzl.usercenter.constant;

/**
 * 用户常量接口
 *
 * @author genius
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";

    /**
     * 用户权限
     * 0为默认权限
     * 1为管理员权限
     */
    int DEFAULT_ROLE =0;
    int ADMIN_ROLE=1;
}
