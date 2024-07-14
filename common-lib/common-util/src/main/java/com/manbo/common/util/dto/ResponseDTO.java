package com.manbo.common.util.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Manbo
 * @param <T>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> implements Serializable {

    private Boolean success;

    private String traceId;

    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    public ResponseDTO(final T data) {
        this.data = data;
        this.success = Boolean.TRUE;
    }

    public ResponseDTO(final String code, final String message) {
        this.code = code;
        this.message = message;
        this.success = Boolean.FALSE;
    }

    public ResponseDTO(final String code, final String traceId, final String message) {
        this.code = code;
        this.message = message;
        this.traceId = traceId;
        this.success = Boolean.FALSE;
    }

    public ResponseDTO<T> setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }
}
