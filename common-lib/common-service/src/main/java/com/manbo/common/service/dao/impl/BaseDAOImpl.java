package com.manbo.common.service.dao.impl;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.common.util.dto.query.QueryObj;
import com.manbo.common.util.dto.query.QueryRuleDTO;
import com.manbo.common.util.enums.QueryGroupOperatorEnum;
import com.manbo.common.util.enums.QueryOperatorEnum;
import com.manbo.common.util.utils.CollectionUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.HQLTemplates;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.manbo.common.util.constant.StringConst.PERCENT;
import static com.manbo.common.util.constant.StringConst.WILD_CARD;

/**
 * @author Manbo
 */
@Slf4j
public class BaseDAOImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseDAO<T, ID> {

    /**
     * Pair<Operator, Boolean> 表示 QueryDsl Operator 以及 是否要另加反向條件 (not)
     */
    private static final Map<QueryOperatorEnum, Pair<Operator, Boolean>> OPERATORS = Map.ofEntries(
        Map.entry(QueryOperatorEnum.EQUAL, Pair.of(Ops.EQ, true)),
        Map.entry(QueryOperatorEnum.NOT_EQUAL, Pair.of(Ops.NE, true)),
        Map.entry(QueryOperatorEnum.CONTAINS, Pair.of(Ops.LIKE, true)),
        Map.entry(QueryOperatorEnum.NOT_CONTAINS, Pair.of(Ops.LIKE, false)),
        Map.entry(QueryOperatorEnum.LESS, Pair.of(Ops.LT, true)),
        Map.entry(QueryOperatorEnum.LESS_OR_EQUAL, Pair.of(Ops.LOE, true)),
        Map.entry(QueryOperatorEnum.GREATER, Pair.of(Ops.GT, true)),
        Map.entry(QueryOperatorEnum.GREATER_OR_EQUAL, Pair.of(Ops.GOE, true)),
        Map.entry(QueryOperatorEnum.IS_NULL, Pair.of(Ops.IS_NULL, true)),
        Map.entry(QueryOperatorEnum.IS_NOT_NULL, Pair.of(Ops.IS_NOT_NULL, true)),
        Map.entry(QueryOperatorEnum.IS_IN, Pair.of(Ops.IN, true)),
        Map.entry(QueryOperatorEnum.IS_NOT_IN, Pair.of(Ops.NOT_IN, true)),
        Map.entry(QueryOperatorEnum.BETWEEN, Pair.of(Ops.BETWEEN, true)),
        Map.entry(QueryOperatorEnum.BEGINS_WITH, Pair.of(Ops.STARTS_WITH, true)),
        Map.entry(QueryOperatorEnum.NOT_BEGIN_WITH, Pair.of(Ops.STARTS_WITH, false)),
        Map.entry(QueryOperatorEnum.END_WITH, Pair.of(Ops.ENDS_WITH, true)),
        Map.entry(QueryOperatorEnum.NOT_END_WITH, Pair.of(Ops.ENDS_WITH, false))
    );

    private final JpaEntityInformation<T, ID> entityInformation;
    private final JPAQueryFactory jpaQueryFactory;
    private final PathBuilder<T> pathBuilder;
    private final EntityManager entityManager;

    public BaseDAOImpl(final JpaEntityInformation<T, ID> entityInformation, final EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
        this.entityInformation = entityInformation;
        this.jpaQueryFactory = new JPAQueryFactory(HQLTemplates.DEFAULT, entityManager);

        final EntityPath<T> path = SimpleEntityPathResolver.INSTANCE.createPath(entityInformation.getJavaType());
        this.pathBuilder = new PathBuilder<>(path.getType(), path.getMetadata());
    }

    @Override
    public JPAQueryFactory getJPAQueryFactory() {
        return jpaQueryFactory;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public PathBuilder<T> getPathBuilder() {
        return pathBuilder;
    }

    @Override
    public PageRequest buildPageRequest(final QueryObj queryObj) {
        if (CollectionUtils.isEmpty(queryObj.getOrders())) {
            return PageRequest.of(queryObj.getPageNumber(), queryObj.getPageSize());
        }

        final Sort.Order[] orders = queryObj.getOrders().stream()
            .map(s -> new Sort.Order(Sort.Direction.fromString(s.getDirection().name()), s.getField()))
            .toArray(Sort.Order[]::new);

        return PageRequest.of(queryObj.getPageNumber(), queryObj.getPageSize(), Sort.by(orders));
    }

    @Override
    public Predicate buildPredicate(final QueryObj queryObj) {
        final BooleanBuilder predicateBuilder = new BooleanBuilder();
        final Set<QueryRuleDTO> rules = queryObj.getRules();
        if(CollectionUtils.isEmpty(rules)) {
            return predicateBuilder;
        }

        final Map<String, Field> doFields = getAllFields(entityInformation.getJavaType());
        final List<Predicate> predicates = new ArrayList<>();
        for(QueryRuleDTO ruleDTO : rules) {
            final Field field = doFields.get(ruleDTO.getField());
            if(Objects.isNull(field)) {
                continue;
            }
            if(WILD_CARD.equals(ruleDTO.getData())) {
                continue;
            }
            final Object data = getCondition(ruleDTO, field.getType());
            predicates.add(buildPredicate(ruleDTO.getField(), ruleDTO.getOp(), data));
        }

        if (queryObj.getGroupOp() == QueryGroupOperatorEnum.OR) {
            predicateBuilder.andAnyOf(predicates.toArray(Predicate[]::new));
        } else {
            predicates.forEach(predicateBuilder::and);
        }
        return predicateBuilder;
    }

    private Predicate buildPredicate(final String field, final QueryOperatorEnum operator, final Object data) {
        final Pair<Operator, Boolean> operatorData = OPERATORS.get(operator);
        final Predicate predicate = Expressions.predicate(operatorData.getLeft(), getPropertyParams(pathBuilder, field, data));
        if (operatorData.getRight()) {
            return predicate;
        } else {
            return predicate.not();
        }
    }

    private Expression[] getPropertyParams(final PathBuilder<T> entityPath, final String field, final Object data) {
        final Stream.Builder<Expression<Object>> sb = Stream.builder();
        sb.add(entityPath.get(field));
        if (Objects.nonNull(data)) {
            if (Object[].class.isAssignableFrom(data.getClass())) {
                Stream.of((Object[]) data).forEach(p -> sb.add(Expressions.constant(p)));
            } else {
                sb.add(Expressions.constant(data));
            }
        }
        return sb.build().toArray(Expression[]::new);
    }

    private Object getCondition(final QueryRuleDTO dto, final Class propertyType) {
        final Object condition = dto.getData();
        if(Objects.isNull(condition)) {
            return null;
        }

        if (dto.getOp() == QueryOperatorEnum.IS_IN || dto.getOp() == QueryOperatorEnum.IS_NOT_IN) {
            if(condition instanceof String str) {
                final List<String> conds = Stream.of(StringUtils.split(str, dto.getDelimiter())).filter(StringUtils::isNotBlank).map(String::trim).toList();
                if (!propertyType.isEnum()) {
                    return conds;
                }
                return conds.stream().map(con -> Enum.valueOf(propertyType, con)).collect(Collectors.toList());
            } else if (condition instanceof Object[] array) {
                return Arrays.stream(array).filter(Objects::nonNull).collect(Collectors.toList());
            } else if (condition instanceof Collection<?> collection) {
                return collection.stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
            return List.of(condition);
        }

        if ((dto.getOp() == QueryOperatorEnum.CONTAINS || dto.getOp() == QueryOperatorEnum.NOT_CONTAINS) && !condition.toString().contains(PERCENT)) {
            return StringUtils.wrapIfMissing(condition.toString(), PERCENT);
        }
        if (dto.getOp() == QueryOperatorEnum.BETWEEN) {
            if (OffsetDateTime.class.isAssignableFrom(propertyType)) {
                return Stream.of(StringUtils.split(condition.toString(), dto.getDelimiter()))
                        .map(OffsetDateTime::parse)
                        .toArray(OffsetDateTime[]::new);
            } else if (LocalDate.class.isAssignableFrom(propertyType)) {
                return Stream.of(StringUtils.split(condition.toString(), dto.getDelimiter())).map(LocalDate::parse).toArray(LocalDate[]::new);
            } else if (LocalDateTime.class.isAssignableFrom(propertyType)) {
                return Stream.of(StringUtils.split(condition.toString(), dto.getDelimiter())).map(dt -> LocalDateTime.parse(dt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).toArray(LocalDateTime[]::new);
            } else {
                return StringUtils.split(condition.toString(), dto.getDelimiter());
            }
        }
        if (StringUtils.containsAny(dto.getOp().name(), "EQUAL", "LESS", "GREATER")) {
            if (LocalDate.class.isAssignableFrom(propertyType)) {
                return LocalDate.parse(condition.toString());
            }
            if (LocalDateTime.class.isAssignableFrom(propertyType)) {
                return LocalDateTime.parse(condition.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (OffsetDateTime.class.isAssignableFrom(propertyType)) {
                return OffsetDateTime.parse(condition.toString());
            }
            if (propertyType.isEnum()) {
                return Enum.valueOf(propertyType, condition.toString());
            }
        }

        return condition;
    }

    private Map<String, Field> getAllFields(final Class<?> clazz) {
        final Map<String, Field> fieldMap = new HashMap<>(16);
        Class<?> current = clazz;
        do {
            for (final Field field : current.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
            }
        } while ((current = current.getSuperclass()) != null);
        return fieldMap;
    }

    @Override
    public T findOneOrThrowException(final Predicate predicate) {
        return findOne(predicate).orElseThrow();
    }

    @Override
    public T findByIdOrThrowException(final ID id) {
        return findById(id).orElseThrow();
    }

    @Override
    public Optional<T> findOne(final Predicate predicate) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(pathBuilder).where(predicate).fetchOne());
    }

    @Override
    public Page<T> findAll(final QueryObj queryObj) {
        final Predicate predicate = buildPredicate(queryObj);
        final PageRequest pageRequest = buildPageRequest(queryObj);
        return findAll(predicate, pageRequest);
    }

    @Override
    public Page<T> findAll(JPAQuery<T> jpaQuery, final QueryObj queryObj) {
        applySortingAndPaging(jpaQuery, queryObj);
        final QueryResults<T> queryResults = jpaQuery.fetchResults();
        return new PageImpl(queryResults.getResults(), PageRequest.of(queryObj.getPageNumber(), queryObj.getPageSize()), queryResults.getTotal());
    }

    @Override
    public List<T> findAll(final Predicate predicate) {
        return jpaQueryFactory.selectFrom(pathBuilder).where(predicate).fetch();
    }

    @Override
    public List<T> findAll(final Predicate predicate, final Sort sort) {
        return jpaQueryFactory.selectFrom(pathBuilder).where(predicate).orderBy(getOrderSpecifiers(sort)).fetch();
    }

    private OrderSpecifier[] getOrderSpecifiers(final Sort sort) {
        return CollectionUtils.streamOf(sort)
            .map(o -> new OrderSpecifier(Order.valueOf(o.getDirection().name()), getPathBuilder().get(o.getProperty())))
            .toArray(OrderSpecifier[]::new);
    }

    @Override
    public List<T> findAll(final Predicate predicate, final OrderSpecifier<?>... orders) {
        return jpaQueryFactory.selectFrom(pathBuilder).where(predicate).orderBy(orders).fetch();
    }

    @Override
    public List<T> findAll(final OrderSpecifier<?>... orders) {
        return jpaQueryFactory.selectFrom(pathBuilder).orderBy(orders).fetch();
    }

    @Override
    public Page<T> findAll(final Predicate predicate, final Pageable pageable) {
        final JPAQuery<T> jpaQuery = jpaQueryFactory.selectFrom(pathBuilder).where(predicate);

        Optional.of(pageable.getSort()).ifPresent(sort -> jpaQuery.orderBy(getOrderSpecifiers(sort)));

        final int pageSize = pageable.getPageSize();
        final int pageNumber = pageable.getPageNumber();
        final QueryResults<T> queryResults = jpaQuery.limit(pageSize).offset((long) pageSize * pageNumber).fetchResults();

        return new PageImpl<>(queryResults.getResults(), PageRequest.of(pageNumber, pageSize), queryResults.getTotal());
    }

    @Override
    public long count(final Predicate predicate) {
        return jpaQueryFactory.select(pathBuilder.count()).from(pathBuilder).where(predicate).fetchFirst();
    }

    @Override
    public boolean exists(final Predicate predicate) {
        return count(predicate) > 0;
    }

    @Override
    public boolean notExists(Predicate predicate) {
        return !exists(predicate);
    }

    @Override
    public boolean exists(QueryObj queryObj) {
        return exists(buildPredicate(queryObj));
    }

}
