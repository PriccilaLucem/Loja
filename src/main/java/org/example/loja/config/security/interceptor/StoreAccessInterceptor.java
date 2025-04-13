package org.example.loja.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.loja.config.security.JwtTokenProvider;
import org.example.loja.enums.TokenType;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class StoreAccessInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtTokenProvider provider;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object  handler) throws Exception {
        String token = request.getHeader("Authorization");
        String storeId = request.getRequestURI().split("/")[4];

        if (token == null || storeId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false;
        }

        try {
            TokenType type = provider.parseTypeOfToken(token);
            List<String> storeIds = provider.parseStoresFromToken(token, type.name());

            if ((type.name().equals("storeAdmin") || type.name().equals("manager")) && !storeIds.contains(storeId)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Unauthorized\"}");
                return false;
            }

            return true;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized\"}");
            return false;
        }
    }
}
