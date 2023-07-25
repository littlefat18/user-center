package com.dzl.usercenter.controller;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzl.usercenter.common.BaseResponse;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.common.ResultUtils;
import com.dzl.usercenter.exception.BusinessException;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.request.UpdateTagRequest;
import com.dzl.usercenter.model.request.UserLoginRequest;
import com.dzl.usercenter.model.request.UserQueryRequest;
import com.dzl.usercenter.model.request.UserRegisterRequest;
import com.dzl.usercenter.model.vo.UserVO;
import com.dzl.usercenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@Api(tags = "用户管理接口")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){

        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String username = userRegisterRequest.getUsername();

        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,username)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码不能为空");
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword, username);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
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
    @ApiOperation("用户登出")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    @ApiOperation("用户搜索")
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

    @GetMapping("/search/tags")
    @ApiOperation("通过标签搜索用户")
    public BaseResponse<List<User>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }
    @GetMapping("/current")
    @ApiOperation("查看当前登录用户")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        System.out.println(request.getSession().getId());
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

    @PostMapping("/update")
    @ApiOperation("更新用户")
    public BaseResponse<Integer> updateUsers(@RequestBody User user, HttpServletRequest request){
        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/update/tags")
    public BaseResponse<Integer> updateTagById(@RequestBody UpdateTagRequest tagRequest, HttpServletRequest request) {
        if (tagRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getLoginUser(request);
        int updateTag = userService.updateTagById(tagRequest, currentUser);
        redisTemplate.delete(userService.redisFormat(currentUser.getId()));
        return ResultUtils.success(updateTag);
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize,long pageNum,HttpServletRequest request) {
        Page<User> userPage = userService.recommendUsers(pageSize, pageNum, request);
//        List<User> userList = userService.list(queryWrapper);
//        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(userPage);

    }



    @GetMapping("/{id}")
    public BaseResponse<UserVO> getUserById(@PathVariable("id") Integer id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        //long userId = loginUser.getId();
        //User loginUser1 = userService.getById(userId);
        //System.out.println("id========================"+loginUser.getUserIds());
        User user = userService.getById(id);
        UserVO userVO = BeanUtil.copyProperties(user, UserVO.class);
        Boolean isFriend = userService.isFriend(id, loginUser);
        //System.out.println(isFriend);
        userVO.setIsFriend(isFriend);
        return ResultUtils.success(userVO);
    }


    @PostMapping("/delete")
    @ApiOperation("删除用户")
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

    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num , HttpServletRequest request){
        if (num <=0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num, user));
    }

    @GetMapping("/friends")
    public BaseResponse<List<User>> getFriends(HttpServletRequest request) {
        User currentUser = userService.getLoginUser(request);
        List<User> friends = userService.getFriendsById(currentUser);
        return ResultUtils.success(friends);
    }

    @PostMapping("/deleteFriend/{id}")
    public BaseResponse<Boolean> deleteFriend(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "好友不存在");
        }
        User currentUser = userService.getLoginUser(request);
        boolean deleteFriend = userService.deleteFriend(currentUser, id);
        return ResultUtils.success(deleteFriend);
    }

    @PostMapping("/searchFriend")
    public BaseResponse<List<User>> searchFriend(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        User currentUser = userService.getLoginUser(request);
        List<User> searchFriend = userService.searchFriend(userQueryRequest, currentUser);
        return ResultUtils.success(searchFriend);
    }

    /**
     * 判断是否为管理员
     * @param request
     * @return
     */
    @ApiOperation("判断是否为管理员")
    private boolean isAdmin(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        if (user==null || user.getUserRole()!=ADMIN_ROLE){
            return false;
        }
        return true;
    }
}
