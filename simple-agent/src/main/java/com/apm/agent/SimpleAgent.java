package com.apm.agent;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * 简单版Agent探针
 */
public class SimpleAgent {

    private static final Logger logger = LoggerFactory.getLogger(SimpleAgent.class);

    /**
     * If true, Agent will save all instrumented classes files in `/debugging` folder.
     * Use these files in order to resolve compatible problem.
     */
    public static boolean IS_OPEN_DEBUGGING_CLASS = false;

    /**
     * 该方法在main方法之前运行，与main方法运行在同一个JVM中
     * 并被同一个System ClassLoader装载
     * 被统一的安全策略(security policy)和上下文(context)管理
     *
     * @param agentOps
     * @param instrumentation
     * @author SHANHY
     * @create  2016年3月30日
     */
    public static void premain(String agentOps, Instrumentation instrumentation) {
        System.out.println("=========premain方法执行========");
        System.out.println("可选的agent参数=" + agentOps);

        AgentBuilder.Transformer transformer = new AgentBuilder.Transformer() {
            @Override
            public DynamicType.Builder<?> transform(final DynamicType.Builder<?> builder,
                                                    final TypeDescription typeDescription,
                                                    final ClassLoader classLoader,
                                                    final JavaModule module) {
                /**
                 * method 指定哪些方法需要被拦截，ElementMatchers.any指定了所有的方法
                 * 声明intercept拦截器
                 */
                return builder
                        // 拦截任意方法
                        .method(ElementMatchers.<MethodDescription>any())
                        // 拦截到的方法委托给MyInterceptor
                        .intercept(MethodDelegation.to(MyInterceptor.class));
            }
        };

        /**
         * 1.type指定了agent拦截的报名，以com.agent作为前缀
         * 2.指定了transformer
         * 3.将配置安装到Instrumentation
         * Byte Buddy专门有个AgentBuilder来处理Java Agent的场景
         */
        final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(IS_OPEN_DEBUGGING_CLASS));
        new AgentBuilder.Default(byteBuddy)
                .ignore(nameStartsWith("net.bytebuddy.").or(nameStartsWith("org.slf4j."))
                        .or(nameStartsWith("org.groovy."))
                        .or(nameContains("javassist"))
                        .or(nameContains(".asm."))
                        .or(nameContains(".reflectasm."))
                        .or(nameStartsWith("sun.reflect"))
                        .or(ElementMatchers.isSynthetic())
                        .or(allCustomAgentExcludeToolkit())
                )
                // 根据包名前缀拦截类
                .type(ElementMatchers.nameStartsWith("com.agent"))
                // 拦截到的类由transformer处理
                .transform(transformer)
                // 设置已经被agent 加载到内存里面的类的重定义策略
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                // 注册监听器，创建agent的时候，会唤醒这个Listener。注意的是可以注册多个，如果早已经注册，也会重复唤醒，不会只唤醒一个
                .with(new Listener())
                .installOn(instrumentation);
    }

    private static ElementMatcher.Junction<NamedElement> allCustomAgentExcludeToolkit() {
        return nameStartsWith("com.apm.");
    }

    /**
     * 如果不存在 premain(String agentOps, Instrumentation inst)，则会执行 premain(String agentOps)
     *
     * @param agentOps
     * @author SHANHY
     * @create  2016年3月30日
     */
    public static void premain(String agentOps) {
        System.out.println("=========premain方法执行2========");
        System.out.println(agentOps);
    }


    private static class Listener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {

        }

        @Override
        public void onTransformation(final TypeDescription typeDescription,
                                     final ClassLoader classLoader,
                                     final JavaModule module,
                                     final boolean loaded,
                                     final DynamicType dynamicType) {
            if (logger.isDebugEnabled()) {
                logger.debug("On Transformation class {}.", typeDescription.getName());
            }
        }

        @Override
        public void onIgnored(final TypeDescription typeDescription,
                              final ClassLoader classLoader,
                              final JavaModule module,
                              final boolean loaded) {

        }

        @Override
        public void onError(final String typeName,
                            final ClassLoader classLoader,
                            final JavaModule module,
                            final boolean loaded,
                            final Throwable throwable) {
            logger.error("Enhance class " + typeName + " error.", throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
        }
    }
}
