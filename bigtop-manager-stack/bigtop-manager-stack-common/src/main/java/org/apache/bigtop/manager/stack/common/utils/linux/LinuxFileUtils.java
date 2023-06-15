package org.apache.bigtop.manager.stack.common.utils.linux;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.utils.JsonUtils;
import org.apache.bigtop.manager.stack.common.utils.PropertiesUtils;
import org.apache.bigtop.manager.stack.common.utils.XmlUtils;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;


/**
 * Only support Linux
 */
@Slf4j
public class LinuxFileUtils {

    public static void toFile(ConfigType type, String fileName, String owner, String group, String permissionsString,
                              Map<String, Object> content) {
        if (type == null || StringUtils.isBlank(fileName) || CollectionUtils.isEmpty(content)) {
            log.error("type, fileName, content must not be null");
            return;
        }

        switch (type) {
            case PROPERTIES:
                PropertiesUtils.writeProperties(fileName, content);
                break;
            case YAML:
                YamlUtils.writeYaml(fileName, content);
                break;
            case XML:
                XmlUtils.writeXml(fileName, content);
                break;
            case JSON:
                JsonUtils.writeJson(fileName, content);
                break;
            case ENV:
                BaseTemplate.writeTemplate(fileName, content, "env");
                break;
            case TEMPLATE:
                log.warn("must set templateString when type is TEMPLATE");
                break;
            case UNKNOWN:
                log.info("no need to write");
                break;
        }

        updateOwner(fileName, owner, group);
        updatePermissions(fileName, permissionsString);
    }

    public static void toFile(ConfigType type, String fileName, String owner, String group, String permissionsString,
                              Map<String, Object> modelMap, String templateString) {
        if (type == null || StringUtils.isBlank(fileName) || CollectionUtils.isEmpty(modelMap) || StringUtils.isEmpty(templateString)) {
            log.error("type, fileName, content, templateString must not be null");
            return;
        }
        switch (type) {
            case TEMPLATE:
                BaseTemplate.writeTemplateByContent(fileName, modelMap, templateString);
                break;
            default:
                log.warn("only support TEMPLATE type");
        }

        updateOwner(fileName, owner, group);
        updatePermissions(fileName, permissionsString);
    }

    /**
     * Update file Permissions
     *
     * @param filePath          file path
     * @param permissionsString {@code rwxr--r--}
     */
    public static void updatePermissions(String filePath, String permissionsString) {
        if (StringUtils.isBlank(filePath)) {
            log.error("filePath must not be null");
            return;
        }
        permissionsString = StringUtils.isBlank(permissionsString) ? "rw-r--r--" : permissionsString;

        Path path = Paths.get(filePath);
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString(permissionsString);
        try {
            Files.setPosixFilePermissions(path, permissions);
            log.info("Permissions set successfully.");
        } catch (IOException e) {
            log.error("[updatePermissions] error,", e);
        }
    }

    /**
     * Update file owner
     *
     * @param filePath file path
     * @param owner    owner
     * @param group    group
     */
    public static void updateOwner(String filePath, String owner, String group) {
        if (StringUtils.isBlank(filePath)) {
            log.error("filePath must not be null");
            return;
        }
        owner = StringUtils.isBlank(owner) ? "root" : owner;
        group = StringUtils.isBlank(group) ? "root" : group;

        Path path = Paths.get(filePath);
        try {
            UserPrincipal userPrincipal = path.getFileSystem().
                    getUserPrincipalLookupService().lookupPrincipalByName(owner);

            GroupPrincipal groupPrincipal = path.getFileSystem().
                    getUserPrincipalLookupService().lookupPrincipalByGroupName(group);

            PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
            fileAttributeView.setOwner(userPrincipal);
            fileAttributeView.setGroup(groupPrincipal);
        } catch (IOException e) {
            log.error("[updateOwner] error,", e);
        }
    }

    public static void main(String[] args) {
        String file = "/usr/bigtop/3.2.0/usr/lib/zookeeper/conf/zoo.cfg";
        updateOwner(file, "test2", "zookeeper");

//        updatePermissions(file, "rwxr--r--");

        Properties properties = new Properties();
        properties.setProperty("a", "1");
    }
}
