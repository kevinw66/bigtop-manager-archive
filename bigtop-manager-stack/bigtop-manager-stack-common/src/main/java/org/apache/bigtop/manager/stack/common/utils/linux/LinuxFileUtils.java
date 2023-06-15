package org.apache.bigtop.manager.stack.common.utils.linux;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.utils.JsonUtils;
import org.apache.bigtop.manager.stack.common.utils.PropertiesUtils;
import org.apache.bigtop.manager.stack.common.utils.XmlUtils;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.*;
import java.util.Map;
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

        updateOwner(fileName, owner, group, false);
        updatePermissions(fileName, permissionsString, false);
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

        updateOwner(fileName, owner, group, false);
        updatePermissions(fileName, permissionsString, false);
    }

    /**
     * Update file Permissions
     *
     * @param filePath          file path
     * @param permissionsString {@code rwxr--r--}
     * @param recursive         recursive
     */
    public static void updatePermissions(String filePath, String permissionsString, boolean recursive) {
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

        // When is a directory, recursive update
        if (recursive && Files.isDirectory(path)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path subPath : ds) {
                    updatePermissions(filePath + File.separator + subPath.getFileName(), permissionsString, true);
                }
            } catch (IOException e) {
                log.error("[updatePermissions] error,", e);
            }
        }
    }

    /**
     * Update file owner
     *
     * @param filePath  file path
     * @param owner     owner
     * @param group     group
     * @param recursive recursive
     */
    public static void updateOwner(String filePath, String owner, String group, boolean recursive) {
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

        // When it is a directory, recursively set the file owner
        if (recursive && Files.isDirectory(path)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path subPath : ds) {
                    updateOwner(filePath + File.separator + subPath.getFileName(), owner, group, true);
                }
            } catch (IOException e) {
                log.error("[updateOwner] error,", e);
            }
        }
    }

    /**
     * create directories
     *
     * @param dirPath           directory path
     * @param owner             owner
     * @param group             group
     * @param permissionsString {@code rwxr--r--}
     * @param recursive         recursive
     */
    public static void createDirectories(String dirPath, String owner, String group, String permissionsString, boolean recursive) {
        Path path = Paths.get(dirPath);

        if (Files.isSymbolicLink(path)) {
            log.warn("unable to create symbolic link: {}", dirPath);
            return;
        }

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            log.error("[createDirectories] error,", e);
        }

        updateOwner(dirPath, owner, group, recursive);
        updatePermissions(dirPath, permissionsString, recursive);
    }

    public static void main(String[] args) {
        String file = "/usr/bigtop/3.2.0/usr/lib/zookeeper/conf/zoo.cfg";
        updateOwner(file, "zookeeper", "zookeeper", false);
        updatePermissions(file, "rwxr--r--", false);

        createDirectories("/usr/bigtop/3.2.0/usr/lib/zookeeper/conf/rest", "zookeeper", "root", "rwxrwxr--", true);
    }
}
