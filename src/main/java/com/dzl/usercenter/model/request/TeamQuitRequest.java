package com.dzl.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dzl
 * @date 2023/5/17 14:14
 */
@Data
public class TeamQuitRequest implements Serializable {

    /**
     * id
     */
    private Long teamId;
}
