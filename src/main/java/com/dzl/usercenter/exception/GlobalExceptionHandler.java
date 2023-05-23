package com.dzl.usercenter.exception;

import com.dzl.usercenter.common.BaseResponse;
import com.dzl.usercenter.common.ErrorCode;
import com.dzl.usercenter.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessExceptionHandler"+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());

    }
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(BusinessException e){
        log.error("runtimeExceptionHandler"+e.getMessage(),e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");

    }
}
