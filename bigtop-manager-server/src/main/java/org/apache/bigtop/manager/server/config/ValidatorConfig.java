package org.apache.bigtop.manager.server.config;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfig {
    @Bean
    public jakarta.validation.Validator validator() {
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
                .configure()
                // fail fast
                .failFast(true)
                .buildValidatorFactory();
        return validatorFactory.getValidator();
    }
}
