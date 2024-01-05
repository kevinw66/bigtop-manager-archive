package org.apache.bigtop.manager.stack.common.utils.linux;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.FileUtils;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LinuxOSUtils {

    /**
     * Execute the sudo command
     *
     * @param command command
     * @param tenant  Tenant User
     * @return result of execute command
     * @throws IOException errors
     */
    public static ShellResult sudoExecCmd(String command, String tenant) throws IOException {
        return execCmd(command, getTenant(tenant));
    }

    /**
     * get sudo command
     *
     * @param tenant Tenant User
     * @return result of sudo execute command
     */
    public static String getTenant(String tenant) {
        if (StringUtils.isBlank(tenant) || !LinuxAccountUtils.isUserExists(tenant)) {
            return "root";
        }

        return tenant;
    }

    /**
     * support sudo command
     */
    public static ShellResult execCmd(String command, String tenant) throws IOException {
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("sudo");
        builderParameters.add("-u");
        builderParameters.add(tenant);
        builderParameters.add("sh");
        builderParameters.add("-c");
        builderParameters.add(command);
        return ShellExecutor.execCommand(builderParameters);
    }

    /**
     * Execute the corresponding command of Linux or Windows
     *
     * @param command command
     * @return result of execute command
     * @throws IOException errors
     */
    public static ShellResult execCmd(String command) throws IOException {
        StringTokenizer st = new StringTokenizer(command);
        List<String> builderParameters = new ArrayList<>();
        while (st.hasMoreTokens()) {
            builderParameters.add(st.nextToken());
        }
        return ShellExecutor.execCommand(builderParameters);
    }

    public static ShellResult checkProcess(String filepath) {
        File file = new File(filepath);
        if (!file.exists() || !file.isFile()) {
            log.warn("Pid file {} is empty or does not exist", filepath);
            return new ShellResult(-1, "", "Component is not running");
        }
        int pid;
        try {
            pid = Integer.parseInt(FileUtils.readFile2Str(file));
        } catch (Exception e) {
            log.error("a", e);
            log.warn("Pid file {} does not exist or does not contain a process id number", filepath);
            return new ShellResult(-1, "", "Component is not running");
        }
        try {
            return execCmd("kill -0 " + pid);
        } catch (IOException e) {
            log.warn("Process with pid {} is not running. Stale pid file at {}", pid, filepath);
            return new ShellResult(-1, "", "Component is not running");
        }
    }

}
