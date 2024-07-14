package com.manbo.user.exception;

import com.manbo.common.util.enums.HttpStatus;
import com.manbo.common.util.exception.ApplicationError;
import com.manbo.common.util.exception.ApplicationException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author manboyu
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorEnum implements ApplicationError {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.notFound"),

    USER_INACTIVE(HttpStatus.BAD_REQUEST, "error.user.inactive"),

    USER_CURRENCY_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.currency.notFound"),

    INSUFFICIENT_PRODUCT_INVENTORY(HttpStatus.BAD_REQUEST, "error.user.purchase.insufficientInventory"),

    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "error.user.purchase.insufficientBalance");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final boolean logError;

    UserErrorEnum(final HttpStatus httpStatus, final String errorCode) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.logError = Boolean.FALSE;
    }

    @Override
    public ApplicationException exception(Object... objects) {
        return new ApplicationException(this, objects);
    }

    @Override
    public boolean logError() {
        return this.logError;
    }
}
