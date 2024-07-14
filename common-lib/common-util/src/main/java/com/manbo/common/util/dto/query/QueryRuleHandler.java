package com.manbo.common.util.dto.query;

import com.manbo.common.util.enums.QueryOperatorEnum;
import com.manbo.common.util.utils.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * @author Manbo
 */
public interface QueryRuleHandler extends Serializable {

    Set<QueryRuleDTO> getRules();

    void setRules(Set<QueryRuleDTO> rules);

    default boolean isRuleEmpty() {
        return CollectionUtils.isEmpty(getRules());
    }

    default boolean isRuleExists(final String field) {
        return CollectionUtils.streamOf(getRules()).anyMatch(q -> StringUtils.equals(q.getField(), field));
    }

    default Optional<QueryRuleDTO> getRule(final String field) {
        return CollectionUtils.streamOf(getRules())
            .filter(q -> StringUtils.equals(q.getField(), field))
            .findFirst();
    }

    default <T> Optional<T> getRule(final String field, final Class<T> type) {
        return getRule(field)
            .map(QueryRuleDTO::getOp)
            .map(type::cast);
    }

    default Optional<QueryRuleDTO> popRule(final String field) {
        final Optional<QueryRuleDTO> ruleOpt = getRule(field);
        ruleOpt.ifPresent(rule -> getRules().remove(rule));
        return ruleOpt;
    }

    default <T> Optional<T> popRule(final String field, final Class<T> type) {
        return popRule(field)
            .map(QueryRuleDTO::getData)
            .map(type::cast);
    }

    /**
     * @param field field
     * @param data data
     * @return <code>true</code> if field is a new field in the rules and value was set.
     *         <code>false</code> if field already exists in the rules and the new field was ignored.
     */
    default boolean addRuleIfNotExists(final String field, final Object data) {
        return addRuleIfNotExists(field, QueryOperatorEnum.EQUAL, data);
    }

    /**
     * @param field field
     * @param data data
     * @return <code>true</code> if field is a new field in the rules and value was set.
     *         <code>false</code> if field already exists in the rules and the new field was ignored.
     */
    default boolean addRuleIfNotExists(final String field, final QueryOperatorEnum op, final Object data) {
        if(isRuleExists(field)) {
            return Boolean.FALSE;
        }
        addRule(field, op, data);
        return Boolean.TRUE;
    }

    default void addRule(final String field, final QueryOperatorEnum operator, final Object data) {
        if (Objects.isNull(getRules())) {
            setRules(new LinkedHashSet<>());
        }
        getRules().add(new QueryRuleDTO(field, operator, data));
    }

    default void addEqualRule(final String field, final Object data) {
        addRule(field, QueryOperatorEnum.EQUAL, data);
    }

    default void addNotEqualRule(final String field, final Object data) {
        addRule(field, QueryOperatorEnum.NOT_EQUAL, data);
    }

    default void addBeginsWithRule(final String field, final Object data) {
        addRule(field, QueryOperatorEnum.BEGINS_WITH, data);
    }
}
