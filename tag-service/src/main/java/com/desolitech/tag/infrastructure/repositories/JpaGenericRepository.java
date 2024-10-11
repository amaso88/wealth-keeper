package com.desolitech.tag.infrastructure.repositories;

import com.desolitech.tag.infrastructure.models.BaseDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface JpaGenericRepository<M extends BaseDbModel> extends JpaRepository<M, UUID>, JpaSpecificationExecutor<M> {
}
