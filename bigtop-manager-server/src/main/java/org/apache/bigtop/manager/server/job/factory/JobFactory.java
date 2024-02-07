package org.apache.bigtop.manager.server.job.factory;

import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.dao.entity.Job;

public interface JobFactory {

    CommandIdentifier getCommandIdentifier();

    Job createJob(JobContext jobContext);
}
