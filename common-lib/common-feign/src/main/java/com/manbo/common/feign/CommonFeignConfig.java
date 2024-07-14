package com.manbo.common.feign;

import com.manbo.common.util.constant.HeaderConst;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

/**
 * @author kai
 */
public class CommonFeignConfig {

    @Bean
    public RequestInterceptor authRequestInterceptor() {
        return requestTemplate -> requestTemplate.header(HeaderConst.COMMON_TRACE_ID, MDC.get(HeaderConst.COMMON_TRACE_ID));
    }

    @Bean
    Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
        return new SpringFormEncoder(new SpringEncoder(converters));
    }
}
