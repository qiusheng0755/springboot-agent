import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 拦截器
 * @Author qiusheng
 * @Date 2021-1-9 14:26
 * @Version 1.0
 */
public class MyInterceptor {

    /**
     * 类似于 AOP 的环绕切面,
     * 通过 @Origin 注入的 Method 就是目标方法的元信息
     * 通过 @SuperCall 注解注入的 Callable 实例可以调到被拦截的目标方法（即使目标方法带参数，这里也不用传哈）
     */
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) throws Exception {

        Long start = System.currentTimeMillis();
        try {
            return callable.call(); // 执行原函数
        }finally {
            System.out.println(method.getName() + ":" + (System.currentTimeMillis()-start) + "ms");
        }
    }
}
