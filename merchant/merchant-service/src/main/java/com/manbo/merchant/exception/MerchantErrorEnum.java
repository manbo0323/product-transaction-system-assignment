package com.manbo.merchant.exception;

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
public enum MerchantErrorEnum implements ApplicationError {

    MERCHANT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.merchant.notFound"),

    MERCHANT_ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.merchant.account.notFound"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.merchant.product.notFound"),

    MERCHANT_ACCOUNT_BALANCE_FAILED(HttpStatus.BAD_REQUEST, "error.merchant.accountBalance"),

    MERCHANT_PRODUCT_QUANTITY_FAILED(HttpStatus.BAD_REQUEST, "error.merchant.productQuantity");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final boolean logError;

    MerchantErrorEnum(final HttpStatus httpStatus, final String errorCode) {
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
