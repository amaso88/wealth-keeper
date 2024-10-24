package com.desolitech.summary.infrastructure.repositories;

import com.desolitech.summary.domian.entities.BaseEntity;
import com.desolitech.summary.domian.interfaces.repositories.BaseRepository;
import com.desolitech.summary.domian.models.GetEntitiesResponse;
import com.desolitech.summary.domian.models.PaginationResponse;
import com.desolitech.summary.domian.specification.Criteria;
import com.desolitech.summary.domian.specification.FilterOperator;
import com.desolitech.summary.domian.specification.OrderBy;
import com.desolitech.summary.domian.specification.OrderType;
import com.desolitech.summary.infrastructure.models.BaseDbModel;
import com.desolitech.summary.infrastructure.specification.JpaSpecificationBuilder;
import com.google.common.reflect.TypeToken;
import de.mobiuscode.nameof.Name;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BaseRepositoryImpl<T extends BaseEntity, M extends BaseDbModel> implements BaseRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;
    private final Class<M> modelType;
    private final JpaGenericRepository<M> repository;
    private final GenericMapper<T, M> mapper;
    private final JpaSpecificationBuilder<M> specificationBuilder;

    public BaseRepositoryImpl(JpaGenericRepository<M> repository,
                              Class<?> mapperClass,
                              JpaSpecificationBuilder<M> specificationBuilder) {
        this.repository = repository;
        var mapperType = Mappers.getMapper(mapperClass);
        this.mapper = (GenericMapper<T, M>) mapperType;
        this.specificationBuilder = specificationBuilder;
        TypeToken<M> typeModelToken = new TypeToken<>(getClass()) {
        };
        this.modelType = (Class<M>) typeModelToken.getRawType();
    }

    public T add(T entity) {
        if (entity.getId() == null || entity.getId().toString().isEmpty()) {
            entity.setId(UUID.randomUUID());
        }

        entity.setCreateAt(new Date());

        var model = mapper.toModel(entity);
        var newModel = repository.save(model);
        return mapper.toEntity(newModel);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public void delete(T entity) {
        var model = mapper.toModel(entity);
        repository.delete(model);
    }

    public void deleteAll(List<T> entities) {
        var models = mapper.toModels(entities);
        repository.deleteAll(models);
    }

    public T update(T entity) {
        var model = mapper.toModel(entity);
        var newModel = repository.save(model);
        return mapper.toEntity(newModel);
    }

    public void updateAll(List<T> entities) {
        var models = new ArrayList<M>();
        for (T entity : entities) {
            var model = mapper.toModel(entity);
            models.add(model);
        }

        repository.saveAll(models);
    }

    public T getById(UUID id) {
        var model = repository.findById(id);
        return model.map(m -> mapper.toEntity(m)).orElse(null);
    }

    public long count() {
        return repository.count();
    }

    public long count(Criteria criteria) {

        var specification = specificationBuilder.createSpecification(criteria);
        return repository.count(specification);
    }

    public GetEntitiesResponse<T> getAll(Criteria criteria) {

        if (criteria.getOrders()
                .stream().noneMatch(o ->
                        o.getFieldName().equals(Name.of(BaseEntity.class, BaseEntity::getCreateAt)))) {

            var orders = new ArrayList<OrderBy>();
            orders.add(new OrderBy(Name.of(BaseEntity.class, BaseEntity::getCreateAt), OrderType.DESC));
            criteria.addOrders(orders);
        }

        return toListOrderedPagedValues(criteria);
    }

    public T getFirst(Criteria criteria) {

        var specification = specificationBuilder.createSpecification(criteria);
        Page<M> models;
        if (criteria.getOrders().isEmpty()) {
            models = repository.findAll(specification, PageRequest.of(0, 1));
        } else {
            models = getModelsBySpecificationOrder(criteria.getOrders(), specification, 0, 1);
        }

        if (models.isEmpty()) {
            return null;
        }
        var model = models.toList().get(0);
        return mapper.toEntity(model);
    }

    private GetEntitiesResponse<T> toListOrderedPagedValues(Criteria criteria) {

        var specification = specificationBuilder.createSpecification(criteria);

        //Get the total amount of entities
        var totalAmount = (int) repository.count(specification);

        //If there is no entities return empty list of entities.
        if (totalAmount == 0) {
            return new GetEntitiesResponse<>();
        }

        //valid max page size by total amount.
        if (totalAmount < criteria.getPageSize()) {
            criteria.setPageSize(totalAmount);
        }

        //Verify if page is correct. The first correct page is 0
        if (criteria.getPage() != 0) {
            var rest = totalAmount % criteria.getPageSize() == 0 ? 0 : 1;
            var lastPage = totalAmount / criteria.getPageSize() + rest;
            if (criteria.getPage() >= lastPage) {
                return new GetEntitiesResponse<>(
                        new PaginationResponse(criteria.getPage(), criteria.getPageSize(), totalAmount)
                );
            }
        }

        //Get entity list by criteria
        var pageModelResponse = createPaginationByCriteria(criteria, specification);

        return new GetEntitiesResponse<>(
                mapper.toEntities(pageModelResponse.toList()),
                new PaginationResponse(criteria.getPage(), criteria.getPageSize(), totalAmount)
        );
    }

    private Page<M> createPaginationByCriteria(Criteria criteria, Specification<M> specification) {
        var orders = criteria.getOrders();
        var page = criteria.getPage();
        var pageSize = criteria.getPageSize();

        if (orders.isEmpty()) {
            return repository.findAll(specification, PageRequest.of(page, pageSize));
        }

        return getModelsBySpecificationOrder(orders, specification, page, pageSize);
    }

    private Page<M> getModelsBySpecificationOrder(List<OrderBy> orders,
                                                  Specification<M> specification,
                                                  int page, int pageSize) {
        Page<M> pageModelResponse;
        List<String> orderAscendingProperties = new ArrayList<>();
        List<String> orderDescendingProperties = new ArrayList<>();
        orders.forEach(order -> {
            if (order.getOrderType() == OrderType.ASC) {
                orderAscendingProperties.add(order.getFieldName());
            } else {
                orderDescendingProperties.add(order.getFieldName());
            }
        });

        if (!orderAscendingProperties.isEmpty() && !orderDescendingProperties.isEmpty()) {
            pageModelResponse = repository.findAll(specification, PageRequest.of(page, pageSize,
                    Sort.by(Sort.Direction.ASC, orderAscendingProperties.toArray(new String[orderAscendingProperties.size()])).and(
                            Sort.by(Sort.Direction.DESC, orderDescendingProperties.toArray(new String[orderDescendingProperties.size()]))
                    )));
        } else if (!orderAscendingProperties.isEmpty()) {
            pageModelResponse = repository.findAll(specification, PageRequest.of(page, pageSize,
                    Sort.by(Sort.Direction.ASC, orderAscendingProperties.toArray(new String[orderAscendingProperties.size()]))));
        } else {
            pageModelResponse = repository.findAll(specification, PageRequest.of(page, pageSize,
                    Sort.by(Sort.Direction.DESC, orderDescendingProperties.toArray(new String[orderDescendingProperties.size()]))));
        }

        return pageModelResponse;
    }

    public T hasRevisions(T entity) {

        var model = ((GenericMapper<T, M>) mapper).toModel(entity);
        try {
            AuditReader reader = AuditReaderFactory.get(entityManager);
            var revision = reader.createQuery()
                    .forRevisionsOfEntity(model.getClass(), false, true)
                    .getResultList()
                    .get(0);

            if (revision == null) {
                return null;
            }
            var modelAudit = getModel(revision);
            return mapper.toEntity(modelAudit);
        } catch (Exception e) {
            return null;
        }
    }

    public List<T> getRevisionsById(UUID id) {

        AuditReader reader = AuditReaderFactory.get(entityManager);
        var revisions = reader.createQuery()
                .forRevisionsOfEntity(modelType, false, true)
                .add(AuditEntity.id().eq(id))
                .addOrder(AuditEntity.revisionNumber().desc())
                .addOrder(AuditEntity.property("updateAt").desc())
                .getResultList();

        if (revisions.isEmpty()) {
            return new ArrayList<>();
        }

        return convertRevisionToEntities(revisions);
    }

    public GetEntitiesResponse<T> getRevisions(Criteria criteria) {

        AuditReader reader = AuditReaderFactory.get(entityManager);
        var query = reader.createQuery()
                .forRevisionsOfEntity(modelType, false, true);
        var queryCount = reader.createQuery()
                .forRevisionsOfEntity(modelType, false, true);

        //Add filters
        if (criteria.getFilters() != null) {
            for (var filter : criteria.getFilters()) {
                if (filter.getFilterOperator().equals(FilterOperator.EQ)) {
                    query.add(AuditEntity.property(filter.getFieldName()).eq(filter.getValue()));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).eq(filter.getValue()));
                }
                else if (filter.getFilterOperator().equals(FilterOperator.LT)) {
                    query.add(AuditEntity.property(filter.getFieldName()).lt(filter.getValue()));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).lt(filter.getValue()));
                }
                else if (filter.getFilterOperator().equals(FilterOperator.GT)) {
                    query.add(AuditEntity.property(filter.getFieldName()).gt(filter.getValue()));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).gt(filter.getValue()));
                }
                else if (filter.getFilterOperator().equals(FilterOperator.LTE)) {
                    query.add(AuditEntity.property(filter.getFieldName()).le(filter.getValue()));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).le(filter.getValue()));
                }
                else if (filter.getFilterOperator().equals(FilterOperator.GTE)) {
                    query.add(AuditEntity.property(filter.getFieldName()).ge(filter.getValue()));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).ge(filter.getValue()));
                }
                else if (filter.getFilterOperator().equals(FilterOperator.NEQ)) {
                    query.add(AuditEntity.property(filter.getFieldName()).ne(filter.getValue()));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).ne(filter.getValue()));
                }
                else {
                    query.add(AuditEntity.property(filter.getFieldName()).ilike("%s"+filter.getValue()+"%s"));
                    queryCount.add(AuditEntity.property(filter.getFieldName()).ilike("%s"+filter.getValue()+"%s"));
                }
            }
        }

        //Add orders
        if (!criteria.getOrders().isEmpty()) {
            var orders = criteria.getOrders();
            List<String> orderAscendingProperties = new ArrayList<>();
            List<String> orderDescendingProperties = new ArrayList<>();
            orders.forEach(order -> {
                if (order.getOrderType() == OrderType.ASC) {
                    orderAscendingProperties.add(order.getFieldName());
                } else {
                    orderDescendingProperties.add(order.getFieldName());
                }
            });

            if (!orderAscendingProperties.isEmpty() && !orderDescendingProperties.isEmpty()) {
                for (var order : orderAscendingProperties) {
                    query.addOrder(AuditEntity.property(order).asc());
                }
                for (var order : orderDescendingProperties) {
                    query.addOrder(AuditEntity.property(order).desc());
                }
            } else if (!orderAscendingProperties.isEmpty()) {
                for (var order : orderAscendingProperties) {
                    query.addOrder(AuditEntity.property(order).asc());
                }
            } else {
                for (var order : orderDescendingProperties) {
                    query.addOrder(AuditEntity.property(order).desc());
                }
            }
        }
        query.addOrder(AuditEntity.revisionNumber().desc());

        //Get total amount of entities
        var totalAmount = queryCount.getResultList().size();

        //Add pagination
        var revisions = query
                .setFirstResult(criteria.getPage() * criteria.getPageSize())
                .setMaxResults(criteria.getPageSize())
                .getResultList();

        if (revisions.isEmpty()) {
            return new GetEntitiesResponse<>();
        }

        var entities = convertRevisionToEntities(revisions);

        return new GetEntitiesResponse<>(entities, new PaginationResponse(criteria.getPage(), criteria.getPageSize(), totalAmount));
    }

    private List<T> convertRevisionToEntities(List revisions) {
        var models = getModels(revisions);
        var entities = new ArrayList<T>();
        for (var m : models) {
            try {
                var entity = mapper.toEntity(m);
                entities.add(entity);
            } catch (Exception ignored) {
            }
        }
        return entities;
    }

    private ArrayList<M> getModels(List revisions) {
        var models = new ArrayList<M>();
        for (var revision : revisions) {
            var modelDb = getModel(revision);
            models.add(modelDb);
        }
        return models;
    }

    private M getModel(Object revision) {
        Object[] objArray = (Object[]) revision;

        var modelDb = (M) objArray[0];
        var defaultRevisionEntity = (DefaultRevisionEntity) objArray[1];
        var revisionType = (RevisionType) objArray[2];
        modelDb.setAuditRevision(defaultRevisionEntity.getId());
        modelDb.setAuditRevisionType(
                revisionType.ordinal() == 0 ? "CREATE" :
                        revisionType.ordinal() == 1 ? "UPDATE" :
                                revisionType.ordinal() == 2 ? "DELETE" :
                                        "UNKNOWN");
        return modelDb;
    }
}
