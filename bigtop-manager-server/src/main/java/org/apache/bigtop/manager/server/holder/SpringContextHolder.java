/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bigtop.manager.server.holder;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.apache.bigtop.manager.server.job.factory.JobFactory;
import org.apache.bigtop.manager.server.job.validator.CommandValidator;
import org.apache.bigtop.manager.server.ws.ServerWebSocketHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringContextHolder implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    public static ServerWebSocketHandler getServerWebSocket() {
        return applicationContext.getBean(ServerWebSocketHandler.class);
    }

    public static Map<String, CommandValidator> getCommandValidators() {
        return applicationContext.getBeansOfType(CommandValidator.class);
    }

    public static Map<String, JobFactory> getJobFactories() {
        return applicationContext.getBeansOfType(JobFactory.class);
    }
}
