package com.manbo.merchant.dao;

import com.manbo.common.service.dao.BaseDAO;
import com.manbo.merchant.model.MerchantPurchaseLogDO;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author manboyu
 */
public interface MerchantPurchaseLogDAO extends BaseDAO<MerchantPurchaseLogDO, Long> {

    List<MerchantPurchaseLogDO> findByPurchaseAtBetween(OffsetDateTime start, OffsetDateTime end);
}
