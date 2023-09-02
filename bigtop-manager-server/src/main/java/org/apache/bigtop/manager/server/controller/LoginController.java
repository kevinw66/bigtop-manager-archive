package org.apache.bigtop.manager.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.req.LoginReq;
import org.apache.bigtop.manager.server.model.vo.LoginVO;
import org.apache.bigtop.manager.server.service.LoginService;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.apache.bigtop.manager.server.utils.ThreadLocalUtils;
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
    public ResponseEntity<LoginVO> login(HttpSession session, @RequestBody LoginReq loginReq) {
        if (!StringUtils.hasText(loginReq.getUsername()) || !StringUtils.hasText(loginReq.getPassword())) {
            throw new ServerException(ServerExceptionStatus.USERNAME_OR_PASSWORD_REQUIRED);
        }

        ;
        return ResponseEntity.success(loginService.login(session, loginReq.getUsername(), loginReq.getPassword()));
    }

    @Operation(summary = "test", description = "test")
    @GetMapping(value = "/test")
    public ResponseEntity<String> test() {
        Long userId = ThreadLocalUtils.getUserId();
//        throw new ServerException(ServerExceptionStatus.USERNAME_OR_PASSWORD_REQUIRED);
        return ResponseEntity.success("111");
    }
}
