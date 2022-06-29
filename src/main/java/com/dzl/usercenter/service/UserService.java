package com.dzl.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzl.usercenter.model.domain.User;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
* @author genius
* @description 针对表【user(用户表)】的数据库操作Service
* @createDate 2022-04-24 09:43:11
*/
public interface UserService extends IService<User> {


    /**
     *
     * 用户注册业务逻辑
     * @param userAccount 用户账户
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录业务逻辑
     * @param userAccount 用户账户
     * @param userPassword  用户密码
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);
}
