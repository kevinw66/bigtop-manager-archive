package org.apache.bigtop.manager.stack.common.exception;


import java.text.MessageFormat;

public class StackException extends RuntimeException {

    private String message;

    public StackException(String message) {
        super(message);
    }

    public StackException(String msgFormat, Object... args) {
        super(MessageFormat.format(msgFormat, args));
    }

    public StackException(Throwable cause) {
        super(cause);
    }


}
