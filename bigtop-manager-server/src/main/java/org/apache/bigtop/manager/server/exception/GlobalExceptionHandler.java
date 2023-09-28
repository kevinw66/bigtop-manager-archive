package org.apache.bigtop.manager.server.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.bigtop.manager.server.enums.LocaleKeys;
import org.apache.bigtop.manager.server.enums.ResponseStatus;
import org.apache.bigtop.manager.server.utils.MessageSourceUtils;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Void> exceptionHandler(ApiException e) {
        return ResponseEntity.error(e.getEx());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Void> exceptionHandler(MethodArgumentNotValidException e) {

        FieldError fieldError = e.getBindingResult().getFieldError();
        String code = fieldError.getCode();
        String field = fieldError.getField();
        String message = fieldError.getDefaultMessage();
        boolean validEnum = EnumUtils.isValidEnum(LocaleKeys.class, code.toUpperCase());
        if (validEnum) {
            message = MessageSourceUtils.getMessage(LocaleKeys.valueOf(code.toUpperCase()), field);
        }
        log.error("Request Body incorrect, message: {}", message, e);
        return ResponseEntity.error(ResponseStatus.PARAMETER_ERROR, message);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Void> exceptionHandler(ConstraintViolationException e) {
        String message = e.getMessage();
        log.error("Method parameter exception, message: {}", message, e);
        return ResponseEntity.error(ResponseStatus.PARAMETER_ERROR, message);
    }
}
