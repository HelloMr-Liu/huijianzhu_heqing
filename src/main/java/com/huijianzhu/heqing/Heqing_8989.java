package com.huijianzhu.heqing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ================================================================
 * 说明：当前类说说明
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  14:10            创建
 * =================================================================
 **/
@EnableScheduling   //开启定时服务
@SpringBootApplication
@MapperScan({"com.huijianzhu.heqing.mapper"})
public class Heqing_8989 {

    public static void main(String[] args) {
        SpringApplication.run(Heqing_8989.class);
    }
    @Bean
    //由于 @PropertySource 不支持yml文件的对象转换 默认支持properties
    //所以手动配置自定义 yml文件
    public static PropertySourcesPlaceholderConfigurer properties() {
        PropertySourcesPlaceholderConfigurer configurer = new
                PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("databaseConfig.yml"));
        configurer.setProperties(yaml.getObject());
        return configurer;
    }
}
    
    
    