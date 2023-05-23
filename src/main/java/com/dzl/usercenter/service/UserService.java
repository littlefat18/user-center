package com.dzl.usercenter.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzl.usercenter.model.domain.User;
import org.springframework.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    long userRegister(String userAccount, String userPassword, String checkPassword,String planetCode);


    /**
     * 根据用户搜索标签
     * @param tagNameList
     * @return
     */
    List<User> searchUsersByTags(List<String> tagNameList);

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

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout (HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取推荐用户
     * @param pageSize
     * @param pageNum
     * @param request
     */
    Page<User> recommendUsers(long pageSize, long pageNum, HttpServletRequest request);
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 匹配推荐用户
     * @param num
     * @param user
     * @return
     */
    List<User> matchUsers(long num, User user);
}
