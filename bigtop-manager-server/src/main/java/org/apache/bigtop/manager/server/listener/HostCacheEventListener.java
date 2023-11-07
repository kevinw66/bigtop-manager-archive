package org.apache.bigtop.manager.server.listener;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.message.type.HostCacheMessage;
import org.apache.bigtop.manager.server.enums.JobStrategyType;
import org.apache.bigtop.manager.server.listener.strategy.AsyncJobStrategy;
import org.apache.bigtop.manager.server.model.event.HostCacheEvent;
import org.apache.bigtop.manager.server.orm.entity.Job;
import org.apache.bigtop.manager.server.orm.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@org.springframework.stereotype.Component
public class HostCacheEventListener {

    @Resource
    private JobRepository jobRepository;

    @Resource
    private AsyncJobStrategy<HostCacheMessage> asyncJobStrategy;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHostCache(HostCacheEvent event) {
        log.info("listen HostCacheEvent: {}", event);
        Job job = jobRepository.getReferenceById(event.getJobId());

        Boolean failed = asyncJobStrategy.handle(job, HostCacheMessage.class, JobStrategyType.OVER_ON_FAIL);
        log.info("[HostCacheEventListener] failed: {}", failed);
    }

}
