package org.apache.bigtop.manager.server.service.impl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.orm.entity.User;
import org.apache.bigtop.manager.server.orm.repository.UserRepository;
import org.apache.bigtop.manager.server.service.LoginService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;

    @Override
    public void login(HttpSession session, String username, String password) {
        User user = userRepository.findByUsername(username).orElse(new User());
        String hex = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!hex.equalsIgnoreCase(user.getPassword())) {
            throw new ServerException(ServerExceptionStatus.INCORRECT_USERNAME_OR_PASSWORD);
        }

        session.setAttribute("user", user);
    }
}
