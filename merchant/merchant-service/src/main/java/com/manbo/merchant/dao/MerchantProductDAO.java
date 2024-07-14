package com.manbo.merchant.dao;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.common.util.dto.query.QueryObj;
import com.manbo.common.util.enums.CommonStatusEnum;
import com.manbo.merchant.dto.MerchantProductDTO;
import com.manbo.merchant.dto.MerchantProductDTO.SimpleMerchantDTO;
import com.manbo.merchant.model.MerchantProductDO;
import com.manbo.merchant.model.QMerchantDO;
import com.manbo.merchant.model.QMerchantProductDO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static com.manbo.common.util.constant.StringConst.EMPTY;
import static com.manbo.common.util.constant.StringConst.PERCENT;

/**
 * @author manboyu
 */
public interface MerchantProductDAO extends BaseDAO<MerchantProductDO, Long> {

    Optional<MerchantProductDO> findByMerchantIdAndSkuAndStatus(Long merchantId, String sku, CommonStatusEnum status);

    List<MerchantProductDO> findByIdIn(List<Long> ids);

    default Page<MerchantProductDTO> findProducts(QueryObj queryDTO) {
        final QMerchantDO merchantDO = QMerchantDO.merchantDO;
        final QMerchantProductDO merchantProductDO = QMerchantProductDO.merchantProductDO;
        final BooleanBuilder predicate = new BooleanBuilder();

        final String keyword = Optional.ofNullable(queryDTO.getKeyword())
            .filter(StringUtils::isNotBlank)
            .map(k -> StringUtils.wrapIfMissing(k, PERCENT))
            .orElse(EMPTY);

        if (StringUtils.isNotBlank(keyword)) {
            predicate.and(merchantProductDO.name.like(keyword));
        }

        predicate.and(buildPredicate(queryDTO));

        final List<Expression<?>> expressions = entityExpressions(merchantProductDO);
        expressions.add(Projections.fields(SimpleMerchantDTO.class, merchantDO.id, merchantDO.name, merchantDO.email).as(MerchantProductDTO.Fields.merchant));

        final JPAQuery<MerchantProductDTO> jpaQuery = getJPAQueryFactory()
            .select(Projections.fields(MerchantProductDTO.class, expressions.toArray(new Expression[0])))
            .from(merchantProductDO)
            .join(merchantDO).on(merchantDO.id.eq(merchantProductDO.merchantId))
            .where(predicate);

        return buildPage(jpaQuery, queryDTO);
    }

    default Long subtractQuantity(Long id, Integer quantity) {
        final QMerchantProductDO merchantProductDO = QMerchantProductDO.merchantProductDO;
        return getJPAQueryFactory()
            .update(merchantProductDO)
            .set(merchantProductDO.quantity, merchantProductDO.quantity.subtract(quantity))
            .set(merchantProductDO.updatedAt, OffsetDateTime.now())
            .where(merchantProductDO.id.eq(id))
            .execute();
    }
}
