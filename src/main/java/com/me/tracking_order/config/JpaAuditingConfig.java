package com.me.tracking_order.config;

import com.me.tracking_order.security.CurrentUserProvider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider(
            CurrentUserProvider currentUserProvider
    ) {
        return () -> Optional.of(
                currentUserProvider.findUsername()
                        .orElse("system")
        );
    }
}
