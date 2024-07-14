package com.manbo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author manboyu
 */
@Data
@Builder
@AllArgsConstructor
public class PurchaseReqDTO implements Serializable {

    @NotNull
    private Long merchantId;

    @NotBlank
    private String sku;

    @NotNull
    private Integer quantity;

    private String currency;

    public PurchaseReqDTO() {
        this.currency = "USD";
    }
}
