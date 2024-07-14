package com.manbo.common.util.exception;

import com.manbo.common.util.enums.HttpStatus;

/**
 * @author Manbo
 */
public interface ApplicationError {

    HttpStatus getHttpStatus();

    String getErrorCode();

    ApplicationException exception(Object... objects);

    boolean logError();

    default int getStatusCode() {
        return getHttpStatus().getValue();
    }

}
