package org.apache.bigtop.manager.server.exception;


import java.text.MessageFormat;

public class ServerException extends RuntimeException {

    public ServerException(String message) {
        super(message);
    }

    public ServerException(String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
    }

    public ServerException(Throwable cause) {
        super(cause);
    }


}
