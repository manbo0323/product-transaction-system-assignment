package com.manbo.common.util.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Manbo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationException extends RuntimeException {

    private final ApplicationError error;
    private final Object[] args;

    public ApplicationException(final ApplicationError error) {
        super(error.getErrorCode());
        this.error = error;
        this.args = null;
    }

    public ApplicationException(final String error) {
        super(error);
        this.error = null;
        this.args = null;
    }

    public ApplicationException(final ApplicationError error, final Object[] objects) {
        super(error.getErrorCode());
        this.error = error;
        this.args = objects;
    }

    public ApplicationException(final ApplicationError error, final String errorMsg, final Object[] objects) {
        super(errorMsg);
        this.error = error;
        this.args = objects;
    }

}
