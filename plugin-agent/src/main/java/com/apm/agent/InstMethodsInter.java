package com.apm.agent;

import com.apm.agent.plugin.interceptor.InstanceMethodsAroundInterceptor;
import com.apm.agent.plugin.interceptor.enhance.MethodInterceptResult;
import net.bytebuddy.implementation.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 对象实例拦截器
 * @Author qiusheng
 * @Date 2021-1-9 14:26
 * @Version 1.0
 */
public class InstMethodsInter {
    private static final Logger logger = LoggerFactory.getLogger(InstMethodsInter.class);

    private InstanceMethodsAroundInterceptor interceptor;

    public InstMethodsInter(InstanceMethodsAroundInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    /**
     * Intercept the target instance method.
     *
     * @param obj          target class instance.
     * @param allArguments all method arguments
     * @param method       method description.
     * @param zuper        the origin call ref.
     * @return the return value of target instance method.
     * @throws Exception only throw exception because of zuper.call() or unexpected exception in sky-walking ( This is a
     *                   bug, if anything triggers this condition ).
     *
     * 类似于 AOP 的环绕切面,
     * 通过 @Origin 注入的 Method 就是目标方法的元信息
     * 通过 @SuperCall 注解注入的 Callable 实例可以调到被拦截的目标方法（即使目标方法带参数，这里也不用传哈）
     */
    @RuntimeType
    public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperCall Callable<?> zuper,
                            @Origin Method method) throws Throwable {

        logger.info("class[{}], method[{}] intercept", obj.getClass().getName(), method.getName());
//        EnhancedInstance targetObject = (EnhancedInstance) obj;

        MethodInterceptResult result = new MethodInterceptResult();
        try {
            interceptor.beforeMethod(obj, method, allArguments, method.getParameterTypes(), result);
        } catch (Throwable t) {
            logger.error("class[{}] before method[{}] intercept failure", obj.getClass(), method.getName(), t);
        }

        Object ret = null;
        try {
            if (!result.isContinue()) {
                ret = result._ret();
            } else {
                ret = zuper.call();
            }
        } catch (Throwable t) {
            try {
                interceptor.handleMethodException(obj, method, allArguments, method.getParameterTypes(), t);
            } catch (Throwable t2) {
                logger.error("class[{}] handle method[{}] exception failure", obj.getClass(), method.getName(), t2);
            }
            throw t;
        } finally {
            try {
                ret = interceptor.afterMethod(obj, method, allArguments, method.getParameterTypes(), ret);
            } catch (Throwable t) {
                logger.error("class[{}] after method[{}] intercept failure", obj.getClass(), method.getName(), t);
            }
        }
        return ret;

//        Long start = System.currentTimeMillis();
//        try {
//            return zuper.call(); // 执行原函数
//        }finally {
//            System.out.println(method.getName() + ":" + (System.currentTimeMillis()-start) + "ms");
//        }
    }
}
