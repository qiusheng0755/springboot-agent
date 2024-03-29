package com.apm.agent;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 拦截器
 * @Author qiusheng
 * @Date 2021-1-9 14:26
 * @Version 1.0
 */
public class MyInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(MyInterceptor.class);

    /**
     * Intercept the target method.
     *
     * @param clazz        target class
     * @param allArguments all method arguments
     * @param method       method description.
     * @param callable     the origin call ref.
     * @return the return value of target static method.
     *
     * 类似于 AOP 的环绕切面,
     * 通过 @Origin 注入的 Method 就是目标方法的元信息
     * 通过 @SuperCall 注解注入的 Callable 实例可以调到被拦截的目标方法（即使目标方法带参数，这里也不用传哈）
     */
    @RuntimeType
    public static Object intercept(@Origin Class<?> clazz, @AllArguments Object[] allArguments,
                                   @Origin Method method, @SuperCall Callable<?> callable) throws Exception {

        logger.info("class[{}] method[{}] intercept", clazz, method.getName());

        Long start = System.currentTimeMillis();
        try {
            return callable.call(); // 执行原函数
        }finally {
            System.out.println(method.getName() + ":" + (System.currentTimeMillis()-start) + "ms");
        }
    }
}
