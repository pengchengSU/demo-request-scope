package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.AuthScope;
import com.example.demo.scope.ScopeThreadPoolExecutor;

@Slf4j
@RestController
@RequestMapping("/rest")
public class UserController {

    // curl --location --request GET 'localhost:8080/rest/getLoginUser' --header 'Cookie: login_user=zhangsan'
    @GetMapping("/getLoginUser")
    public String getLoginUser() {
        return AuthScope.getLoginUser();
    }

    // curl --location --request GET 'localhost:8080/rest/getLoginUserInThreadPool' --header 'Cookie: login_user=zhangsan'
    @GetMapping("/getLoginUserInThreadPool")
    public String getLoginUserInThreadPool() {
        ScopeThreadPoolExecutor executor = ScopeThreadPoolExecutor.newFixedThreadPool(4);
        executor.execute(() -> {
            log.info("get login user in thread pool: {}", AuthScope.getLoginUser());
        });

        return AuthScope.getLoginUser();
    }
}
