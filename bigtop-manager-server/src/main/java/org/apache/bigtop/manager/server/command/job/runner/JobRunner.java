package org.apache.bigtop.manager.server.command.job.runner;

import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.dao.entity.Job;

public interface JobRunner {

    CommandIdentifier getCommandIdentifier();

    void setJob(Job job);

    void beforeRun();

    void run();

    void onSuccess();

    void onFailure();
}
