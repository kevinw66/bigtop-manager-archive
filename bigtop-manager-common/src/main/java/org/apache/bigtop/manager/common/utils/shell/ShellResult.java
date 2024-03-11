package org.apache.bigtop.manager.common.utils.shell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.bigtop.manager.common.constants.MessageConstants;

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

    public static ShellResult success(String output) {
        return new ShellResult(MessageConstants.SUCCESS_CODE, output, "");
    }

    public static ShellResult success() {
        return success("Run shell success.");
    }

    public static ShellResult fail(String output) {
        return new ShellResult(MessageConstants.FAIL_CODE, output, "");
    }

    public static ShellResult fail() {
        return fail("Run shell fail.");
    }
}
