package org.apache.bigtop.manager.server.service;

import org.apache.bigtop.manager.server.orm.entity.User;

import javax.servlet.http.HttpSession;

public interface LoginService {

    void login(HttpSession session, String username, String password);
}
