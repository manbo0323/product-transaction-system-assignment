package com.manbo.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPurchaseDTO implements Serializable {

    private Long userId;
    private String name;
    private String email;
    private String currency;
    private BigDecimal balance;
    private BigDecimal purchasePrice;
    private String productName;
}
