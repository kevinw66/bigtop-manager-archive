package org.apache.bigtop.manager.agent.enums;

public enum AgentExceptionStatus {

    AAA(10000, "aaa"),
    AGENT_MONITORING_ERROR(10001,"get agent host monitoring info failed")
    ;

    private final Integer code;

    private final String message;

    AgentExceptionStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
