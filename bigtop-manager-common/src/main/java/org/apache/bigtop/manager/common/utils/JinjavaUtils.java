package org.apache.bigtop.manager.common.utils;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.hubspot.jinjava.Jinjava;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Component
public class JinjavaUtils {

    @Resource
    private Jinjava jinjava;

    public String renderTemplate(String template, Map<String, Object> context) {
        return jinjava.render(template, context);
    }

    public String renderTemplateByPath(String templatePath, Map<String, Object> context) throws IOException {
        String template = Resources.toString(Resources.getResource("my-template.html"), Charsets.UTF_8);

        return jinjava.render(template, context);
    }
}
