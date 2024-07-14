package com.manbo.common.web.advice;

import com.manbo.common.util.dto.ResponseDTO;
import com.manbo.common.util.utils.MdcUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import static org.springdoc.core.utils.Constants.DEFAULT_API_DOCS_URL;

/**
 * @author Manbo
 */
@RestControllerAdvice
public class CommonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Value("${swagger.enable:false}")
    private Boolean swaggerEnabled;

    @Override
    public boolean supports(@NonNull final MethodParameter methodParameter, @NonNull final Class<? extends HttpMessageConverter<?>> converterType) {
        return MappingJackson2HttpMessageConverter.class.equals(converterType);
    }

    @Override
    public Object beforeBodyWrite(final Object returnValue,
                          @NonNull final MethodParameter returnType,
                          @NonNull final MediaType selectedContentType,
                          @NonNull final Class<? extends HttpMessageConverter<?>> selectedConverterType,
                          @NonNull final ServerHttpRequest request,
                          @NonNull final ServerHttpResponse response) {
        if (((ServletServerHttpResponse) response).getServletResponse().getStatus() != HttpStatus.OK.value()) {
            return returnValue;
        }

        final String requestUri = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
        if (swaggerEnabled && StringUtils.endsWithAny(requestUri, DEFAULT_API_DOCS_URL)) {
            return returnValue;
        }

        if (returnValue instanceof ResponseDTO<?>) {
            return returnValue;
        }

        return ResponseDTO.builder().success(Boolean.TRUE).traceId(MdcUtils.getTraceId()).data(returnValue).build();
    }
}
