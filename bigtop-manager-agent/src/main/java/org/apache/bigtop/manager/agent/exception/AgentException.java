package org.apache.bigtop.manager.agent.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.agent.enums.AgentExceptionStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class AgentException extends RuntimeException {

    private AgentExceptionStatus ex;

    public AgentException(AgentExceptionStatus ex) {
        this.ex = ex;
    }
}
