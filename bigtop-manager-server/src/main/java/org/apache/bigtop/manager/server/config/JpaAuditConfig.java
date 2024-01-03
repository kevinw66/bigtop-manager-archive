package org.apache.bigtop.manager.server.config;

import jakarta.annotation.Nonnull;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class JpaAuditConfig implements AuditorAware<Long> {

    @Nonnull
    @Override
    public Optional<Long> getCurrentAuditor() {
        try {
            return Optional.of(SessionUserHolder.getUserId());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

}
