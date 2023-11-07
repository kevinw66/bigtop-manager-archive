package org.apache.bigtop.manager.server.listener.strategy;

import org.apache.bigtop.manager.common.message.type.BaseCommandMessage;
import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.orm.entity.Job;

public interface JobStrategy<T extends BaseCommandMessage> {
    Boolean handle(Job job, Class<T> clazz, JobStrategyType strategyType);
}
