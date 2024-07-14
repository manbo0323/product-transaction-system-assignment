package com.manbo.common.util.exception;

import com.manbo.common.util.enums.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Manbo
 */
@Getter
@RequiredArgsConstructor
public enum CommonErrorEnum implements ApplicationError {

    /**
     * internal error
     */
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal", Boolean.TRUE),

    /**
     * Data Not Found
     */
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "error.data.notFound"),

    /**
     * duplicate data
     */
    DUPLICATE_DATA(HttpStatus.BAD_REQUEST, "error.duplicate.data"),

    /**
     * '{param}' is not valid param
     */
    INVALID_PARAM(HttpStatus.BAD_REQUEST, "error.invalid.param");

    private static final Map<String, CommonErrorEnum> ENUMS = Stream.of(values()).collect(Collectors.toUnmodifiableMap(CommonErrorEnum::name, Function.identity()));

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final boolean logError;

    CommonErrorEnum(final HttpStatus httpStatus, final String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.logError = Boolean.FALSE;
    }

    @Override
    public boolean logError() {
        return this.logError;
    }

    @Override
    public ApplicationException exception(final Object... objects) {
        return new ApplicationException(this, objects);
    }

    public static Optional<CommonErrorEnum> lookup(final String name) {
        return Optional.ofNullable(ENUMS.get(name));
    }
}
