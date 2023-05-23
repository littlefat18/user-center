package com.dzl.usercenter.common;

/**
 * 错误码
 *
 * @author dzl
 */
public enum ErrorCode {

    SUCCESS(0,"OK",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(400001,"请求数据为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限",""),
    FORBIDDEN(40301,"禁止操作",""),
    SYSTEM_ERROR(50000,"系统内部异常","");

    private int code;
    private String message;
    private String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
