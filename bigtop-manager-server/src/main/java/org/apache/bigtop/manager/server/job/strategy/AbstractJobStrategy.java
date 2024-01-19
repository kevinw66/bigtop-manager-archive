package org.apache.bigtop.manager.server.job.strategy;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.springframework.context.ApplicationContext;

public abstract class AbstractJobStrategy implements JobStrategy {
    @Resource
    private ApplicationContext applicationContext;

    public StageCallback getStageCallback(Stage stage) {
        String callbackClassName = stage.getCallbackClassName();
        if (callbackClassName != null) {
            try {
                Class<?> clazz = Class.forName(callbackClassName);
                if (StageCallback.class.isAssignableFrom(clazz)) {
                    return (StageCallback) applicationContext.getBean(clazz);
                }
            } catch (Exception e) {
                throw new ServerException(e);
            }
        }
        return null;
    }
}
