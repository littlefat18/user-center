package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class QQLoginRequest implements Serializable {
    private String code;
}
