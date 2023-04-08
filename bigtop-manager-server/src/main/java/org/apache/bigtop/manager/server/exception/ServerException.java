package org.apache.bigtop.manager.server.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class ServerException extends RuntimeException {

    private ServerExceptionStatus ex;

    public ServerException(ServerExceptionStatus ex) {
        this.ex = ex;
    }
}
