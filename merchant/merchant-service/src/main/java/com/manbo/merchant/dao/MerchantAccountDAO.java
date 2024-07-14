package com.manbo.merchant.dao;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.merchant.dto.MerchantAccountDTO;
import com.manbo.merchant.model.MerchantAccountDO;
import com.manbo.merchant.model.QMerchantAccountDO;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

/**
 * @author manboyu
 */
public interface MerchantAccountDAO extends BaseDAO<MerchantAccountDO, Long> {

    default List<MerchantAccountDTO> findByMerchantIds(List<Long> merchantIds) {
        final QMerchantAccountDO merchantAccountDO = QMerchantAccountDO.merchantAccountDO;
        return getJPAQueryFactory()
            .select(Projections.fields(MerchantAccountDTO.class, entityExpressions(merchantAccountDO).toArray(new Expression[0])))
            .from(merchantAccountDO)
            .where(merchantAccountDO.merchantId.in(merchantIds))
            .fetch();
    }

    default Optional<MerchantAccountDO> lockByMerchantIdAndCurrency(Long merchantId, String currency) {
        final QMerchantAccountDO merchantAccountDO = QMerchantAccountDO.merchantAccountDO;
        return Optional.ofNullable(
            getJPAQueryFactory()
                .selectFrom(merchantAccountDO)
                .where(
                    merchantAccountDO.merchantId.eq(merchantId),
                    merchantAccountDO.currency.eq(currency)
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne()
        );
    }
}
