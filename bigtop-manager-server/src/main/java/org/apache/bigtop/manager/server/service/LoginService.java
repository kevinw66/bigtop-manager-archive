package org.apache.bigtop.manager.server.service;


import org.apache.bigtop.manager.server.model.dto.LoginDTO;
import org.apache.bigtop.manager.server.model.vo.LoginVO;

public interface LoginService {

    /**
     * Login by username and password
     *
     * @param loginDTO loginDTO
     */
    LoginVO login(LoginDTO loginDTO);
}
