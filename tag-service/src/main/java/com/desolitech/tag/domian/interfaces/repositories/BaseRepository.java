package com.desolitech.tag.domian.interfaces.repositories;



import com.desolitech.tag.domian.entities.BaseEntity;
import com.desolitech.tag.domian.models.GetEntitiesResponse;
import com.desolitech.tag.domian.specification.Criteria;

import java.util.List;
import java.util.UUID;

public interface BaseRepository<T extends BaseEntity> {
    T add(T entity);
    T update(T entity);
    void updateAll(List<T> entity);
    T getById(UUID id);
    long count();
    long count(Criteria criteria);
    GetEntitiesResponse<T> getAll(Criteria criteria);
    T getFirst(Criteria criteria);
    T hasRevisions(T entity);
    List<T> getRevisionsById(UUID id);
    GetEntitiesResponse<T> getRevisions(Criteria criteria);
}
