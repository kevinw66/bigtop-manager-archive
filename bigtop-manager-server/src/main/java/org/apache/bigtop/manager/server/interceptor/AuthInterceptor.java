package org.apache.bigtop.manager.server.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.exception.ServerException;
import org.apache.bigtop.manager.server.utils.JWTUtils;
import org.apache.bigtop.manager.server.utils.ThreadLocalUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        checkLogin(request);
        checkPermission();

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        clearStatus();

        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private void clearStatus() {
        ThreadLocalUtils.removeUserId();
    }

    private void checkLogin(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (StringUtils.isBlank(token)) {
            throw new ServerException(ServerExceptionStatus.NEED_LOGIN);
        }

        try {
            DecodedJWT decodedJWT = JWTUtils.resolveToken(token);
            ThreadLocalUtils.setUserId(decodedJWT.getClaim(JWTUtils.CLAIM_ID).asLong());
        } catch (Exception e) {
            throw new ServerException(ServerExceptionStatus.NEED_LOGIN);
        }
    }

    private void checkPermission() {
    }
}
