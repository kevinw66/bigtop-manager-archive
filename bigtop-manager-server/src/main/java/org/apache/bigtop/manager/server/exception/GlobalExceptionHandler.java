package org.apache.bigtop.manager.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.ResponseStatus;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Void> exceptionHandler(Exception e) {
        log.error("Internal Server Error: ", e);
        return ResponseEntity.error(ResponseStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = ServiceException.class)
    public ResponseEntity<Void> exceptionHandler(ServiceException e) {
        return ResponseEntity.error(e.getStatus());
    }
}
