package com.manbo.common.web.config;

import com.manbo.common.web.advice.CommonResponseBodyAdvice;
import com.manbo.common.web.filter.LogProcessFilter;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * @author kai
 */
@Slf4j
@Configuration
@Import({CommonResponseBodyAdvice.class})
public class CommonWebConfig {

    @Value("${common.web.access-log.include-payload:false}")
    private Boolean includePayload;

    @Value("${common.web.access-log.include-headers:false}")
    private Boolean includeHeaders;

    @Value("${common.web.access-log.ignoreMessages:}")
    private List<String> ignoreMessages;

    @Bean
    public LogProcessFilter logFilter() {
        final LogProcessFilter filter = new LogProcessFilter(ignoreMessages);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(includePayload);
        filter.setIncludeHeaders(includeHeaders);
        filter.setMaxPayloadLength(10000);
        filter.setBeforeMessagePrefix("REQUEST DATA: ");
        return filter;
    }
}
