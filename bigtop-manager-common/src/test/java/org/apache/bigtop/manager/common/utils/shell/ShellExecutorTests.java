package org.apache.bigtop.manager.common.utils.shell;

import org.apache.bigtop.manager.common.shell.ShellExecutor;
import org.apache.bigtop.manager.common.shell.ShellResult;

import java.util.ArrayList;
import java.util.List;

public class ShellExecutorTests {

    public static void main(String[] args) throws Exception {
        List<String> builderParameters = new ArrayList<>();
        builderParameters.add("cmd");
        builderParameters.add("/c");
        builderParameters.add("E:\\Projects\\GitHub\\bigtop-manager\\bigtop-manager-common\\src\\test\\java\\org\\apache\\bigtop\\manager\\common\\utils\\shell\\test.bat");

        List<String> res = new ArrayList<>();
        ShellResult shellResult = ShellExecutor.execCommand(builderParameters, System.out::println);
        System.out.println("-----------");
        System.out.println(shellResult.getResult());
    }
}
