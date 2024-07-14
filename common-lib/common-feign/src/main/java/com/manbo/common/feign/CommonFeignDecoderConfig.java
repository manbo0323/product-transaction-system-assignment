package com.manbo.common.feign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.manbo.common.feign.exception.CommonFeignException;
import com.manbo.common.util.dto.ResponseDTO;
import feign.Response;
import feign.Util;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.GenericTypeResolver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static feign.FeignException.errorStatus;

/**
 * @author Manbo
 */
@Slf4j
public class CommonFeignDecoderConfig {

    @Bean
    @ConditionalOnMissingBean(name = "objectMapper")
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    @Bean
    public ErrorDecoder errorDecoder(final ObjectMapper objectMapper) {
        return (final String methodKey, final Response response) -> {
            final Response.Body body = response.body();
            if (Objects.isNull(body)) {
                log.warn("[ErrorDecoder] call api got error, request url:{}, response status:{}", response.request().url(), response.status());
                return errorStatus(methodKey, response);
            }

            final ResponseDTO<JsonNode> responseDTO = parseValue(objectMapper, body);
            log.warn("[ErrorDecoder] call api got error, request url:{}, response status:{}, code:{}, message:{}", response.request().url(), response.status(), responseDTO.getCode(), responseDTO.getMessage());
            return CommonFeignException.builder()
                .statusCode(response.status())
                .code(responseDTO.getCode())
                .message(responseDTO.getMessage())
                .methodKey(methodKey)
                .build();
        };
    }

    private ResponseDTO<JsonNode> parseValue(final ObjectMapper objectMapper, final Response.Body body) {
        try (final InputStream inputStream = body.asInputStream()) {
            final String responseBody = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            try {
                return objectMapper.readValue(responseBody, new TypeReference<ResponseDTO<JsonNode>>() {});
            } catch (final Exception e) {
                log.error("[FeignDecoder] Json parse error! responseBody:{}", responseBody, e);
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            log.error("[FeignDecoder] Get response body got error", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    public Decoder responseDecoder(final ObjectMapper objectMapper) {
        return (final Response response, final Type type) -> {
            final Response.Body body = response.body();
            if (Objects.isNull(body)) {
                return null;
            }

            if (String.class.equals(type)) {
                return Util.toString(body.asReader(Util.UTF_8));
            }

            if (!(type instanceof ParameterizedType) && !(type instanceof Class)) {
                throw new IllegalArgumentException("[FeignDecoder] type:" + type.getTypeName() + " is unsupported");
            }

            final ResponseDTO<JsonNode> responseDTO = parseValue(objectMapper, body);
            log.debug("[FeignDecoder] request url:{}, success:{}, code:{}, message:{}", response.request().url(), responseDTO.getSuccess(), responseDTO.getCode(), responseDTO.getMessage());
            final JsonNode data = responseDTO.getData();
            if (Objects.isNull(data)) {
                return null;
            }

            if (type instanceof Class) {
                return objectMapper.readValue(data.traverse(), (Class<?>) type);
            }

            /*
             * 另外一種做法: objectMapper.convertValue(data, javaType);
             * 但實測起來 readValue 比 convertValue 還要快得多
             */
            final JavaType javaType = objectMapper.constructType(GenericTypeResolver.resolveType(type, (Class<?>) null));
            return objectMapper.readValue(data.traverse(), javaType);
        };
    }

}
