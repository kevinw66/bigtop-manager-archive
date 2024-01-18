package org.apache.bigtop.manager.server.job.validator;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.common.enums.Command;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.enums.CommandLevel;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.job.CommandIdentifier;
import org.apache.bigtop.manager.server.model.dto.command.ClusterCommandDTO;
import org.apache.bigtop.manager.server.orm.entity.Cluster;
import org.apache.bigtop.manager.server.orm.repository.ClusterRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClusterCreateValidator implements CommandValidator {

    @Resource
    private ClusterRepository clusterRepository;

    @Override
    public List<CommandIdentifier> getCommandIdentifiers() {
        return List.of(new CommandIdentifier(CommandLevel.CLUSTER, Command.INSTALL));
    }

    @Override
    public void validate(ValidatorContext context) {
        ClusterCommandDTO clusterCommand = context.getCommandDTO().getClusterCommand();
        String clusterName = clusterCommand.getClusterName();

        Optional<Cluster> clusterOptional = clusterRepository.findByClusterName(clusterName);

        if (clusterOptional.isPresent()) {
            throw new ApiException(ApiExceptionEnum.CLUSTER_IS_INSTALLED, clusterName);
        }
    }
}
