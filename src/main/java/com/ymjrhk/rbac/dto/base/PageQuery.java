package com.ymjrhk.rbac.dto.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageQuery implements Serializable {

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 得到偏移量
     *
     * @return
     */
    public Integer getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
