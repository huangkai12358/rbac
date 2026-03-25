package com.ymjrhk.rbac.dto.base;


import java.io.Serializable;
import java.util.Objects;

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
    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageQuery that = (PageQuery) o;
        return Objects.equals(pageNum, that.pageNum) && Objects.equals(pageSize, that.pageSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageNum, pageSize);
    }

    @Override
    public String toString() {
        return "PageQuery" + "{" + "pageNum=" + pageNum + ", " + "pageSize=" + pageSize + "}";
    }

}
