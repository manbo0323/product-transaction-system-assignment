package com.manbo.merchant.model;

import com.manbo.common.service.model.BaseDO;
import com.manbo.common.util.enums.CommonStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "t_merchant_product")
@ToString(callSuper = true)
public class MerchantProductDO extends BaseDO {

    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(name = "sku")
    private String sku;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "currency")
    private String currency;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommonStatusEnum status;

    public MerchantProductDO() {
        this.status = CommonStatusEnum.ACTIVE;
    }
}
