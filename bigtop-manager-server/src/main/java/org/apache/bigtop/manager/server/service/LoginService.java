package org.apache.bigtop.manager.server.service;


import jakarta.servlet.http.HttpSession;
import org.apache.bigtop.manager.server.model.vo.LoginVO;

public interface LoginService {

    /**
     * Login by username and password
     *
     * @param session HttpSession
     * @param username username
     * @param password password
     */
    LoginVO login(HttpSession session, String username, String password);
}
