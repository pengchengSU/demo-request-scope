package com.example.demo.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.filter.ScopeFilter;

@Slf4j
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ScopeFilter> scopeFilterRegistration() {
        FilterRegistrationBean<ScopeFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ScopeFilter());
        registration.addUrlPatterns("/rest/*");
        registration.setOrder(0);
        log.info("scope filter registered");
        return registration;
    }
}
