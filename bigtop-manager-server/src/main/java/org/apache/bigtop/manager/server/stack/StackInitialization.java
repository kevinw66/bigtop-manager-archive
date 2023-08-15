package org.apache.bigtop.manager.server.stack;

import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.RepoDTO;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.RepoMapper;
import org.apache.bigtop.manager.server.orm.entity.Repo;
import org.apache.bigtop.manager.server.orm.entity.Stack;
import org.apache.bigtop.manager.server.orm.repository.RepoRepository;
import org.apache.bigtop.manager.server.orm.repository.StackRepository;
import org.apache.bigtop.manager.server.utils.StackUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Initialize the stack(Include repo and service version) and persist it to the database.
 * 1. Get All stacks, Parse the stack.yaml
 * 2. Parse the stack services
 * 3. Parse the stack repo
 * 4. Check if the stack is already in the database
 * 5. Persist the stack to the database
 */
@Slf4j
@Component
public class StackInitialization implements ApplicationListener<ApplicationStartedEvent> {

    @Resource
    private StackRepository stackRepository;

    @Resource
    private RepoRepository repoRepository;

    @Getter
    private Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> stackKeyMap;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("StackInitialization starting...");
        stackKeyMap = new HashMap<>();

        Map<StackDTO, Set<ServiceDTO>> stackMap = StackUtils.stackList();

        for (Map.Entry<StackDTO, Set<ServiceDTO>> entry : stackMap.entrySet()) {

            StackDTO stackDTO = entry.getKey();
            Set<ServiceDTO> serviceDTOSet = entry.getValue();

            String stackName = stackDTO.getStackName();
            String stackVersion = stackDTO.getStackVersion();
            List<RepoDTO> repoDTOList = stackDTO.getRepos();

            stackKeyMap.put(StackUtils.fullStackName(stackName, stackVersion), new ImmutablePair<>(stackDTO, serviceDTOSet));

            /*
             * Update strategy:
             * 1. If the stack does not exist, create a stack and repo;
             * 2. If a stack exists, do not create a stack, only create a repo.
             */
            Stack stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion).orElse(new Stack());
            if (stack.getId() == null) {
                stack.setStackName(stackName);
                stack.setStackVersion(stackVersion);

                stackRepository.save(stack);

                stack = stackRepository.findByStackNameAndStackVersion(stackName, stackVersion).orElse(new Stack());
                List<Repo> repos = RepoMapper.INSTANCE.DTO2Entity(repoDTOList, stack);
                repoRepository.saveAll(repos);
            } else {
                List<Repo> repos = RepoMapper.INSTANCE.DTO2Entity(repoDTOList, stack);
                for (Repo repo : repos) {
                    Optional<Repo> repoOptional = repoRepository.findByRepoIdAndOsAndArchAndStackId(repo.getRepoId(), repo.getOs(), repo.getArch(), stack.getId());

                    repoOptional.ifPresent(value -> repo.setId(value.getId()));
                    repoRepository.save(repo);
                }
            }
        }
    }

}
