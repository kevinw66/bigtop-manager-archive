package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.model.vo.LoginVO;
import org.apache.bigtop.manager.server.orm.entity.User;
import org.apache.bigtop.manager.server.orm.repository.UserRepository;
import org.apache.bigtop.manager.server.service.LoginService;
import org.apache.bigtop.manager.server.utils.JWTUtils;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private UserRepository userRepository;

    @Override
    public LoginVO login(HttpSession session, String username, String password) {
        User user = userRepository.findByUsername(username).orElse(new User());
        if (!password.equalsIgnoreCase(user.getPassword())) {
            throw new ServerException(ServerExceptionStatus.INCORRECT_USERNAME_OR_PASSWORD);
        }

        String token = JWTUtils.generateToken(user.getId(), user.getUsername());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        return loginVO;
    }
}
