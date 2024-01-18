package org.apache.bigtop.manager.server.listener.factory;

import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.orm.entity.Job;

public interface JobFactory {

    CommandLevel getCommandLevel();

    Job createJob(JobContext context);
}
