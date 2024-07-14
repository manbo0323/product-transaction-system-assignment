package com.manbo.user.dto;

import com.manbo.common.util.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author manboyu
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccountDTO extends BaseDTO {

    private Long userId;
    private String currency;
    private BigDecimal balance;
}
