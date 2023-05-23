package com.dzl.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用分页请求参数
 * @author dzl
 * @date 2023/5/15 14:21
 */
@Data
public class PageRequest implements Serializable {

    protected int pageSize = 10;

    protected int pageNum = 1;
}
