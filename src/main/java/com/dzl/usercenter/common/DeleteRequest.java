package com.dzl.usercenter.common;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用删除请求
 *
 * @author dzl
 */
@Data
public class DeleteRequest implements Serializable {

    private long id;
}

