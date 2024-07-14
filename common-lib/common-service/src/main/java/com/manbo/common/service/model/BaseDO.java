package com.manbo.common.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

/**
 * @author kai
 */
@Getter
@Setter
@MappedSuperclass
@ToString(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public class BaseDO extends BaseIdDO {

    @CreatedDate
    @Column(name = "created_at")
    protected OffsetDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected OffsetDateTime updatedAt;
}
