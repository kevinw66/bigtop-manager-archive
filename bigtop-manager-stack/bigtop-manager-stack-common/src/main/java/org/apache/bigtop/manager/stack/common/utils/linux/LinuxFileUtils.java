package org.apache.bigtop.manager.stack.common.utils.linux;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.common.utils.YamlUtils;
import org.apache.bigtop.manager.stack.common.enums.ConfigType;
import org.apache.bigtop.manager.stack.common.utils.template.BaseTemplate;
import org.apache.bigtop.manager.stack.common.utils.template.TemplateUtils;
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

    /**
     * Generate config file by ConfigType
     *
     * @param type        config file type
     * @param filename    file path
     * @param owner       owner
     * @param group       group
     * @param permissions permissions
     * @param content     content map
     */
    public static void toFile(ConfigType type, String filename, String owner, String group, String permissions,
                              Map<String, Object> content) {
        if (type == null || StringUtils.isBlank(filename) || CollectionUtils.isEmpty(content)) {
            log.error("type, filename, content must not be null");
            return;
        }

        switch (type) {
            case PROPERTIES, XML, ENV:
                TemplateUtils.map2Template(filename, content, type);
                break;
            case YAML:
                YamlUtils.writeYaml(filename, content);
                break;
            case JSON:
                JsonUtils.writeToFile(filename, content);
                break;
            case TEMPLATE:
                log.warn("must set template when type is TEMPLATE");
                break;
            case UNKNOWN:
                log.info("no need to write");
                break;
        }

        updateOwner(filename, owner, group, false);
        updatePermissions(filename, permissions, false);
    }

    /**
     * Generate file by template
     *
     * @param type        config file type
     * @param filename    file path
     * @param owner       owner
     * @param group       group
     * @param permissions permissions
     * @param modelMap    modelMap
     * @param template    template
     */
    public static void toFile(ConfigType type, String filename, String owner, String group, String permissions,
                              Map<String, Object> modelMap, String template) {
        if (type == null || StringUtils.isBlank(filename) || CollectionUtils.isEmpty(modelMap) || StringUtils.isEmpty(template)) {
            log.error("type, filename, content, template must not be null");
            return;
        }
        switch (type) {
            case TEMPLATE:
                BaseTemplate.writeCustomTemplate(filename, modelMap, template);
                break;
            default:
                log.warn("only support TEMPLATE type");
        }

        updateOwner(filename, owner, group, false);
        updatePermissions(filename, permissions, false);
    }

    /**
     * Update file Permissions
     *
     * @param dir         file path
     * @param permissions {@code rwxr--r--}
     * @param recursive   recursive
     */
    public static void updatePermissions(String dir, String permissions, boolean recursive) {
        if (StringUtils.isBlank(dir)) {
            log.error("dir must not be null");
            return;
        }
        permissions = StringUtils.isBlank(permissions) ? "rw-r--r--" : permissions;

        Path path = Paths.get(dir);
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString(permissions);
        try {
            Files.setPosixFilePermissions(path, perms);
            log.info("Permissions set successfully.");
        } catch (IOException e) {
            log.error("[updatePermissions] error,", e);
        }

        // When is a directory, recursive update
        if (recursive && Files.isDirectory(path)) {
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(path)) {
                for (Path subPath : ds) {
                    updatePermissions(dir + File.separator + subPath.getFileName(), permissions, true);
                }
            } catch (IOException e) {
                log.error("[updatePermissions] error,", e);
            }
        }
    }

    /**
     * Update file owner
     *
     * @param dir       file path
     * @param owner     owner
     * @param group     group
     * @param recursive recursive
     */
    public static void updateOwner(String dir, String owner, String group, boolean recursive) {
        if (StringUtils.isBlank(dir)) {
            log.error("dir must not be null");
            return;
        }
        owner = StringUtils.isBlank(owner) ? "root" : owner;
        group = StringUtils.isBlank(group) ? "root" : group;

        Path path = Paths.get(dir);
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
                    updateOwner(dir + File.separator + subPath.getFileName(), owner, group, true);
                }
            } catch (IOException e) {
                log.error("[updateOwner] error,", e);
            }
        }
    }

    /**
     * create directories
     *
     * @param dirPath     directory path
     * @param owner       owner
     * @param group       group
     * @param permissions {@code rwxr--r--}
     * @param recursive   recursive
     */
    public static void createDirectories(String dirPath, String owner, String group, String permissions, boolean recursive) {
        if (StringUtils.isBlank(dirPath)) {
            log.error("dirPath must not be null");
            return;
        }
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
        updatePermissions(dirPath, permissions, recursive);
    }

}
