package org.apache.bigtop.manager.server.interceptor;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.bigtop.manager.common.utils.JsonUtils;
import org.apache.bigtop.manager.server.enums.ServerExceptionStatus;
import org.apache.bigtop.manager.server.holder.SessionUserHolder;
import org.apache.bigtop.manager.server.utils.JWTUtils;
import org.apache.bigtop.manager.server.utils.ResponseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private ResponseEntity<?> responseEntity;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (checkLogin(request) && checkPermission()) {
            return HandlerInterceptor.super.preHandle(request, response, handler);
        } else {
            response.setHeader("Content-Type", "application/json; charset=UTF-8");
            response.getWriter().write(JsonUtils.writeAsString(responseEntity));
            return false;
        }
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
        SessionUserHolder.clear();
    }

    private Boolean checkLogin(HttpServletRequest request) {
        String token = request.getHeader("Token");
        if (StringUtils.isBlank(token)) {
            responseEntity = ResponseEntity.error(ServerExceptionStatus.NEED_LOGIN);
            return false;
        }

        try {
            DecodedJWT decodedJWT = JWTUtils.resolveToken(token);
            SessionUserHolder.setUserId(decodedJWT.getClaim(JWTUtils.CLAIM_ID).asLong());
        } catch (Exception e) {
            responseEntity = ResponseEntity.error(ServerExceptionStatus.NEED_LOGIN);
            return false;
        }

        return true;
    }

    private Boolean checkPermission() {
        return true;
    }
}
