package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: dzl
 * @Date: 2023年03月10日 17:22
 * @Version: 1.0
 * @Description:
 */
@Data
public class UserUpdateRequest implements Serializable {
    Integer id;
    String field;
    String editValue;
}
