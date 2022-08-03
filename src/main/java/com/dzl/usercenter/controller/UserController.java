package com.dzl.usercenter.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzl.usercenter.common.BaseResponse;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.common.ResultUtils;
import com.dzl.usercenter.exception.BusinessException;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.domain.request.UserLoginRequest;
import com.dzl.usercenter.model.domain.request.UserRegisterRequest;
import com.dzl.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dzl.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.dzl.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 * @author dzl
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){

        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();

        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码不能为空");
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){

        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码不能为空");
        }

        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        //仅管理员可查询
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"没有权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);

        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN,"用户未登录");
        }
        Long userId = currentUser.getId();
        //todo 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUsers(long id,HttpServletRequest request){
        //仅管理员可删除
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"没有权限");
        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"id不能小于1");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user==null || user.getUserRole()!=ADMIN_ROLE){
            return false;
        }
        return true;
    }
}
