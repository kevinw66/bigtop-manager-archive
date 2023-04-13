package org.apache.bigtop.manager.common.shell;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShellResult {

    int exitCode;

    String output;

    String errMsg;
}
