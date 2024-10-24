package com.desolitech.summary.domian.models;

import org.springframework.stereotype.Component;

@Component
public class PaginationResponse {
    private final int page;
    private final int pageSize;
    private int total;

    public PaginationResponse()
    {
        this.page = 0;
        this.pageSize = 0;
        this.total = 0;
    }

    public PaginationResponse(int page, int pageSize, int total) {
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
