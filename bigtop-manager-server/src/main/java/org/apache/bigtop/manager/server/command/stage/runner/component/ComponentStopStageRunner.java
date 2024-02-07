package org.apache.bigtop.manager.server.command.stage.runner.component;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.command.stage.factory.StageType;
import org.apache.bigtop.manager.server.command.stage.runner.AbstractStageRunner;
import org.apache.bigtop.manager.common.enums.MaintainState;
import org.apache.bigtop.manager.dao.entity.HostComponent;
import org.apache.bigtop.manager.dao.entity.Task;
import org.apache.bigtop.manager.dao.repository.HostComponentRepository;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

@Slf4j
@org.springframework.stereotype.Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ComponentStopStageRunner extends AbstractStageRunner {

    @Resource
    private HostComponentRepository hostComponentRepository;


    @Override
    public StageType getStageType() {
        return StageType.COMPONENT_STOP;
    }

    @Override
    public void onTaskSuccess(Task task) {
        super.onTaskSuccess(task);

        Long clusterId = task.getCluster().getId();
        String componentName = task.getComponentName();
        String hostname = task.getHostname();
        HostComponent hostComponent = hostComponentRepository.findByComponentClusterIdAndComponentComponentNameAndHostHostname(clusterId, componentName, hostname);
        hostComponent.setState(MaintainState.STOPPED);
        hostComponentRepository.save(hostComponent);
    }
}
