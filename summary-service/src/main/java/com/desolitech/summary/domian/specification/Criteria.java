package com.desolitech.summary.domian.specification;

import java.util.ArrayList;
import java.util.List;

public class Criteria {
    private List<Filter> filters;
    private List<OrderBy> orders;
    private int page;
    private int pageSize;
    public static final int MAX_PAGE_SIZE = 2000;

    public Criteria() {
        this.filters = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.page = 0;
        this.pageSize = Criteria.MAX_PAGE_SIZE;
    }

    public Criteria(List<Filter> filters) {
        this.filters = filters;
        this.orders = new ArrayList<>();
        this.page = 0;
        this.pageSize = Criteria.MAX_PAGE_SIZE;
    }

    public Criteria(List<Filter> filters, List<OrderBy> orders) {
        this.filters = filters;
        this.orders = orders;
        this.page = 0;
        this.pageSize = Criteria.MAX_PAGE_SIZE;
    }

    public Criteria(List<Filter> filters, List<OrderBy> orders, int page, int pageSize) {
        this.filters = filters;
        this.orders = orders;
        this.page = page;
        this.pageSize = pageSize == 0 ? Criteria.MAX_PAGE_SIZE : pageSize;
    }

    public Criteria(List<Filter> filters, int page, int pageSize) {
        this.filters = filters;
        this.orders = new ArrayList<>();
        this.page = page;
        this.pageSize = pageSize == 0 ? Criteria.MAX_PAGE_SIZE : pageSize;
    }

    public Criteria(List<Filter> filters, int pageSize) {
        this.filters = filters;
        this.orders = new ArrayList<>();
        this.page = 0;
        this.pageSize = pageSize == 0 ? Criteria.MAX_PAGE_SIZE : pageSize;
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public void addFilters(List<Filter> filters) {
        this.filters.addAll(filters);
    }

    public void addOrders(List<OrderBy> orders) {
        this.orders.addAll(orders);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public Criteria setFilters(List<Filter> filters) {
        this.filters = filters;
        return this;
    }

    public List<OrderBy> getOrders() {
        return orders;
    }

    public void addOrder(OrderBy order) {
        this.orders.add(order);
    }

    public Criteria setOrders(List<OrderBy> orders) {
        this.orders = orders;
        return this;
    }

    public int getPage() {
        return page;
    }

    public Criteria setPage(int page) {
        this.page = page;
        return this;
    }

    public int getPageSize() {
        return pageSize;
    }

    public Criteria setPageSize(int pageSize) {
        if (pageSize > Criteria.MAX_PAGE_SIZE) {
            pageSize = Criteria.MAX_PAGE_SIZE;
        }
        this.pageSize = pageSize;
        return this;
    }
}
