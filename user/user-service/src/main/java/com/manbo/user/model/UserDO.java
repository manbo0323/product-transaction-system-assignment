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

/**
 * @author manboyu
 */
@Getter
@Setter
@Entity
@Table(name = "t_user")
@ToString(callSuper = true)
public class UserDO extends BaseDO {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommonStatusEnum status;

    public UserDO() {
        this.status = CommonStatusEnum.ACTIVE;
    }
}
