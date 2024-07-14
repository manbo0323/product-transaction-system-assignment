package com.manbo.merchant.dto;

import com.manbo.common.util.dto.BaseDTO;
import com.manbo.common.util.enums.CommonStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Data
@FieldNameConstants
@EqualsAndHashCode(callSuper = true)
public class MerchantProductDTO extends BaseDTO {

    private String sku;
    private String name;
    private BigDecimal price;
    private String currency;
    private Integer quantity;
    private CommonStatusEnum status;
    private SimpleMerchantDTO merchant;

    @Data
    public static class SimpleMerchantDTO implements Serializable {

        private Long id;
        private String name;
        private String email;
    }
}
