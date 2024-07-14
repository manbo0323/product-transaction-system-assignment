package com.manbo.merchant.dao;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.common.util.dto.query.QueryObj;
import com.manbo.merchant.dto.MerchantListDTO;
import com.manbo.merchant.model.MerchantDO;
import com.manbo.merchant.model.QMerchantDO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

import static com.manbo.common.util.constant.StringConst.EMPTY;
import static com.manbo.common.util.constant.StringConst.PERCENT;

/**
 * @author manboyu
 */
public interface MerchantDAO extends BaseDAO<MerchantDO, Long> {

    List<MerchantDO> findByIdIn(List<Long> merchantIds);

    default Page<MerchantListDTO> findMerchants(QueryObj queryDTO) {
        final QMerchantDO merchantDO = QMerchantDO.merchantDO;
        final BooleanBuilder predicate = new BooleanBuilder();

        final String keyword = Optional.ofNullable(queryDTO.getKeyword())
            .filter(StringUtils::isNotBlank)
            .map(k -> StringUtils.wrapIfMissing(k, PERCENT))
            .orElse(EMPTY);

        if (StringUtils.isNotBlank(keyword)) {
            predicate.and(merchantDO.name.like(keyword));
        }

        predicate.and(buildPredicate(queryDTO));

        final JPAQuery<MerchantListDTO> jpaQuery = getJPAQueryFactory()
            .select(Projections.fields(MerchantListDTO.class,
                merchantDO.id, merchantDO.name, merchantDO.email,
                merchantDO.status, merchantDO.createdAt, merchantDO.updatedAt))
            .from(merchantDO)
            .where(predicate);
        return buildPage(jpaQuery, queryDTO);
    }
}
