package com.manbo.merchant.config;

import com.manbo.common.service.dao.impl.BaseDAOImpl;
import com.manbo.common.web.config.CommonWebConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * @author manboyu
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.manbo.merchant.dao", repositoryBaseClass = BaseDAOImpl.class)
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
@EnableScheduling
@Import(CommonWebConfig.class)
public class AppConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(OffsetDateTime.now());
    }
}
