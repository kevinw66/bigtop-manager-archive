package org.apache.bigtop.manager.common.utils.os;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.common.utils.shell.ShellExecutor;
import org.apache.bigtop.manager.common.utils.shell.ShellResult;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class OSDetection {

    private static final Pattern ID_PATTERN = Pattern.compile("ID=(.*)");
    private static final Pattern VERSION_PATTERN = Pattern.compile("VERSION_ID=(.*)");

    public static String getOS() {
        return getOSType().toLowerCase() + getOSVersion().toLowerCase();
    }

    public static String getOSType() {
        String output = getOSRelease();

        String osType = regexOS(ID_PATTERN, output);

        log.info(osType);
        return osType;
    }

    public static String getOSVersion() {
        String output = getOSRelease();

        String osVersion = regexOS(VERSION_PATTERN, output);

        log.info(osVersion);
        return osVersion;
    }


    public static String getOSRelease() {
        checkIfLinux();

        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("cat");
        builderParameters.add("/etc/os-release");

        try {
            ShellResult shellResult = ShellExecutor.execCommand(builderParameters);
            String output = shellResult.getOutput();

            log.debug("getOSRelease: {}", output);
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private static String regexOS(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).replace("\"", "");
        } else {
            return null;
        }
    }


    public static String getArch() {
        checkIfLinux();

        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("arch");

        try {
            ShellResult shellResult = ShellExecutor.execCommand(builderParameters);
            String output = shellResult.getOutput().replace("\n", "");

            log.debug("getArch: {}", output);
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkIfLinux() {
        if (!SystemUtils.IS_OS_LINUX) {
            throw new RuntimeException("Only Linux is supported");
        }
        ifSupportedOS();
    }

    //TODO: Define the system support logic
    public static boolean ifSupportedOS() {
        return true;
    }

}
