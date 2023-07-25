package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @Author: genius
 * @Date: 2023年03月13日 09:18
 * @Version: 1.0
 * @Description:
 */
@Data
public class UpdateTagRequest implements Serializable {
    private long id;
    private Set<String> tagList;
}
