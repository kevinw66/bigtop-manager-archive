package org.apache.bigtop.manager.server.job.strategy;

import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.dao.entity.Job;

public interface JobStrategy {
    Boolean handle(Job job, JobStrategyType strategyType);
}
