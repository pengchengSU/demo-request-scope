package com.example.demo.filter;

import static com.example.demo.scope.Scope.beginScope;
import static com.example.demo.scope.Scope.endScope;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.auth.AuthScope;

@Lazy
@Order(0)
@Service("scopeFilter")
public class ScopeFilter extends OncePerRequestFilter {

    @Override
    protected String getAlreadyFilteredAttributeName() {
        return this.getClass().getName();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 开启Scope
        beginScope();
        try {
            Cookie[] cookies = request.getCookies();
            String loginUser = "unknownUser";
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("login_user")) {
                        loginUser = cookie.getValue();
                        break;
                    }
                }
            }

            // 设置该 Request 上下文对用的登陆用户
            AuthScope.setLoginUser(loginUser);

            filterChain.doFilter(request, response);
        } finally {
            // 关闭Scope
            endScope();
        }
    }
}
