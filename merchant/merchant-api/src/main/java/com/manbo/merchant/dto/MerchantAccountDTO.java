package com.manbo.merchant.dto;

import com.manbo.common.util.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantAccountDTO extends BaseDTO {

    private Long merchantId;
    private String currency;
    private BigDecimal balance;
}
