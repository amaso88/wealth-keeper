package com.desolitech.tag.infrastructure.specification;

import com.desolitech.tag.domian.specification.Criteria;
import com.desolitech.tag.domian.specification.Filter;
import com.desolitech.tag.domian.specification.FilterConcat;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JpaSpecificationBuilder<T> {

    public Specification<T> createSpecification(Criteria criteria) {
        if (criteria == null || CollectionUtils.isEmpty(criteria.getFilters())) {
            return null;
        }
        FilterConcat concat = null;
        Specification<T> specification = null;
        int countNextIndex = 1;
        for (var i = 0; i < criteria.getFilters().size(); i+=countNextIndex) {
            var filter = criteria.getFilters().get(i);
            if (filter.getSymbolInclude().equals("(")) {
                var partialCriteria = partialCriteria(criteria, filter);
                if (partialCriteria != null) {
                    var internalSpecification = createSpecification(partialCriteria);
                    var filterSpecification = concatSpecification(buildCurrentSpecification(filter), internalSpecification, filter.getFilterConcat());
                    if (concat != null && specification != null) {
                        specification = concatSpecification(specification, filterSpecification, concat);
                    } else {
                        specification = filterSpecification;
                    }
                    countNextIndex = partialCriteria.getFilters().size() + 1;
                    continue;
                }
            }
            else {
                countNextIndex = 1;
            }
            specification = buildSpecification(specification, filter, concat);
            concat = filter.getFilterConcat();
        }

        return specification;
    }

    private Criteria partialCriteria(Criteria originCriteria, Filter parentFilter) {
        var filterIndex = originCriteria.getFilters().indexOf(parentFilter) + 1;
        if (filterIndex >= originCriteria.getFilters().size()) {
            return null;
        }

        var partialCriteria = new Criteria();
        Filter nextPartialFilter;
        do {
            try {
                nextPartialFilter = originCriteria.getFilters().get(filterIndex++);
            } catch (IndexOutOfBoundsException e) {
                nextPartialFilter = null;
            }
            if (nextPartialFilter != null) {
                partialCriteria.addFilter(nextPartialFilter);
            }
        }
        while (nextPartialFilter != null && !nextPartialFilter.getSymbolInclude().equals(")"));

        return partialCriteria;
    }

    private Specification<T> buildSpecification(Specification<T> specification, Filter filter, FilterConcat concat) {
        Specification<T> specificationResult;

        if (concat != null && specification != null) {
            specificationResult = concatSpecification(specification, buildCurrentSpecification(filter), concat);
        } else {
            specificationResult = buildCurrentSpecification(filter);
        }

        return specificationResult;
    }

    private Specification<T> concatSpecification(Specification<T> specificationA, Specification<T> specificationB, FilterConcat concat) {
        Specification<T> specificationResult;

        if (concat.equals(FilterConcat.AND)) {
            specificationResult = specificationA.and(specificationB);
        } else {
            specificationResult = specificationA.or(specificationB);
        }

        return specificationResult;
    }

    private Specification<T> buildCurrentSpecification(Filter filter) {
        var logicalOperators = buildCurrentSpecificationLogicalOperators(filter);
        if (logicalOperators != null) {
            return logicalOperators;
        }

        return buildCurrentSpecificationComparisonOperators(filter);
    }

    private Specification<T> buildCurrentSpecificationLogicalOperators(Filter filter) {
        var equalsLogicalOperators = buildCurrentSpecificationEqualsLogicalOperators(filter);
        if (equalsLogicalOperators != null) {
            return equalsLogicalOperators;
        }

        var greaterLogicalOperators = buildCurrentSpecificationGreaterLogicalOperators(filter);
        if (greaterLogicalOperators != null) {
            return greaterLogicalOperators;
        }

        return buildCurrentSpecificationLessLogicalOperators(filter);
    }

    private Specification<T> buildCurrentSpecificationEqualsLogicalOperators(Filter filter) {
        switch (filter.getFilterOperator()) {
            case EQ:
                return equalComparer(filter);
            case NEQ:
                return notEqualComparer(filter);
            default:
                return null;
        }
    }

    private Specification<T> buildCurrentSpecificationGreaterLogicalOperators(Filter filter) {
        Specification<T> numberComparer;
        switch (filter.getFilterOperator()) {
            case GTE:
                numberComparer = greaterThanOrEqualNumberComparer(filter);
                if (numberComparer == null) {
                    return greaterThanOrEqualComparer(filter);
                }
                return numberComparer;
            case GT:
                numberComparer = greaterThanNumberComparer(filter);
                if (numberComparer == null) {
                    return greaterThanComparer(filter);
                }
                return numberComparer;
            default:
                return null;
        }
    }

    private Specification<T> buildCurrentSpecificationLessLogicalOperators(Filter filter) {
        Specification<T> numberComparer;
        switch (filter.getFilterOperator()) {
            case LTE:
                numberComparer = lessThanOrEqualNumberComparer(filter);
                if (numberComparer == null) {
                    return lessThanOrEqualComparer(filter);
                }
                return numberComparer;
            case LT:
                numberComparer = lessThanNumberComparer(filter);
                if (numberComparer == null) {
                    return lessThanComparer(filter);
                }
                return numberComparer;
            default:
                return null;
        }
    }

    private Specification<T> buildCurrentSpecificationComparisonOperators(Filter filter) {
        switch (filter.getFilterOperator()) {
            case CONTAINS:
                return contains(filter);
            case NOT_CONTAINS:
                return notContains(filter);
            case RELATED_EQ:
                return relatedEqual(filter);
            case RELATED_CONTAINS:
                return relatedContains(filter);
            default:
                return null;
        }
    }

    private Specification<T> equalComparer(Filter filter) {
        if (filter.getValue() == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get(filter.getFieldName()));
        }
        if (filter.getValue().getClass() == String.class && filter.getValue() instanceof String) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(filter.getFieldName())), filter.getValue().toString().toLowerCase());
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(filter.getFieldName()), filter.getValue());
    }

    private Specification<T> notEqualComparer(Filter filter) {
        if (filter.getValue() == null) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get(filter.getFieldName()));
        }
        if (filter.getValue().getClass() == String.class && filter.getValue() instanceof String) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(criteriaBuilder.lower(root.get(filter.getFieldName())), filter.getValue().toString().toLowerCase());
        }
        return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(filter.getFieldName()), filter.getValue());
    }

    private Specification<T> greaterThanOrEqualNumberComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Integer.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Integer ? criteriaBuilder.greaterThanOrEqualTo(root.get(filter.getFieldName()), (Integer) filter.getValue()) : null);
        specificationByClass.put(Double.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Double ? criteriaBuilder.greaterThanOrEqualTo(root.get(filter.getFieldName()), (Double) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> greaterThanOrEqualComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Date.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Date ? criteriaBuilder.greaterThanOrEqualTo(root.get(filter.getFieldName()), (Date) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> lessThanOrEqualNumberComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Integer.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Integer ? criteriaBuilder.lessThanOrEqualTo(root.get(filter.getFieldName()), (Integer) filter.getValue()) : null);
        specificationByClass.put(Double.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Double ? criteriaBuilder.lessThanOrEqualTo(root.get(filter.getFieldName()), (Double) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> lessThanOrEqualComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Date.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Date ? criteriaBuilder.lessThanOrEqualTo(root.get(filter.getFieldName()), (Date) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> greaterThanNumberComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Integer.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Integer ? criteriaBuilder.greaterThan(root.get(filter.getFieldName()), (Integer) filter.getValue()) : null);
        specificationByClass.put(Double.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Double ? criteriaBuilder.greaterThan(root.get(filter.getFieldName()), (Double) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> greaterThanComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Date.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Date ? criteriaBuilder.greaterThan(root.get(filter.getFieldName()), (Date) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> lessThanNumberComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Integer.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Integer ? criteriaBuilder.lessThan(root.get(filter.getFieldName()), (Integer) filter.getValue()) : null);
        specificationByClass.put(Double.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Double ? criteriaBuilder.lessThan(root.get(filter.getFieldName()), (Double) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> lessThanComparer(Filter filter) {
        Map<Class, Specification<T>> specificationByClass = new HashMap<>();
        specificationByClass.put(Date.class, (root, query, criteriaBuilder) ->
                filter.getValue() instanceof Date ? criteriaBuilder.lessThan(root.get(filter.getFieldName()), (Date) filter.getValue()) : null);

        if (!specificationByClass.containsKey(filter.getValue().getClass())) {
            return null;
        }

        return specificationByClass.get(filter.getValue().getClass());
    }

    private Specification<T> contains(Filter filter) {
        if (filter.getValue().getClass() == String.class && filter.getValue() instanceof String) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(filter.getFieldName())), "%" + filter.getValue().toString().toLowerCase() + "%");
        }
        return (root, query, criteriaBuilder) -> root.get(filter.getFieldName()).in(filter.getValue());
    }

    private Specification<T> notContains(Filter filter) {
        if (filter.getValue().getClass() == String.class && filter.getValue() instanceof String) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.notLike(criteriaBuilder.lower(root.get(filter.getFieldName())), "%" + filter.getValue().toString().toLowerCase() + "%");
        }
        return (root, query, criteriaBuilder) -> root.get(filter.getFieldName()).in(filter.getValue()).not();
    }

    private Specification<T> relatedEqual(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            var split = filter.getFieldName().split("\\.");
            if (split.length != 2) {
                return null;
            }

            var relation = split[0];
            var field = split[1];
            var join = root.join(relation);
            return criteriaBuilder.equal(join.get(field), filter.getValue());
        };
    }

    private Specification<T> relatedContains(Filter filter) {
        return (root, query, criteriaBuilder) -> {
            var split = filter.getFieldName().split("\\.");
            if (split.length != 2) {
                return null;
            }

            var relation = split[0];
            var field = split[1];
            var join = root.join(relation);
            if (filter.getValue().getClass() == String.class && filter.getValue() instanceof String) {
                return criteriaBuilder.like(criteriaBuilder.lower(join.get(field)), "%" + filter.getValue().toString().toLowerCase() + "%");
            }

            return join.get(field).in(filter.getValue());
        };
    }
}
