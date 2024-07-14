package com.manbo.user.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Data
public class UserRechargeDTO implements Serializable {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String currency;
    private BigDecimal balance;
}
