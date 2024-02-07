package org.apache.bigtop.manager.server.command.job.factory;

import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.dao.entity.Job;

public interface JobFactory {

    CommandIdentifier getCommandIdentifier();

    Job createJob(JobContext jobContext);
}
