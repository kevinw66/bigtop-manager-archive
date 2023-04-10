package org.apache.bigtop.manager.common.mpack;

import org.apache.bigtop.manager.agent.mpack.info.StackInfo;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.FileNotFoundException;

@Component
public class StackManager {

    @Resource
    private YamlUtils<StackInfo> yamlUtils;

    public StackInfo stackInfo;

    public void initConfig(String path) throws FileNotFoundException {
        stackInfo = yamlUtils.loadYaml(path, StackInfo.class);
    }

}
