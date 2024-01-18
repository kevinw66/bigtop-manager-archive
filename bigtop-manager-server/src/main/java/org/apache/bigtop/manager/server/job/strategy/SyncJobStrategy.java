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
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.entity.Stage;
import org.apache.bigtop.manager.server.orm.entity.Task;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.apache.bigtop.manager.server.orm.repository.StageRepository;
import org.apache.bigtop.manager.server.orm.repository.TaskRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.bigtop.manager.common.constants.Constants.CACHE_STAGE_NAME;

@Slf4j
@Component
public class SyncJobStrategy extends AbstractJobStrategy {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private StageRepository stageRepository;

    @Resource
    private TaskRepository taskRepository;

    @Override
    public Boolean handle(Job job, JobStrategyType strategyType) {
        AtomicBoolean failed = new AtomicBoolean(false);
        List<Stage> stages = job.getStages();

        job.setState(JobState.PROCESSING);
        jobRepository.save(job);
        for (Stage stage : stages) {
            stage.setState(JobState.PROCESSING);
            stageRepository.save(stage);

            StageCallback stageCallback = getStageCallback(stage);
            if (stageCallback != null) {
                stageCallback.beforeStage(stage);
            }

            List<Task> tasks = stage.getTasks();
            for (Task task : tasks) {
                task.setState(JobState.PROCESSING);
                taskRepository.save(task);

                String content = task.getContent();
                if (stageCallback != null && stage.getName().equals(CACHE_STAGE_NAME)) {
                    content = stageCallback.generatePayload(task);
                }
                BaseCommandMessage message = JsonUtils.readFromString(content, RequestMessage.class);
                log.info("[SyncJobStrategy] [BaseCommandMessage]: {}", message);
                String hostname = task.getHostname();
                ResultMessage res = SpringContextHolder.getServerWebSocket().sendMessage(hostname, message);
                if (res == null || res.getCode() != MessageConstants.SUCCESS_CODE) {
                    task.setState(JobState.FAILED);
                    failed.set(true);
                } else {
                    task.setState(JobState.SUCCESSFUL);
                }
                taskRepository.save(task);

                if (failed.get() && strategyType == JobStrategyType.OVER_ON_FAIL) {
                    break;
                }
            }

            if (failed.get()) {
                stage.setState(JobState.FAILED);
            } else {
                stage.setState(JobState.SUCCESSFUL);
            }
            stageRepository.save(stage);

            if (stageCallback != null) {
                stageCallback.afterStage(stage);
            }

            if (failed.get() && strategyType == JobStrategyType.OVER_ON_FAIL) {
                break;
            }
        }
        if (failed.get()) {
            job.setState(JobState.FAILED);
        } else {
            job.setState(JobState.SUCCESSFUL);
        }
        jobRepository.save(job);

        return failed.get();
    }
}
