package com.apm.agent;

import com.apm.agent.context.ContextManager;
import com.apm.agent.context.trace.AbstractSpan;
import com.apm.agent.context.trace.component.OfficialComponent;
import com.apm.agent.plugin.interceptor.InstanceMethodsAroundInterceptor;
import com.apm.agent.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.apm.agent.plugin.interceptor.enhance.MethodInterceptResult;
import com.apm.agent.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;

import static com.apm.agent.plugin.bytebuddy.ArgumentTypeNameMatch.takesArgumentWithType;
import static com.apm.agent.plugin.match.NameMatch.byName;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * @Author qiusheng
 * @Date 2021-5-28 15:49
 * @Version 1.0
 */
public class NettyRoutingFilterInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public ClassMatch enhanceClass() {
        return byName("org.springframework.cloud.gateway.filter.NettyRoutingFilter");
    }

    @Override
    public InstanceMethodsInterceptPoint getInstanceMethodsInterceptPoint() {
        return new InstanceMethodsInterceptPoint() {
            @Override
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return named("filter").and(
                        takesArgumentWithType(0, "org.springframework.web.server.ServerWebExchange"));
            }

            @Override
            public String getMethodsInterceptor() {
                return "com.apm.agent.NettyRoutingFilterInterceptor";
            }

            @Override
            public boolean isOverrideArgs() {
                return true;
            }
        };
    }

    @Override
    public void beforeMethod(Object objInst, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes, MethodInterceptResult result) {

//        ServerWebExchange exchange = (ServerWebExchange) allArguments[0];
//        EnhancedInstance enhancedInstance = getInstance(exchange);

        AbstractSpan span = ContextManager.createLocalSpan("SpringCloudGateway/RoutingFilter");
//        if (enhancedInstance != null && enhancedInstance.getSkyWalkingDynamicField() != null) {
//            ContextManager.continued((ContextSnapshot) enhancedInstance.getSkyWalkingDynamicField());
//        }
        span.setComponent(new OfficialComponent(10000, "esbus-gateway"));
    }

    @Override
    public Object afterMethod(Object objInst, Method method, Object[] allArguments,
                              Class<?>[] argumentsTypes, Object ret) {

        ContextManager.stopSpan();
        return ret;
    }

    @Override
    public void handleMethodException(Object objInst, Method method, Object[] allArguments,
                                      Class<?>[] argumentsTypes, Throwable t) {

        ContextManager.activeSpan().errorOccurred().log(t);
    }
}
