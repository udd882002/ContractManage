package com.seven.contract.manage.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by apple on 2018/12/12.
 */
public class PageResult<T> implements Serializable {

    private long records;

    private List<T> rows;

    private int page;

    private int total;

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
