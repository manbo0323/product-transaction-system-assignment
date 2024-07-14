package com.manbo.common.util.dto.query;

import com.manbo.common.util.enums.QueryOrderDirectionEnum;
import com.manbo.common.util.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author kai 
 */
public interface QueryOrderHandler extends Serializable {

    Set<QueryOrderDTO> getOrders();

    void setOrders(Set<QueryOrderDTO> orders);

    default boolean isOrderEmpty() {
        return CollectionUtils.isEmpty(getOrders());
    }

    default boolean isOrderExists(final String field) {
        return CollectionUtils.streamOf(getOrders()).anyMatch(q -> StringUtils.equals(q.getField(), field));
    }

    default void addOrder(final String field, final QueryOrderDirectionEnum direction) {
        if (Objects.isNull(getOrders())) {
            setOrders(new LinkedHashSet<>());
        }
        getOrders().add(new QueryOrderDTO(field, direction));
    }

    /**
     * @param field field
     * @param direction direction
     * @return <code>true</code> if orders is empty and value was set.
     *         <code>false</code> if orders is not empty and the new field was ignored.
     */
    default boolean addOrderIfEmpty(final String field, final QueryOrderDirectionEnum direction) {
        if(CollectionUtils.isNotEmpty(getOrders())) {
            return Boolean.FALSE;
        }
        addOrder(field, direction);
        return Boolean.TRUE;
    }

    /**
     * @param field field
     * @return <code>true</code> if orders is empty and value was set.
     *         <code>false</code> if orders is not empty and the new field was ignored.
     */
    default boolean addOrderIfEmpty(final String field) {
        if(CollectionUtils.isNotEmpty(getOrders())) {
            return Boolean.FALSE;
        }
        addOrder(field, QueryOrderDirectionEnum.ASC);
        return Boolean.TRUE;
    }

    /**
     * @param field field
     * @param direction direction
     * @return <code>true</code> if field is a new field in the orders and value was set.
     *         <code>false</code> if field already exists in the orders and the new field was ignored.
     */
    default boolean addOrderIfNotExists(final String field, final QueryOrderDirectionEnum direction) {
        if(isOrderExists(field)) {
            return Boolean.FALSE;
        }
        addOrder(field, direction);
        return Boolean.TRUE;
    }

    /**
     * @param field field
     * @return <code>true</code> if field is a new field in the orders and value was set.
     *         <code>false</code> if field already exists in the orders and the new field was ignored.
     */
    default boolean addOrderIfNotExists(final String field) {
        if(isOrderExists(field)) {
            return Boolean.FALSE;
        }
        addOrder(field, QueryOrderDirectionEnum.ASC);
        return Boolean.TRUE;
    }

    default void addOrderAsc(final String field) {
        addOrder(field, QueryOrderDirectionEnum.ASC);
    }

    default void addOrderDesc(final String field) {
        addOrder(field, QueryOrderDirectionEnum.DESC);
    }

    default Optional<QueryOrderDTO> getOrder(final String field) {
        return CollectionUtils.streamOf(getOrders())
            .filter(q -> StringUtils.equals(q.getField(), field))
            .findFirst();
    }

    default Optional<QueryOrderDTO> popOrder(final String field) {
        final Optional<QueryOrderDTO> ruleOpt = getOrder(field);
        ruleOpt.ifPresent(rule -> getOrders().remove(rule));
        return ruleOpt;
    }
}
