package org.apache.bigtop.manager.common.utils.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.text.MessageFormat;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShellResult {

    private Integer exitCode;

    private String output;

    private String errMsg;

    public String getResult() {
        return MessageFormat.format("result=[output={0}, errMsg={1}]", output, errMsg);
    }
}
