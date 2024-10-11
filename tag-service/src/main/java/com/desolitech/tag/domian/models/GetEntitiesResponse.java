package com.desolitech.tag.domian.models;

import java.util.ArrayList;
import java.util.List;

public class GetEntitiesResponse<T> {

    private List<T> entities;
    private final PaginationResponse pagination;

    public GetEntitiesResponse() {
        this.entities = new ArrayList<>();
        this.pagination = new PaginationResponse();
    }

    public GetEntitiesResponse(PaginationResponse pagination) {
        this.entities = new ArrayList<>();
        this.pagination = pagination;
    }

    public GetEntitiesResponse(List<T> entities, PaginationResponse pagination) {
        this.entities = entities;
        this.pagination = pagination;
    }

    public List<T> getEntities() {
        return entities;
    }

    public void setEntities(List<T> entities) {
        this.entities = entities;
    }

    public PaginationResponse getPagination() {
        return pagination;
    }
}
