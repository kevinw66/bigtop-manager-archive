package org.apache.bigtop.manager.common.mpack.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.bigtop.manager.common.mpack.common.BaseManager;
import org.apache.bigtop.manager.common.shell.ShellExecutor;

import java.io.IOException;

@Data
@AllArgsConstructor
public class PackageManager implements BaseManager {

    private String pkg;

    @Override
    public String getCommand() {
        return String.format("yum install -y %", pkg);
    }

    @Override
    public String runCommand() {
        String output = null;
        try {
            output = ShellExecutor.execCommand(getCommand());
            return output;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
