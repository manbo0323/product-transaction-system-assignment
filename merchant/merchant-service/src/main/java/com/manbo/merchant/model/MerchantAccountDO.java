package com.manbo.merchant.model;

import com.manbo.common.service.model.BaseDO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Getter
@Setter
@Entity
@Table(name = "t_merchant_account")
@ToString(callSuper = true)
public class MerchantAccountDO extends BaseDO {

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "balance")
    private BigDecimal balance;

    public MerchantAccountDO() {
        this.currency = "USD";
    }
}
