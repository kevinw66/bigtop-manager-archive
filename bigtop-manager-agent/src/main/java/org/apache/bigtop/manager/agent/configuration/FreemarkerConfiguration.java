package org.apache.bigtop.manager.agent.configuration;

import freemarker.core.UndefinedOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class FreemarkerConfiguration {

    @Bean
    public Configuration configuration() {
        Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

        cfg.setClassForTemplateLoading(FreemarkerConfiguration.class, "/templates");

        cfg.setDefaultEncoding("UTF-8");

        cfg.setOutputFormat(UndefinedOutputFormat.INSTANCE);

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        cfg.setLogTemplateExceptions(false);

        cfg.setWrapUncheckedExceptions(true);

        cfg.setFallbackOnNullLoopVariable(false);

        cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());

        return cfg;
    }
}
