/**
 * Copyright (c) 2020-2030, Gsafety All rights reserved.
 */
package com.qiusheng.agent.config;

import com.alibaba.cloud.sentinel.feign.CustomHystrixFeign;
import com.alibaba.csp.sentinel.SphU;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

/**
 *
 * @Author qiusheng
 * @Date 2021-5-30 19:00
 * @Version 1.0
 */
@Configuration
public class CustomSentinelFeignConfiguration {

	/**
	 * 采用基于sentinel的自定义熔断器
	 * @return
	 */
	@Bean
	@Scope("prototype")
	@ConditionalOnClass({ SphU.class, Feign.class })
	@ConditionalOnProperty(name = "feign.sentinel.enabled")
	@Primary
	public Feign.Builder feignBuilder() {
		return CustomHystrixFeign.builder();
	}
}
