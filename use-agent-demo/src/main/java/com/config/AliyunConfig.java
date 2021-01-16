package com.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 在Configuration配置类里直接读取properties信息
 */
@Data
@Slf4j
@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:aliyun.properties")
@ConfigurationProperties(prefix = "aliyun")
public class AliyunConfig implements SmartLifecycle {

    private String accessKeyId;
    private String accessKeySecret;


    @Override
    public void start() {
        log.info("accessKeyId={}", accessKeyId);
        log.info("accessKeySecret={}", accessKeySecret);
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}

