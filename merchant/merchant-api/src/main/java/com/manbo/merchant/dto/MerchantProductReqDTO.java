package com.manbo.merchant.dto;

import com.manbo.common.util.enums.CommonStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Data
public class MerchantProductReqDTO implements Serializable {

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String currency;

    @NotNull
    private Integer quantity;
}
