package com.ymjrhk.rbac.result;


import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class PageResult<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 当前页数据
     */
    private List<T> records;
    public PageResult(long total, List<T> records) {
        this.total = total;
        this.records = records;
    }

    public PageResult() {
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PageResult that = (PageResult) o;
        return Objects.equals(total, that.total) && Objects.equals(records, that.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, records);
    }

    @Override
    public String toString() {
        return "PageResult" + "{" + "total=" + total + ", " + "records=" + records + "}";
    }

}
