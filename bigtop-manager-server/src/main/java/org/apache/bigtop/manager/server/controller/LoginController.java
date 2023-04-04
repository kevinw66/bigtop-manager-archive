package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.bigtop.manager.server.enums.ResponseStatus;
import org.apache.bigtop.manager.server.exception.ServiceException;
import org.apache.bigtop.manager.server.model.request.LoginRequest;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login Controller")
@RestController
public class LoginController {

    @Operation(summary = "login", description = "User Login")
    @PostMapping(value = "/login")
    private ResponseEntity<Void> login(@RequestBody LoginRequest loginRequest) {
        if (loginRequest.getUsername().equals("admin")) {
            throw new ServiceException(ResponseStatus.AAA);
        }
        return ResponseEntity.success();
    }
}
