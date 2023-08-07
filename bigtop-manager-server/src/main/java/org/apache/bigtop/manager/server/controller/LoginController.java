package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.request.LoginRequest;
import org.apache.bigtop.manager.server.orm.entity.User;
import org.apache.bigtop.manager.server.service.LoginService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Login Controller")
@RestController
public class LoginController {

    @Resource
    private LoginService loginService;

    @Operation(summary = "login", description = "User Login")
    @PostMapping(value = "/login")
    public ResponseEntity<Void> login(HttpSession session, @RequestBody LoginRequest loginRequest) {
        if (!StringUtils.hasText(loginRequest.getUsername()) || !StringUtils.hasText(loginRequest.getPassword())) {
            throw new ServerException(ServerExceptionStatus.USERNAME_OR_PASSWORD_REQUIRED);
        }

        loginService.login(session, loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.success();
    }

    @Operation(summary = "test", description = "test")
    @GetMapping(value = "/test")
    public ResponseEntity<String> login(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return ResponseEntity.success("111");
    }
}
