package org.apache.bigtop.manager.server.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.bigtop.manager.server.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@RequiredArgsConstructor
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // Server APIs
                .excludePathPatterns("/login")
                // Swagger pages
                .excludePathPatterns("/swagger-ui/**", "/v3/**", "/swagger-ui.html");
    }
}
