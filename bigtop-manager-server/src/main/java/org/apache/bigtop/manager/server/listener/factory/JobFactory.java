package org.apache.bigtop.manager.server.listener.factory;

import org.apache.bigtop.manager.server.orm.entity.Job;

public interface JobFactory {
    Job createJob(JobFactoryContext context);
}
