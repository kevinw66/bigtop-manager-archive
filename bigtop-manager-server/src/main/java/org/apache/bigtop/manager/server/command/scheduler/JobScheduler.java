package org.apache.bigtop.manager.server.command.scheduler;

import org.apache.bigtop.manager.dao.entity.Job;

/**
 * JobScheduler interface for job management.
 * This interface provides methods to submit, start, and stop jobs.
 */
public interface JobScheduler {

    /**
     * Submits a job to the job scheduler.
     * @param job The job to be submitted.
     */
    void submit(Job job);

    /**
     * Starts the job scheduler.
     * This method should be called after all jobs have been submitted.
     */
    void start();

    /**
     * Stops the job scheduler.
     * This method should be called to gracefully stop the job scheduler.
     */
    void stop();
}
