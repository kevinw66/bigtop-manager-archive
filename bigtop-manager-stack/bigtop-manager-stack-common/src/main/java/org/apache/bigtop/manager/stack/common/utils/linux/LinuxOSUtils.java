package org.apache.bigtop.manager.stack.common.utils.linux;

import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.bigtop.manager.stack.common.utils.linux.LinuxAccountUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class LinuxOSUtils {

    /**
     * Execute the sudo command
     *
     * @param command    command
     * @param tenantUser tenantUser
     * @return result of execute command
     * @throws IOException errors
     */
    public static ShellResult sudoExecCmd(String command, String tenantUser) throws IOException {
        return execCmd(getSudoCmd(tenantUser, command));
    }

    /**
     * get sudo command
     *
     * @param tenant Tenant User
     * @param command    command
     * @return result of sudo execute command
     */
    public static String getSudoCmd(String tenant, String command) {
        if (StringUtils.isBlank(tenant) || !LinuxAccountUtils.isUserExists(tenant)) {
            return command;
        }

        return MessageFormat.format("sudo -u {0} {1}", tenant, command);
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
}
