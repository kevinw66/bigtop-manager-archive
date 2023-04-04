package org.apache.bigtop.manager.server.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.server.enums.ResponseStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ServiceException extends RuntimeException {

    private ResponseStatus status;

    public ServiceException(ResponseStatus status) {
        this.status = status;
    }
}
