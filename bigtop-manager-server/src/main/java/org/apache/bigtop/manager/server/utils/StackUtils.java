package org.apache.bigtop.manager.server.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;
import org.apache.bigtop.manager.server.stack.pojo.StackModel;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;

import java.io.File;
import java.text.MessageFormat;
import java.util.*;

@Slf4j
public class StackUtils {

    private static final String META_FILE = "metainfo.yaml";

    private static final String SERVICES_TEMPLATE = "stacks/{0}/{1}/services";

    private static final String STACK_META_TEMPLATE = "stacks/{0}/{1}/{2}";


    /**
     * Parse stack file to generate stack model
     * @param stackName stack name
     * @param stackVersion stack version
     * @return stack model {@link StackModel}
     */
    public static StackDTO parseStack(String stackName, String stackVersion) {
        String formatPath = MessageFormat.format(STACK_META_TEMPLATE, stackName, stackVersion, META_FILE);
        log.info("formatPath: {}", formatPath);
        String stackMetaPath = StackUtils.class.getClassLoader().getResource(formatPath).getPath();
        return StackMapper.INSTANCE.Model2DTO(YamlUtils.readYaml(stackMetaPath, StackModel.class));
    }

    /**
     *  Parse service file to generate service model
     * @param stackName stack name
     * @param stackVersion stack version
     * @return service model {@link ServiceModel}
     */
    public static Set<ServiceDTO> parseService(String stackName, String stackVersion) {
        String servicesPath = MessageFormat.format(SERVICES_TEMPLATE, stackName, stackVersion);
        log.info("servicesPath: {}", servicesPath);
        String path = StackUtils.class.getClassLoader().getResource(servicesPath).getPath();
        File[] files = new File(path).listFiles();

        Set<ServiceDTO> serviceDTOSet = new HashSet<>();
        if (files != null) {
            for (File file : files) {
                log.info("file: {}", file);

                ServiceModel serviceModel = YamlUtils.readYaml(file.getAbsolutePath() + "/" + META_FILE, ServiceModel.class);
                ServiceDTO serviceDTO = ServiceMapper.INSTANCE.Model2DTO(serviceModel);
                serviceDTOSet.add(serviceDTO);
            }
        }

        return serviceDTOSet;
    }

    /**
     *
     * @return stack list map
     */
    public static Map<StackDTO, Set<ServiceDTO>> stackList() throws ServerException {
        String path = StackUtils.class.getClassLoader().getResource("stacks").getPath();
        File[] files = new File(path).listFiles();

        Map<StackDTO, Set<ServiceDTO>> stackMap = new HashMap<>();

        for (File file : files) {
            String stackName = file.getName();
            File[] subVersions = file.listFiles();

            for (File subVersion : subVersions) {
                String stackVersion = subVersion.getName();
                log.info("stackName: {}, stackVersion: {}", stackName, stackVersion);

                StackDTO stackDTO = parseStack(stackName, stackVersion);

                checkStack(subVersion);

                Set<ServiceDTO> serviceDTOSet = parseService(stackName, stackVersion);

                stackMap.put(stackDTO, serviceDTOSet);
            }
        }
        return stackMap;
    }

    private static void checkStack(File stackRoot) throws ServerException {
        if (!Arrays.stream(stackRoot.list()).toList().contains(META_FILE)) {
            throw new ServerException(ServerExceptionStatus.STACK_CHECK_INVALID);
        }
    }

    /**
     * Generate full stack name
     * @param stackName BIGTOP
     * @param stackVersion 3.2.0
     * @return stackName-stackVersion BIGTOP-3.2.0
     */
    public static String fullStackName(String stackName, String stackVersion) {
        return stackName + "-" + stackVersion;
    }

}
