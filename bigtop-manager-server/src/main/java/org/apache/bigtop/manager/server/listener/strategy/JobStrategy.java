package org.apache.bigtop.manager.server.listener.strategy;

import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.orm.entity.Job;

public interface JobStrategy {
    Boolean handle(Job job, JobStrategyType strategyType);
}
