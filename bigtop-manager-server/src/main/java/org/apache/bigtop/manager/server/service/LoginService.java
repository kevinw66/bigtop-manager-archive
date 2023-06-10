package org.apache.bigtop.manager.server.service;

import javax.servlet.http.HttpSession;

public interface LoginService {

    /**
     * Login by username and password
     *
     * @param session HttpSession
     * @param username username
     * @param password password
     */
    void login(HttpSession session, String username, String password);
}
