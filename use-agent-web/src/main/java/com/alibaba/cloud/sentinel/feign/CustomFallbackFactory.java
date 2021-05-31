/**
 * Copyright (c) 2020-2030, Gsafety All rights reserved.
 */
package com.alibaba.cloud.sentinel.feign;

import cn.hutool.core.util.StrUtil;
import com.qiusheng.agent.result.Result;
import feign.Target;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * 自定义FallbackFactory
 * cglig实现动态代理
 */
@Slf4j
public class CustomFallbackFactory<T> implements FallbackFactory<T>, MethodInterceptor {
	private final Target<T> target;
	private final Class<T> targetType;
	private final String targetName;
	private Throwable cause;

	public CustomFallbackFactory(Target target) {
		this.target = target;
		this.targetType = target.type();
		this.targetName = target.name();
	}

	@Override
	@SuppressWarnings("unchecked")
	public T create(Throwable cause) {
		this.cause = cause;

		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetType);
		enhancer.setUseCache(true);
		// 代理回调方法
		enhancer.setCallback(this);
//		enhancer.setCallback(new CustomMethodInterceptor<>(targetType, targetName, cause));
		// 创建代理对象
		return (T) enhancer.create();
	}

	/**
	 * 代理回调方法
	 */
	@Nullable
	@Override
	public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		log.error("服务:【{}】，类名【{}】，方法【{}】 出现异常:\n", targetName, targetType.getName(), method.getName(), cause);

		Result<Object> error = Result.error(StrUtil.format("服务【{}.{}】被熔断，原因是【{}】",
				targetType.getName(),
				method.getName(),
				cause.getMessage()));

		return error;
	}

}
