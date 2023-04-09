package org.apache.bigtop.manager.server.configuration;

import org.apache.bigtop.manager.server.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // Server APIs
                .excludePathPatterns("/login")
                // Swagger pages
                .excludePathPatterns("/swagger-ui/**", "/v3/**");
    }
}
