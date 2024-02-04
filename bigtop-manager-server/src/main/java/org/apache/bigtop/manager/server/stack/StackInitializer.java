package org.apache.bigtop.manager.server.stack;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.dao.entity.Stack;
import org.apache.bigtop.manager.dao.repository.StackRepository;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Initialize the stack and persist to the database.
 * 1. Get All stacks, Parse the stack.yaml
 * 2. Check if the stack is already in the database
 * 3. Persist the stack to the database
 */
@Slf4j
@Component
public class StackInitializer implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private StackRepository stackRepository;

    @Override
    public void onApplicationEvent(@Nonnull ApplicationStartedEvent event) {
        log.info("Stacks is initializing...");

        Map<StackDTO, List<ServiceDTO>> stackMap = StackUtils.stackList();
        for (Map.Entry<StackDTO, List<ServiceDTO>> entry : stackMap.entrySet()) {
            StackDTO stackDTO = entry.getKey();
            String stackName = stackDTO.getStackName();
            String stackVersion = stackDTO.getStackVersion();

            Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion);
            if (stack == null) {
                stack = new Stack();
                stack.setStackName(stackName);
                stack.setStackVersion(stackVersion);

                stackRepository.save(stack);
            }
        }

        log.info("Stacks is initialized!");
    }
}
