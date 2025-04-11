package org.example.loja.config.security.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    org.example.loja.interceptor.StoreAccessInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/api/v1/store/**/product/**")
                .addPathPatterns("/api/v1/store/**/employees/**")
                .addPathPatterns("/api/v1/store/**/store-manager/**")
                .addPathPatterns("/api/v1/store/**")
                .excludePathPatterns("/api/v1/store/public/**");

    }
}
