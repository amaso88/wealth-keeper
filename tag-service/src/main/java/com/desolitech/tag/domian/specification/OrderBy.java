package com.desolitech.tag.domian.specification;

public class OrderBy {
    private String fieldName;
    private OrderType order;

    public OrderBy(String fieldName, OrderType order) {
        this.fieldName = fieldName;
        this.order = order;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public OrderType getOrderType() {
        return order;
    }

    public void setOrderType(OrderType order) {
        this.order = order;
    }
}
