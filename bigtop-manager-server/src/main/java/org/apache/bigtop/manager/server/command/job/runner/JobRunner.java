package org.apache.bigtop.manager.server.command.job.runner;

import org.apache.bigtop.manager.server.command.CommandIdentifier;
import org.apache.bigtop.manager.dao.entity.Job;
import org.apache.bigtop.manager.server.command.job.factory.JobContext;

/**
 * Interface for running jobs in the application.
 * A job represents a unit of work, and this interface provides methods for managing and running these jobs.
 */
public interface JobRunner {

    /**
     * Get the identifier of the command that this runner is responsible for.
     *
     * @return The identifier of the command.
     */
    CommandIdentifier getCommandIdentifier();

    /**
     * Set the job that this runner will manage.
     *
     * @param job The job to be managed by this runner.
     */
    void setJob(Job job);

    /**
     * Set the context for the job. The context may contain additional information necessary for running the job.
     *
     * @param jobContext The context for the job.
     */
    void setJobContext(JobContext jobContext);

    /**
     * Method to be called before running the job. Can be used for setup and preparation.
     */
    void beforeRun();

    /**
     * Run the job. This is where the main logic of the job should be implemented.
     */
    void run();

    /**
     * Method to be called after the job has successfully run. Can be used for cleanup and finalization.
     */
    void onSuccess();

    /**
     * Method to be called if the job fails to run. Can be used for error handling and recovery.
     */
    void onFailure();
}