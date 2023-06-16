package org.apache.bigtop.manager.stack.common.utils.linux;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.exception.StackException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class LinuxAccountUtils {

    private static final String GROUPADD = "/usr/sbin/groupadd";
    private static final String GROUPDEL = "/usr/sbin/groupdel";
    private static final String GROUPMOD = "/usr/sbin/groupmod";

    private static final String USERADD = "/usr/sbin/useradd";
    private static final String USERDEL = "/usr/sbin/userdel";
    private static final String USERMOD = "/usr/sbin/usermod";


    /**
     * Delete user
     *
     * @param user User Name
     */
    public static void userDel(String user) {
        Objects.requireNonNull(user);

        List<String> builderParameters = new ArrayList<>();

        if (isUserExists(user)) {
            builderParameters.add(USERDEL);
        } else {
            return;
        }

        builderParameters.add(user);

        log.debug("builderParameters: {}", builderParameters);

        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[AccountGroupUtils] [userDel] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    /**
     * Add user
     * useradd [options] LOGIN
     */
    public static void userAdd(String user,
                               String group,
                               List<String> groups) {
        userAdd(user, group, null, groups, null, null, null, false);
    }

    /**
     * Add user
     * useradd [options] LOGIN
     *
     * @param user     User Name
     * @param group    Primary user group
     * @param uid      user id
     * @param groups   group list
     * @param home     user home directory
     * @param comment  user comment
     * @param password user password
     */
    public static void userAdd(String user,
                               String group,
                               String uid,
                               List<String> groups,
                               String home,
                               String comment,
                               String password,
                               Boolean system) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(group);

        if (!isGroupExists(group)) {
            groupAdd(group);
        }

        if (!CollectionUtils.isEmpty(groups)) {
            for (String g : groups) {
                groupAdd(g);
            }
        }

        List<String> builderParameters = new ArrayList<>();

        if (isUserExists(user)) {
            builderParameters.add(USERMOD);
        } else {
            builderParameters.add(USERADD);
            builderParameters.add("-m");
        }

        if (StringUtils.isNoneBlank(home)) {
            builderParameters.add("-d");
            builderParameters.add(home);
        }
        if (StringUtils.isNoneBlank(password)) {
            builderParameters.add("-p");
            builderParameters.add(password);
        }
        if (StringUtils.isNoneBlank(comment)) {
            builderParameters.add("-c");
            builderParameters.add(comment);
        }
        if (system) {
            builderParameters.add("--system");
        }
        if (StringUtils.isNoneBlank(uid)) {
            builderParameters.add("-u");
            builderParameters.add(uid);
        }

        builderParameters.add("-g");
        builderParameters.add(group);

        if (!CollectionUtils.isEmpty(groups)) {
            builderParameters.add("-G");
            builderParameters.add(String.join(",", groups));
        }
        builderParameters.add(user);

        log.debug("builderParameters: {}", builderParameters);

        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[AccountGroupUtils] [userAdd] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }


    /**
     * Delete group
     *
     * @param group Group Name
     */
    public static void groupDel(String group) {
        Objects.requireNonNull(group);

        List<String> builderParameters = new ArrayList<>();

        if (isGroupExists(group)) {
            builderParameters.add(GROUPDEL);
        } else {
            return;
        }

        builderParameters.add(group);

        log.debug("builderParameters: {}", builderParameters);

        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[AccountGroupUtils] [addGroup] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }


    /**
     * Add group
     *
     * @param group Group Name
     */
    public static void groupAdd(String group) {
        groupAdd(group, null, null);
    }

    /**
     * Add group
     * {@code groupadd [-g gid] [-p password] GROUP}
     * or
     * {@code groupmod [-g gid] [-p password] GROUP}
     *
     * @param group    Group Name
     * @param gid      groupId
     * @param password password
     */
    public static void groupAdd(String group, String gid, String password) {
        Objects.requireNonNull(group);

        List<String> builderParameters = new ArrayList<>();

        if (isGroupExists(group)) {
            builderParameters.add(GROUPMOD);
        } else {
            builderParameters.add(GROUPADD);
        }

        if (StringUtils.isNoneBlank(gid)) {
            builderParameters.add("-g");
            builderParameters.add(gid);
        }
        if (StringUtils.isNoneBlank(password)) {
            builderParameters.add("-p");
            builderParameters.add(password);
        }
        builderParameters.add(group);

        log.debug("builderParameters: {}", builderParameters);

        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[AccountGroupUtils] [addGroup] output: {}", output);
        } catch (IOException e) {
            throw new StackException(e);
        }
    }

    /**
     * Check if exists group
     *
     * @param group Group Name
     * @return
     */
    public static boolean isGroupExists(String group) {
        Objects.requireNonNull(group);

        List<String> builderParameters = new ArrayList<>();

        builderParameters.add("sh");
        builderParameters.add("-c");
        builderParameters.add("awk -F':' '{print $1}' /etc/group | grep  " + group);

        log.debug("builderParameters: {}", builderParameters);

        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[AccountGroupUtils] [checkIfExistsGroup] output: {}", output);
            return output.getExitCode() == 0;
        } catch (IOException e) {
            throw new StackException(e);
        }

    }

    /**
     * Check if exists user
     *
     * @param user User Name
     */
    public static boolean isUserExists(String user) {
        Objects.requireNonNull(user);

        List<String> builderParameters = new ArrayList<>();

        builderParameters.add("sh");
        builderParameters.add("-c");
        builderParameters.add("awk -F':' '{print $1}' /etc/passwd | grep  " + user);

        log.debug("builderParameters: {}", builderParameters);

        try {
            ShellResult output = ShellExecutor.execCommand(builderParameters);
            log.info("[AccountGroupUtils] [checkIfExistsUser] output: {}", output);
            return output.getExitCode() == 0;
        } catch (IOException e) {
            throw new StackException(e);
        }

    }

}
