package com.manbo.merchant.model;

import com.manbo.common.service.model.BaseIdDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * @author manboyu
 */
@Getter
@Setter
@Entity
@Table(name = "t_merchant_purchase_log")
@ToString(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class MerchantPurchaseLogDO extends BaseIdDO {

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sold_quantity")
    private Integer soldQuantity;

    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    @Column(name = "currency")
    private String currency;

    @CreatedDate
    @Column(name = "purchase_at")
    private OffsetDateTime purchaseAt;

    public MerchantPurchaseLogDO() {
        this.currency = "USD";
    }

    @Builder
    public MerchantPurchaseLogDO(final Long merchantId, final Long productId, final Integer soldQuantity, final BigDecimal purchasePrice, final String currency) {
        this();
        this.merchantId = merchantId;
        this.productId = productId;
        this.soldQuantity = soldQuantity;
        this.purchasePrice = purchasePrice;
        this.currency = currency;
    }
}
