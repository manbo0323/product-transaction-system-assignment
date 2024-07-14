package com.manbo.common.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * @author Manbo
 */
@Data
@FieldNameConstants
public class BaseDTO implements Serializable {

    protected Long id;

    @Schema(description = "Create datetime", accessMode = Schema.AccessMode.READ_ONLY)
    protected OffsetDateTime createdAt;

    @Schema(description = "Update datetime", accessMode = Schema.AccessMode.READ_ONLY)
    protected OffsetDateTime updatedAt;
}
