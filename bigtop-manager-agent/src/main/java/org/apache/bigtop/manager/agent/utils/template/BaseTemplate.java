package org.apache.bigtop.manager.agent.utils.template;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.agent.configuration.FreemarkerConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Component
public class BaseTemplate {
    @Resource
    private FreemarkerConfiguration freemarkerConfiguration;

    public void writeTemplate(String path, Object dataModel, String type) {
        /* Get the template (uses cache internally) */
        Template template = null;
        try {
            template = freemarkerConfiguration.configuration().getTemplate(type + ".ftl");
            writeTemplate(path, dataModel, template);
        } catch (IOException e) {
            log.error("Failed to writeTemplate, ", e);
        }
    }

    public void writeTemplateByContent(String path, Object dataModel, String sourceStr) {
        /* Get the template (uses cache internally) */
        try {
            Template template = new Template("tmpTemplate", sourceStr, freemarkerConfiguration.configuration());
            writeTemplate(path, dataModel, template);
        } catch (IOException e) {
            log.error("Failed to writeTemplate, ", e);
        }
    }

    public void writeTemplate(String path, Object dataModel, Template template) {
        /* Get the template (uses cache internally) */
        FileWriter fileWriter = null;
        try {
            /* Merge data-model with template */
            fileWriter = new FileWriter(path, false);
            template.process(dataModel, fileWriter);
            fileWriter.flush();
        } catch (TemplateException | IOException e) {
            log.error("Failed to writeTemplate, ", e);
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
