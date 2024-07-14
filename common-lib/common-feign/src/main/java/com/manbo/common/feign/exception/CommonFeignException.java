package com.manbo.common.feign.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Manbo
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommonFeignException extends RuntimeException {

    private int statusCode;

    private String code;

    private String methodKey;

    @Builder
    public CommonFeignException(int statusCode, String code, String message, String methodKey) {
        super(message);
        this.statusCode = statusCode;
        this.code = code;
        this.methodKey = methodKey;
    }

}
