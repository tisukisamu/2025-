package com.club.fund.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;

    private Integer page;

    private Integer size;

    private List<T> list;

    public PageResult() {
    }

    public PageResult(Long total, Integer page, Integer size, List<T> list) {
        this.total = total;
        this.page = page;
        this.size = size;
        this.list = list;
    }
}
