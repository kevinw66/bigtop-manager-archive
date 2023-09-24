package org.apache.bigtop.manager.server.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AccessLevel;
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
import org.apache.bigtop.manager.server.stack.dag.DagHelper;
import org.apache.bigtop.manager.server.stack.pojo.ServiceModel;
import org.apache.bigtop.manager.server.stack.pojo.StackModel;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.io.File;
import java.net.URL;
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

    private static final Map<String, Map<String, Set<String>>> STACK_DEPENDENCY_MAP = new HashMap<>();

    private static final Map<String, Map<String, Set<String>>> STACK_CONFIG_MAP = new HashMap<>();

    private static final Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> STACK_KEY_MAP = new HashMap<>();


    public static Map<String, Map<String, Set<String>>> getStackConfigMap() {
        return Collections.unmodifiableMap(STACK_CONFIG_MAP);
    }

    public static Map<String, ImmutablePair<StackDTO, Set<ServiceDTO>>> getStackKeyMap() {
        return Collections.unmodifiableMap(STACK_KEY_MAP);
    }

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

                // metainfo.yaml
                ServiceModel serviceModel = YamlUtils.readYaml(file.getAbsolutePath() + "/" + META_FILE, ServiceModel.class);
                ServiceDTO serviceDTO = ServiceMapper.INSTANCE.Model2DTO(serviceModel);
                serviceDTOSet.add(serviceDTO);

                // order.json
                File dependencyFile = new File(file.getAbsolutePath(), DEPENDENCY_FILE);
                if (dependencyFile.exists()) {
                    Map<String, Set<String>> singleDependencyMap = JsonUtils.readFromFile(dependencyFile.getAbsolutePath(), new TypeReference<>() {});
                    if (Objects.nonNull(singleDependencyMap)) {
                        mergedDependencyMap.putAll(singleDependencyMap);
                    }
                }

                // configurations
                Set<String> serviceConfigSet = new HashSet<>();
                File configFolder = new File(file.getAbsolutePath(), CONFIGURATION_FOLDER_NAME);
                if (configFolder.exists()) {
                    for (File configFile : Optional.ofNullable(configFolder.listFiles()).orElse(new File[0])) {
                        serviceConfigSet.add(configFile.getAbsolutePath());
                    }
                }
                mergedConfigMap.put(serviceDTO.getServiceName(), serviceConfigSet);
            }
            STACK_DEPENDENCY_MAP.put(fullStackName(stackName, stackVersion), mergedDependencyMap);
            STACK_CONFIG_MAP.put(fullStackName(stackName, stackVersion), mergedConfigMap);

            log.info("stackConfigMap: {}", STACK_CONFIG_MAP);
            log.info("stackDependencyMap: {}", STACK_DEPENDENCY_MAP);
        }

        return serviceDTOSet;
    }

    /**
     * @return stack list map
     */
    public static Map<StackDTO, Set<ServiceDTO>> stackList() throws ServerException {
        File stacksFile = loadStackFile();
        File[] files = Optional.ofNullable(stacksFile.listFiles()).orElse(new File[0]);
        Map<StackDTO, Set<ServiceDTO>> stackMap = new HashMap<>();

        for (File stackFile : files) {
            String stackName = stackFile.getName();
            File[] subVersions = Optional.ofNullable(stackFile.listFiles()).orElse(new File[0]);

            for (File stackVersionFile : subVersions) {
                String stackVersion = stackVersionFile.getName();
                log.info("stackName: {}, stackVersion: {}", stackName, stackVersion);

                StackDTO stackDTO = parseStack(stackVersionFile);

                checkStack(stackVersionFile);

                Set<ServiceDTO> serviceDTOSet = parseService(stackVersionFile, stackName, stackVersion);

                stackMap.put(stackDTO, serviceDTOSet);

                STACK_KEY_MAP.put(StackUtils.fullStackName(stackName, stackVersion), new ImmutablePair<>(stackDTO, serviceDTOSet));
            }
        }
        log.info("stackKeyMap: {}", STACK_KEY_MAP);

        DagHelper.dagInitialized(STACK_DEPENDENCY_MAP);
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
            URL url = StackUtils.class.getClassLoader().getResource(STACKS_FOLDER_NAME);
            if (url == null) {
                throw new ServerException("Can't find stack folder");
            }

            stackPath = url.getPath();
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
        String[] list = stackVersionFile.list();
        if (list != null && !Arrays.stream(list).toList().contains(META_FILE)) {
            throw new ServerException(ServerExceptionStatus.STACK_CHECK_INVALID);
        }
    }

    /**
     * Generate full stack name
     * @param stackName BIGTOP
     * @param stackVersion 3.3.0
     * @return {stackName}-{stackVersion} eg. BIGTOP-3.3.0
     */
    public static String fullStackName(String stackName, String stackVersion) {
        return stackName + "-" + stackVersion;
    }

}
