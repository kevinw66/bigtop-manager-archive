package org.apache.bigtop.manager.server.job.strategy;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.constants.MessageConstants;
import org.apache.bigtop.manager.common.message.type.BaseCommandMessage;
import org.apache.bigtop.manager.common.message.type.RequestMessage;
import org.apache.bigtop.manager.common.message.type.ResultMessage;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.JobState;
import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.holder.SpringContextHolder;
import org.apache.bigtop.manager.server.orm.entity.CommandLog;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.CommandLogRepository;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;
import static org.apache.bigtop.manager.common.constants.Constants.COMMAND_MESSAGE_RESPONSE_TIMEOUT;

@Slf4j
@Component
public class AsyncJobStrategy extends AbstractJobStrategy {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Resource
    private CommandLogRepository commandLogRepository;

    private CountDownLatch countDownLatch;

    private AtomicBoolean failed;

    @Override
    public Boolean handle(Job job, JobStrategyType strategyType) {
        failed = new AtomicBoolean(false);
        List<Stage> stageCompletedList = new ArrayList<>();

        // Job state change to processing
        job.setState(JobState.PROCESSING);
        jobRepository.save(job);

        List<Stage> stages = job.getStages();
        LinkedBlockingQueue<Stage> pipeLineQueue = new LinkedBlockingQueue<>(stages);
        while (!pipeLineQueue.isEmpty()) {
            Stage stage = pipeLineQueue.poll();

            // Stage state change to processing
            stage.setState(JobState.PROCESSING);
            stageRepository.save(stage);
            StageCallback stageCallback = getStageCallback(stage);

            if (stageCallback != null) {
                stageCallback.beforeStage(stage);
            }

            countDownLatch = new CountDownLatch(stage.getTasks().size());
            for (Task task : stage.getTasks()) {
                // Task state change to processing
                task.setState(JobState.PROCESSING);
                taskRepository.save(task);

                String content = task.getContent();
                if (stageCallback != null && stage.getName().equals(CACHE_STAGE_NAME)) {
                    content = stageCallback.generatePayload(task);
                }

                BaseCommandMessage message = JsonUtils.readFromString(content, RequestMessage.class);
                message.setTaskId(task.getId());
                message.setStageId(stage.getId());
                message.setJobId(job.getId());
                log.info("[AsyncJobStrategy] [BaseCommandMessage]: {}", message);

                SpringContextHolder.getServerWebSocket().sendMessage(task.getHostname(), message, this::call);
            }

            boolean timeoutFlag = false;
            try {
                timeoutFlag = countDownLatch.await(COMMAND_MESSAGE_RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                stage.setState(JobState.FAILED);
            }

            if (timeoutFlag && !failed.get()) {
                stage.setState(JobState.SUCCESSFUL);
            } else {
                log.error("stage failed or timeout, cancel remain stages, [timeoutFlag: {}], [failed: {}]", timeoutFlag, failed);
                stage.setState(JobState.FAILED);
                if (strategyType == JobStrategyType.OVER_ON_FAIL) {
                    releaseRemainStages(pipeLineQueue);
                }
            }
            stageCompletedList.add(stage);
            stageRepository.save(stage);

            if (stageCallback != null) {
                stageCallback.afterStage(stage);
            }
        }

        boolean stageSuccessful = stageCompletedList.stream().allMatch(stage -> stage.getState() == JobState.SUCCESSFUL);
        if (stageSuccessful) {
            job.setState(JobState.SUCCESSFUL);
        } else {
            job.setState(JobState.FAILED);
        }
        jobRepository.save(job);
        return failed.get();
    }


    /**
     * callback
     */
    public void call(ResultMessage resultMessage) {
        log.info("Execute RequestMessage completed, {}", resultMessage);
        Task task = taskRepository.getReferenceById(resultMessage.getTaskId());

        if (resultMessage.getCode() == MessageConstants.SUCCESS_CODE) {
            task.setState(JobState.SUCCESSFUL);
        } else {
            task.setState(JobState.FAILED);
            failed.set(true);
        }
        taskRepository.save(task);
        saveCommandLog(resultMessage.getResult(), task);

        countDownLatch.countDown();
    }


    /**
     * Release remaining Stages
     * Execute when failed or timeout
     */
    private void releaseRemainStages(LinkedBlockingQueue<Stage> pipeLineQueue) {
        if (!pipeLineQueue.isEmpty()) {
            List<Stage> remainStages = new ArrayList<>(pipeLineQueue.size());
            pipeLineQueue.drainTo(remainStages);
            for (Stage stage : remainStages) {
                // Setting the status of the remaining stages to CANCELED
                stage.setState(JobState.CANCELED);
                stageRepository.save(stage);

                stage.getTasks().forEach(t -> t.setState(JobState.CANCELED));
                taskRepository.saveAll(stage.getTasks());
            }
        }
    }

    private void saveCommandLog(String result, Task task) {
        CommandLog commandLog = new CommandLog();
        commandLog.setTask(task);
        commandLog.setStage(task.getStage());
        commandLog.setJob(task.getJob());
        commandLog.setHostname(task.getHostname());
        commandLog.setResult(result);
        commandLogRepository.save(commandLog);
    }

}
