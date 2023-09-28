package org.apache.bigtop.manager.server.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;

@Data
@EqualsAndHashCode(callSuper = false)
public class ApiException extends RuntimeException {

    private ApiExceptionEnum ex;

    public ApiException(ApiExceptionEnum ex) {
        this.ex = ex;
    }
}
