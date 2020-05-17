package com.huijianzhu.heqing.configuration;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 配置跨域请求并可以处理Cookie
 */
@Configuration
public class PortalCorsFilter extends CorsFilter{
    private static Logger logger = LoggerFactory.getLogger(PortalCorsFilter.class);
    public PortalCorsFilter() {
        super(configurationSource());
    }
    private static UrlBasedCorsConfigurationSource configurationSource() {
        logger.info("init CorsFilter...");
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); //支持是否允许用户发送、处理 cookie
        config.addAllowedOrigin("*");     //支持将对应的响应信息返回给客户端
        config.addAllowedHeader("*");
        config.setMaxAge(36000L);
        config.setAllowedMethods(Arrays.asList("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
