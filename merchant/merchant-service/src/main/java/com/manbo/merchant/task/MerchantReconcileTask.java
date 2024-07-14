package com.manbo.merchant.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.manbo.common.util.utils.CollectionUtils;
import com.manbo.merchant.dao.MerchantAccountDAO;
import com.manbo.merchant.dao.MerchantDAO;
import com.manbo.merchant.dao.MerchantProductDAO;
import com.manbo.merchant.dao.MerchantPurchaseLogDAO;
import com.manbo.merchant.dto.MerchantAccountDTO;
import com.manbo.merchant.dto.MerchantReconcileDTO;
import com.manbo.merchant.dto.MerchantReconcileDTO.SummaryPurchaseDTO;
import com.manbo.merchant.model.MerchantDO;
import com.manbo.merchant.model.MerchantProductDO;
import com.manbo.merchant.model.MerchantPurchaseLogDO;
import com.manbo.merchant.model.converter.MerchantConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author manboyu
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "task.merchant-reconciliation.switch", havingValue = "true", matchIfMissing = true)
public class MerchantReconcileTask {

    private final MerchantDAO merchantDAO;
    private final MerchantAccountDAO merchantAccountDAO;
    private final MerchantProductDAO merchantProductDAO;
    private final MerchantPurchaseLogDAO merchantPurchaseLogDAO;
    private final MerchantConverter merchantConverter;

    private final ObjectMapper objectMapper;

    @Scheduled(cron = "${task.merchant-reconciliation.cron}")
    public void reconcileDaily() throws JsonProcessingException {
        final OffsetDateTime yesterday = OffsetDateTime.now().minusDays(1);
        OffsetDateTime start = yesterday.with(OffsetTime.MIN);
        OffsetDateTime end = yesterday.with(OffsetTime.MAX);

        final List<MerchantPurchaseLogDO> purchaseLogs = merchantPurchaseLogDAO.findAll();// merchantPurchaseLogDAO.findByPurchaseAtBetween(start, end);
        if (CollectionUtils.isEmpty(purchaseLogs)) {
            return;
        }

        final List<Long> merchantIds = CollectionUtils.streamOf(purchaseLogs).map(MerchantPurchaseLogDO::getMerchantId).distinct().toList();
        final List<Long> productIds = CollectionUtils.streamOf(purchaseLogs).map(MerchantPurchaseLogDO::getProductId).distinct().toList();

        final Map<Long, MerchantDO> merchantMap = CollectionUtils.streamOf(merchantDAO.findByIdIn(merchantIds))
            .collect(Collectors.toMap(MerchantDO::getId, Function.identity()));
        final Map<Long, List<MerchantAccountDTO>> merchantAccountGroup = CollectionUtils.streamOf(merchantAccountDAO.findByMerchantIds(merchantIds))
            .collect(Collectors.groupingBy(MerchantAccountDTO::getMerchantId));
        final Map<Long, MerchantProductDO> productMap = CollectionUtils.streamOf(merchantProductDAO.findByIdIn(productIds))
            .collect(Collectors.toMap(MerchantProductDO::getId, Function.identity()));

        final Map<Long, List<MerchantPurchaseLogDO>> merchantPurchaseLogGroup = CollectionUtils.streamOf(purchaseLogs)
            .collect(Collectors.groupingBy(MerchantPurchaseLogDO::getMerchantId));
        final List<MerchantReconcileDTO> result = Lists.newArrayListWithCapacity(merchantPurchaseLogGroup.size());

        merchantPurchaseLogGroup.forEach((merchantId, purchaseLogList) -> {
            final MerchantReconcileDTO reconcileDTO = merchantConverter.toReconcileDTO(merchantMap.get(merchantId));
            reconcileDTO.setMerchantAccounts(merchantAccountGroup.get(merchantId));

            final Map<Long, List<MerchantPurchaseLogDO>> productPurchaseLogGroup = CollectionUtils.streamOf(purchaseLogList)
                .collect(Collectors.groupingBy(MerchantPurchaseLogDO::getProductId));

            final List<SummaryPurchaseDTO> purchaseProducts = Lists.newArrayList();
            productPurchaseLogGroup.forEach((productId, productPurchaseList) -> {
                final SummaryPurchaseDTO summaryPurchaseDTO = merchantConverter.toSummaryPurchaseDTO(productMap.get(productId));
                summaryPurchaseDTO.setPurchaseLogs(
                    CollectionUtils.streamOf(productPurchaseList)
                        .map(merchantConverter::toPurchaseLogDTO)
                        .toList()
                );
                purchaseProducts.add(summaryPurchaseDTO);
            });
            reconcileDTO.setPurchaseProducts(purchaseProducts);

            result.add(reconcileDTO);
        });

        log.info("[MerchantReconcileTask] Daily Reconciliation Result - {}", objectMapper.writeValueAsString(result));
    }
}
