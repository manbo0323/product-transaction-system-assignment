package com.manbo.common.service.dao;

import com.manbo.common.util.dto.query.QueryObj;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Manbo
 */
@NoRepositoryBean
public interface BaseDAO<T, ID> extends JpaRepository<T, ID> {

    /**
     * 依照動態條件查詢資料是否存在
     *
     * @param queryObj
     * @return
     */
    boolean exists(QueryObj queryObj);

    /**
     * 依條件查詢單筆資料，無資料則拋出Exception
     *
     * @param predicate
     * @return
     */
    T findOneOrThrowException(Predicate predicate);

    /**
     * 依照id查詢資料，無資料則拋出Exception
     *
     * @param id
     * @return
     */
    T findByIdOrThrowException(ID id);


    /**
     * get jpa query factory
     *
     * @return
     */
    JPAQueryFactory getJPAQueryFactory();

    /**
     * get entity manager
     *
     * @return
     */
    EntityManager getEntityManager();

    /**
     * get path builder
     *
     * @return
     */
    PathBuilder<T> getPathBuilder();

    /**
     * build predicate
     *
     * @param queryObj
     * @return
     */
    Predicate buildPredicate(QueryObj queryObj);

    /**
     * build page request
     *
     * @param queryObj
     * @return
     */
    PageRequest buildPageRequest(QueryObj queryObj);

    /**
     * Returns a single entity matching the given {@link Predicate} or {@link Optional#empty()} if none was found.
     *
     * @param predicate must not be {@literal null}.
     * @return a single entity matching the given {@link Predicate} or {@link Optional#empty()} if none was found.
     * @throws org.springframework.dao.IncorrectResultSizeDataAccessException if the predicate yields more than one
     *                                                                        result.
     */
    Optional<T> findOne(Predicate predicate);

    /**
     * 動態條件查詢、排序資料
     *
     * @param queryObj
     * @return
     */
    Page<T> findAll(QueryObj queryObj);


    /**
     * 依照組好的jpaQuery, 套用排序以及分頁條件作查詢
     *
     * @param jpaQuery
     * @param queryObj
     * @return
     */
    Page<T> findAll(JPAQuery<T> jpaQuery, QueryObj queryObj);

    /**
     * Returns all entities matching the given {@link Predicate}. In case no match could be found an empty
     * {@link Iterable} is returned.
     *
     * @param predicate must not be {@literal null}.
     * @return all entities matching the given {@link Predicate}.
     */
    List<T> findAll(Predicate predicate);

    /**
     * Returns all entities matching the given {@link Predicate} applying the given {@link Sort}. In case no match could
     * be found an empty {@link Iterable} is returned.
     *
     * @param predicate must not be {@literal null}.
     * @param sort      the {@link Sort} specification to sort the results by, may be {@link Sort#empty()}, must not be
     *                  {@literal null}.
     * @return all entities matching the given {@link Predicate}.
     * @since 1.10
     */
    List<T> findAll(Predicate predicate, Sort sort);

    /**
     * Returns all entities matching the given {@link Predicate} applying the given {@link OrderSpecifier}s. In case no
     * match could be found an empty {@link Iterable} is returned.
     *
     * @param predicate must not be {@literal null}.
     * @param orders    the {@link OrderSpecifier}s to sort the results by.
     * @return all entities matching the given {@link Predicate} applying the given {@link OrderSpecifier}s.
     */
    List<T> findAll(Predicate predicate, OrderSpecifier<?>... orders);

    /**
     * Returns all entities ordered by the given {@link OrderSpecifier}s.
     *
     * @param orders the {@link OrderSpecifier}s to sort the results by.
     * @return all entities ordered by the given {@link OrderSpecifier}s.
     */
    List<T> findAll(OrderSpecifier<?>... orders);

    /**
     * Returns a {@link Page} of entities matching the given {@link Predicate}. In case no match could be found, an empty
     * {@link Page} is returned.
     *
     * @param predicate must not be {@literal null}.
     * @param pageable  may be {@link Pageable#unpaged()}, must not be {@literal null}.
     * @return a {@link Page} of entities matching the given {@link Predicate}.
     */
    Page<T> findAll(Predicate predicate, Pageable pageable);

    /**
     * Returns the number of instances matching the given {@link Predicate}.
     *
     * @param predicate the {@link Predicate} to count instances for, must not be {@literal null}.
     * @return the number of instances matching the {@link Predicate}.
     */
    long count(Predicate predicate);

    /**
     * Checks whether the data store contains elements that match the given {@link Predicate}.
     *
     * @param predicate the {@link Predicate} to use for the existence check, must not be {@literal null}.
     * @return {@literal true} if the data store contains elements that match the given {@link Predicate}.
     */
    boolean exists(Predicate predicate);

    boolean notExists(Predicate predicate);

    default void applySortingAndPaging(final JPAQuery<T> query, final QueryObj queryObj) {
        final var pathBuilder = getPathBuilder();
        Optional.ofNullable(queryObj.getOrders())
            .map(sorts -> sorts.stream()
                .map(s -> new OrderSpecifier(Order.valueOf(s.getDirection().name()), pathBuilder.get(s.getField())))
                .toArray(OrderSpecifier[]::new)
            ).ifPresent(query::orderBy);

        final int pageSize = queryObj.getPageSize();
        final int pageNumber = queryObj.getPageNumber();
        query.limit(pageSize).offset((long) pageSize * pageNumber);
    }

    /**
     * Build Page Data
     *
     * @param jpaQuery jpaQuery
     * @param queryObj queryObj
     * @return Page<E>
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    default <E> Page<E> buildPage(final JPAQuery<E> jpaQuery, final QueryObj queryObj) {
        Optional.ofNullable(queryObj.getOrders())
            .map(sort -> sort.stream()
                .map(o -> new OrderSpecifier(Order.valueOf(o.getDirection().name()), getPathBuilder().get(o.getField())))
                .toArray(OrderSpecifier[]::new))
            .ifPresent(jpaQuery::orderBy);

        final int pageSize = queryObj.getPageSize();
        final int pageNumber = queryObj.getPageNumber();
        final QueryResults<E> queryResults = jpaQuery.limit(pageSize).offset((long) pageSize * pageNumber).fetchResults();
        return new PageImpl<>(queryResults.getResults(), PageRequest.of(pageNumber, pageSize), queryResults.getTotal());
    }

    /**
     * all entity expressions from qBean
     *
     * @param qMainBean EntityPathBase<?>
     * @param excludeFields excludeFields
     * @return List<Expression < ?>>
     */
    default List<Expression<?>> entityExpressions(final EntityPathBase<?> qMainBean, String... excludeFields) {
        return Arrays.stream(qMainBean.getClass().getDeclaredFields())
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .filter(field -> !"_super".equals(field.getName()))
            .filter(field -> Arrays.stream(excludeFields).noneMatch(excludeField -> StringUtils.equals(excludeField, field.getName())))
            .map(field -> {
                try {
                    return (Expression<?>) field.get(qMainBean);
                } catch (final Exception e) {
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}
