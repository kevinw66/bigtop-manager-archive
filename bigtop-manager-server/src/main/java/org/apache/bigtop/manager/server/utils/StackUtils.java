package org.apache.bigtop.manager.server.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.dto.ServiceDTO;
import org.apache.bigtop.manager.server.model.dto.StackDTO;
import org.apache.bigtop.manager.server.model.mapper.ServiceMapper;
import org.apache.bigtop.manager.server.model.mapper.StackMapper;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;
import org.apache.bigtop.manager.server.stack.pojo.StackModel;

import java.io.File;
import java.util.*;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StackUtils {

    private static final String BIGTOP_MANAGER_STACK_PATH = "bigtop.manager.stack.path";

    private static final String META_FILE = "metainfo.yaml";

    private static final String STACKS_FOLDER_NAME = "stacks";

    private static final String SERVICES_FOLDER_NAME = "services";

    private static final String CONFIGURATION_FOLDER_NAME = "configuration";

    private static final String DEPENDENCY_FILE = "order.json";

    private static final Map<String, Map<String, Set<String>>> stackDependencyMap = new HashMap<>();

    @Getter
    private static final Map<String, Map<String, Set<String>>> stackConfigMap = new HashMap<>();

    /**
     * Parse stack file to generate stack model
     * @return stack model {@link StackModel}
     */
    public static StackDTO parseStack(File stackVersionFile) {
        return StackMapper.INSTANCE.Model2DTO(YamlUtils.readYaml(
                stackVersionFile.getAbsolutePath() + File.separator + META_FILE,
                StackModel.class));
    }

    /**
     * Parse service file to generate service model
     * @param stackName stack name
     * @param stackVersion stack version
     * @return service model {@link ServiceModel}
     */
    public static Set<ServiceDTO> parseService(File stackVersionFile, String stackName, String stackVersion) {
        Map<String, Set<String>> mergedDependencyMap = new HashMap<>();
        Map<String, Set<String>> mergedConfigMap = new HashMap<>();

        File[] files = new File(stackVersionFile.getAbsolutePath(), SERVICES_FOLDER_NAME).listFiles();

        Set<ServiceDTO> serviceDTOSet = new HashSet<>();
        if (files != null) {
            for (File file : files) {
                log.info("service dir: {}", file);

                //metainfo.yaml
                ServiceModel serviceModel = YamlUtils.readYaml(file.getAbsolutePath() + "/" + META_FILE, ServiceModel.class);
                ServiceDTO serviceDTO = ServiceMapper.INSTANCE.Model2DTO(serviceModel);
                serviceDTOSet.add(serviceDTO);

                //order.json
                File dependencyFile = new File(file.getAbsolutePath(), DEPENDENCY_FILE);
                if (dependencyFile.exists()) {
                    Map<String, Set<String>> singleDependencyMap = JsonUtils.readJson(dependencyFile.getAbsolutePath(),
                            new TypeReference<>() {
                            });
                    if (Objects.nonNull(singleDependencyMap)) {
                        mergedDependencyMap.putAll(singleDependencyMap);
                    }
                }

                //configurations
                Set<String> serviceConfigSet = new HashSet<>();
                File configFolder = new File(file.getAbsolutePath(), CONFIGURATION_FOLDER_NAME);

                if (configFolder.exists()) {
                    Arrays.stream(configFolder.listFiles()).map(x -> serviceConfigSet.add(x.getAbsolutePath()));
                }
                mergedConfigMap.put(serviceDTO.getServiceName(), serviceConfigSet);
            }
            stackDependencyMap.put(fullStackName(stackName, stackVersion), mergedDependencyMap);
            stackConfigMap.put(fullStackName(stackName, stackVersion), mergedConfigMap);
        }

        return serviceDTOSet;
    }

    /**
     * @return stack list map
     */
    public static Map<StackDTO, Set<ServiceDTO>> stackList() throws ServerException {
        File stacksFile = loadStackFile();
        File[] files = stacksFile.listFiles();
        Map<StackDTO, Set<ServiceDTO>> stackMap = new HashMap<>();

        for (File stackFile : files) {
            String stackName = stackFile.getName();
            File[] subVersions = stackFile.listFiles();

            for (File stackVersionFile : subVersions) {
                String stackVersion = stackVersionFile.getName();
                log.info("stackName: {}, stackVersion: {}", stackName, stackVersion);

                StackDTO stackDTO = parseStack(stackVersionFile);

                checkStack(stackVersionFile);

                Set<ServiceDTO> serviceDTOSet = parseService(stackVersionFile, stackName, stackVersion);

                stackMap.put(stackDTO, serviceDTOSet);

            }
        }
        return stackMap;
    }

    /**
     * Load stack folder as file
     */
    private static File loadStackFile() throws ServerException {
        String stackPath = System.getProperty(BIGTOP_MANAGER_STACK_PATH);
        stackPath = stackPath == null ? "" : stackPath;

        File file = new File(stackPath);
        if (!file.exists() || !file.isDirectory()) {
            stackPath = StackUtils.class.getClassLoader().getResource(STACKS_FOLDER_NAME).getPath();
            file = new File(stackPath);
            if (!file.exists()) {
                throw new ServerException("Can't find stack folder");
            }
        }
        log.info("stack file: {}", file);
        return file;
    }

    /**
     * Check stack file
     * @param stackVersionFile stack version file
     */
    private static void checkStack(File stackVersionFile) throws ServerException {
        if (!Arrays.stream(stackVersionFile.list()).toList().contains(META_FILE)) {
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
