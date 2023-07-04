package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequest implements Serializable {

    /**
     * 查询用户
     */
    private String searchText;
}
