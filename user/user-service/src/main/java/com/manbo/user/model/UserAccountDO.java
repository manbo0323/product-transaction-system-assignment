package com.manbo.user.model;

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
@Table(name = "t_user_account")
@ToString(callSuper = true)
public class UserAccountDO extends BaseDO {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "currency")
    private String currency;

    @Column(name = "balance")
    private BigDecimal balance;

    public UserAccountDO() {
        this.currency = "USD";
    }
}
