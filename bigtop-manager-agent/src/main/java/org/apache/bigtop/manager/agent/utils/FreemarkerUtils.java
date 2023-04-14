package org.apache.bigtop.manager.agent.utils;

import freemarker.core.UndefinedOutputFormat;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.util.TimeZone;

public class FreemarkerUtils {

    private static final Configuration CONFIGURATION;

    static {
        CONFIGURATION = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        CONFIGURATION.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
        CONFIGURATION.setDefaultEncoding("UTF-8");
        CONFIGURATION.setOutputFormat(UndefinedOutputFormat.INSTANCE);
        CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        CONFIGURATION.setLogTemplateExceptions(false);
        CONFIGURATION.setWrapUncheckedExceptions(true);
        CONFIGURATION.setFallbackOnNullLoopVariable(false);
        CONFIGURATION.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
    }
}
