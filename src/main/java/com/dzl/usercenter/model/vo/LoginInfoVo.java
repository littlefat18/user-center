package com.dzl.usercenter.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginInfoVo implements Serializable {
    private String social_uid;
    private String faceimg;
    private String nickname;
    private Integer code;
    private String gender;
}
