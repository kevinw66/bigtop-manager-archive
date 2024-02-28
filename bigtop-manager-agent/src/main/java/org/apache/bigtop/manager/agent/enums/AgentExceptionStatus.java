package org.apache.bigtop.manager.agent.enums;

import lombok.Getter;

@Getter
public enum AgentExceptionStatus {

    AGENT_MONITORING_ERROR(10001,"Get agent host monitoring info failed"),

    COMMAND_FAILED(10002, "Run command failed"),
    ;

    private final Integer code;

    private final String message;

    AgentExceptionStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
