package com.ridesync_v2.ridesync.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaForwardingConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/login")
                .setViewName("forward:/index.html");

        registry.addViewController("/register")
                .setViewName("forward:/index.html");

        registry.addViewController("/about")
                .setViewName("forward:/index.html");

        registry.addViewController("/view")
                .setViewName("forward:/index.html");

        registry.addViewController("/upload")
                .setViewName("forward:/index.html");
    }
}
