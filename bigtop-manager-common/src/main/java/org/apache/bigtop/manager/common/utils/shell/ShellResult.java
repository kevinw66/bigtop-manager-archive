package org.apache.bigtop.manager.common.utils.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShellResult {

    private Integer exitCode;

    private String output;

    private String errMsg;
}
