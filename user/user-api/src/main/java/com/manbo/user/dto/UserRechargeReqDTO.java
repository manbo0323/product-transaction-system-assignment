package com.manbo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UserRechargeReqDTO implements Serializable {

    @NotBlank
    private String currency;

    @NotNull
    private BigDecimal amount;
}
