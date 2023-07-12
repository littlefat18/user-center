package com.dzl.usercenter.controller;

import com.dzl.usercenter.common.BaseResponse;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.common.ResultUtils;
import com.dzl.usercenter.exception.BusinessException;
import com.dzl.usercenter.model.domain.User;
import com.dzl.usercenter.model.request.QQLoginRequest;
import com.dzl.usercenter.service.ThirdPartyLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.dzl.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @Author: dzl
 * @Date: 2023年04月24日 09:39
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/login")
public class ThirdPartyLoginController {
    @Resource
    private ThirdPartyLoginService thirdPartyLoginService;

    @GetMapping("/qq")
    public BaseResponse<String> qqLogin() throws IOException {
        String url = thirdPartyLoginService.qqLogin();
        return ResultUtils.success(url);
    }

    @PostMapping("/loginInfo")
    public BaseResponse<User> saveLoginInfo(@RequestBody QQLoginRequest qqLoginRequest, HttpServletRequest request) throws IOException {
        if (qqLoginRequest == null || StringUtils.isBlank(qqLoginRequest.getCode())) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请重新登录");
        }
        User user = thirdPartyLoginService.getLoginInfo(qqLoginRequest, request);
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return ResultUtils.success(user);
    }
}
