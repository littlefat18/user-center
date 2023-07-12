package com.dzl.usercenter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author dzl
 * @date 2022/7/22 10:22
 */
//@Configuration
//public class GlobalCorsConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        // 覆盖所有请求
//        registry.addMapping("/**")
//                // 允许发送 Cookie
//                .allowCredentials(true)
//                // 放行哪些域名（必须用 patterns，否则 * 会和 allowCredentials 冲突）
//                .allowedOriginPatterns("http://genius.zhaogeban.love/", "http://genius-backend.zhaogeban.love/", "http://124.221.33.157:8080/")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .exposedHeaders("*")
//                .maxAge(3600);
//    }
//}

