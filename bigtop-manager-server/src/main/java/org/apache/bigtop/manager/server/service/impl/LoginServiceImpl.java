package org.apache.bigtop.manager.server.service.impl;

import jakarta.annotation.Resource;
import org.apache.bigtop.manager.server.enums.ApiExceptionEnum;
import org.apache.bigtop.manager.server.exception.ApiException;
import org.apache.bigtop.manager.server.model.dto.LoginDTO;
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
    public LoginVO login(LoginDTO loginDTO) {
        User user = userRepository.findByUsername(loginDTO.getUsername());
        if (user == null || !loginDTO.getPassword().equalsIgnoreCase(user.getPassword())) {
            throw new ApiException(ApiExceptionEnum.INCORRECT_USERNAME_OR_PASSWORD);
        }

        if (!user.getStatus()) {
            throw new ApiException(ApiExceptionEnum.USER_IS_DISABLED);
        }

        String token = JWTUtils.generateToken(user.getId(), user.getUsername());

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        return loginVO;
    }
}
