package org.apache.bigtop.manager.agent.stack;


import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ScriptHandler {

    @Resource
    ApplicationContext applicationContext;

    public Script scriptHandler(String beanId) {
        return applicationContext.getBean(beanId, Script.class);
    }
}
