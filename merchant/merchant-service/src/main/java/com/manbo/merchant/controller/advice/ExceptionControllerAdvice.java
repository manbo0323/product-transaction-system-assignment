package com.manbo.merchant.controller.advice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.manbo.common.feign.exception.CommonFeignException;
import com.manbo.common.util.constant.StringConst;
import com.manbo.common.util.dto.ResponseDTO;
import com.manbo.common.util.exception.ApplicationError;
import com.manbo.common.util.exception.ApplicationException;
import com.manbo.common.util.exception.CommonErrorEnum;
import com.manbo.common.util.utils.CollectionUtils;
import com.manbo.common.util.utils.MdcUtils;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * wrap error response
 *
 * @author Manbo
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final MethodArgumentNotValidException exception) {
        final String violationMessage = CollectionUtils.streamOf(exception.getBindingResult().getFieldErrors())
            .map(fieldError -> fieldError.getField() + StringConst.COLON + fieldError.getDefaultMessage())
            .collect(Collectors.joining(StringConst.COMMA));
        return buildErrorResponse(CommonErrorEnum.INVALID_PARAM, violationMessage);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final ConstraintViolationException exception) {
        final String violationMessage = CollectionUtils.streamOf(exception.getConstraintViolations())
            .map(violation -> violation.getPropertyPath() + StringConst.COLON + violation.getMessage())
            .collect(Collectors.joining(StringConst.COMMA));
        return buildErrorResponse(CommonErrorEnum.INVALID_PARAM, violationMessage);
    }

    @ExceptionHandler(CommonFeignException.class)
    public ResponseEntity<ResponseDTO<Void>> executionException(final CommonFeignException e) {
        final HttpStatus httpStatus = Optional.ofNullable(HttpStatus.resolve(e.getStatusCode()))
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        if(httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            log.error("[CommonFeignException] code:{}, method:{}", e.getCode(), e.getMethodKey(), e);
        } else {
            log.error("[CommonFeignException] code:{}, method:{}, message:{}", e.getCode(), e.getMethodKey(), e.getMessage());
        }
        return buildErrorResponse(httpStatus, e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final ApplicationException e) {
        final ApplicationError error = e.getError();
        if (Objects.isNull(error) || error.logError()) {
            log.error("[ApplicationException]", e);
        }
        if (Objects.isNull(error)) {
            return buildErrorResponse(CommonErrorEnum.INTERNAL_ERROR, e.getMessage());
        } else {
             return buildErrorResponse(error.getHttpStatus().getValue(), error.getErrorCode(), e.getMessage());
        }
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final SQLException e) {
        log.error("[SQLException]", e);
        return buildErrorResponse(CommonErrorEnum.INTERNAL_ERROR, "Something went wrong while accessing data. ");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final DataAccessException e) {
        log.error("[DataAccessException]", e);
        return buildErrorResponse(CommonErrorEnum.INTERNAL_ERROR, "Something went wrong while accessing data. ");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final NoSuchElementException e) {
        return buildErrorResponse(CommonErrorEnum.DATA_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final HttpMessageNotReadableException e) {
        final StringBuilder message = new StringBuilder();
        if(e.getRootCause() instanceof InvalidFormatException exception) {
            message.append(exception.getPathReference()).append(" got invalid value: ").append(exception.getValue());
        } else {
            message.append(e.getMessage());
        }
        return buildErrorResponse(CommonErrorEnum.INVALID_PARAM, message.toString());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Void>> exception(final Exception e) {
        log.error("[Exception]", e);
        return buildErrorResponse(CommonErrorEnum.INTERNAL_ERROR, e.getMessage());
    }

    private ResponseEntity<ResponseDTO<Void>> buildErrorResponse(final ApplicationError errorEnum, final String message) {
        final HttpStatus status = Optional.ofNullable(HttpStatus.resolve(errorEnum.getStatusCode())).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(status, errorEnum.getErrorCode(), message);
    }

    private ResponseEntity<ResponseDTO<Void>> buildErrorResponse(final Integer statusCode, final String code, final String message) {
        final HttpStatus status = Optional.ofNullable(HttpStatus.resolve(statusCode)).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return buildErrorResponse(status, code, message);
    }

    private ResponseEntity<ResponseDTO<Void>> buildErrorResponse(final HttpStatus httpStatus, final String code, final String message) {
        return ResponseEntity
            .status(httpStatus)
            .body(new ResponseDTO<>(code, MdcUtils.getTraceId(), message));
    }

}
