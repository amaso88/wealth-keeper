package com.desolitech.tag.infrastructure.repositories;

import com.desolitech.tag.domian.entities.BaseEntity;
import com.desolitech.tag.infrastructure.models.BaseDbModel;

import java.util.List;

public interface GenericMapper<T extends BaseEntity, M extends BaseDbModel> {
    T toEntity(M model);
    List<T> toEntities(List<M> models);
    M toModel(T entity);
    List<M> toModels(List<T> entities);
}
