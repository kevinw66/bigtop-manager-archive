package org.apache.bigtop.manager.common.utils.shell;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShellResult {

    private Integer exitCode;

    private String output;

    private String errMsg;
}
