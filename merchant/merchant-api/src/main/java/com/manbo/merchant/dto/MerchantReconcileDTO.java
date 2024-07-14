package com.manbo.merchant.dto;

import com.manbo.common.util.utils.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author manboyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantReconcileDTO implements Serializable {

    private Long merchantId;
    private String merchantName;
    private String merchantEmail;
    private List<MerchantAccountDTO> merchantAccounts;
    private List<SummaryPurchaseDTO> purchaseProducts;

    @Data
    public static class SummaryPurchaseDTO implements Serializable {
        private Long productId;
        private String productSku;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private List<PurchaseLogDTO> purchaseLogs;

        public Integer getTotalSoldQuantity() {
            if(CollectionUtils.isEmpty(purchaseLogs)) {
                return null;
            }

            return CollectionUtils.streamOf(purchaseLogs)
                .mapToInt(PurchaseLogDTO::getSoldQuantity)
                .sum();
        }

        public BigDecimal getTotalPurchasePrice() {
            if(CollectionUtils.isEmpty(purchaseLogs)) {
                return null;
            }

            return CollectionUtils.streamOf(purchaseLogs)
                .map(PurchaseLogDTO::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    @Data
    public static class PurchaseLogDTO implements Serializable {

        private Integer soldQuantity;
        private BigDecimal purchasePrice;
        private OffsetDateTime purchaseAt;
    }
}
