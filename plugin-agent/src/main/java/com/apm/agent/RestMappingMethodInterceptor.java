/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.apm.agent;

import com.apm.agent.context.ContextManager;
import com.apm.agent.context.trace.AbstractSpan;
import com.apm.agent.context.trace.component.OfficialComponent;
import com.apm.agent.plugin.interceptor.DeclaredInstanceMethodsInterceptPoint;
import com.apm.agent.plugin.interceptor.InstanceMethodsAroundInterceptor;
import com.apm.agent.plugin.interceptor.InstanceMethodsInterceptPoint;
import com.apm.agent.plugin.interceptor.enhance.EnhancedInstance;
import com.apm.agent.plugin.interceptor.enhance.MethodInterceptResult;
import com.apm.agent.plugin.match.ClassAnnotationMatch;
import com.apm.agent.plugin.match.ClassMatch;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.reflect.Method;

import static com.apm.agent.plugin.match.MethodInheritanceAnnotationMatcher.byMethodInheritanceAnnotationMatcher;
import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 对象实例方法拦截器
 */
public class RestMappingMethodInterceptor implements InstanceMethodsAroundInterceptor {

    @Override
    public ClassMatch enhanceClass() {
        String[] enhanceAnnotations = new String[] {"org.springframework.stereotype.Controller"};
        return ClassAnnotationMatch.byClassAnnotationMatch(enhanceAnnotations);
    }

    @Override
    public InstanceMethodsInterceptPoint getInstanceMethodsInterceptPoint() {
        return new DeclaredInstanceMethodsInterceptPoint() {
            @Override
            public ElementMatcher<MethodDescription> getMethodsMatcher() {
                return byMethodInheritanceAnnotationMatcher(named("org.springframework.web.bind.annotation.GetMapping"))
                        .or(byMethodInheritanceAnnotationMatcher(named("org.springframework.web.bind.annotation.PostMapping")))
                        .or(byMethodInheritanceAnnotationMatcher(named("org.springframework.web.bind.annotation.PutMapping")))
                        .or(byMethodInheritanceAnnotationMatcher(named("org.springframework.web.bind.annotation.DeleteMapping")))
                        .or(byMethodInheritanceAnnotationMatcher(named("org.springframework.web.bind.annotation.PatchMapping")));
            }

            @Override
            public String getMethodsInterceptor() {
                return "com.apm.agent.RestMappingMethodInterceptor";
            }

            @Override
            public boolean isOverrideArgs() {
                return false;
            }
        };
    }

    @Override
    public void beforeMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                             Class<?>[] argumentsTypes, MethodInterceptResult result) {

        AbstractSpan span = ContextManager.createLocalSpan("SpringCloudGateway/RoutingFilter");
        span.setComponent(new OfficialComponent(10000, "esbus-gateway"));
    }

    @Override
    public Object afterMethod(EnhancedInstance objInst, Method method, Object[] allArguments,
                              Class<?>[] argumentsTypes, Object ret) {

        ContextManager.stopSpan();
        return ret;
    }

    @Override
    public void handleMethodException(EnhancedInstance objInst, Method method, Object[] allArguments,
                                      Class<?>[] argumentsTypes, Throwable t) {

        ContextManager.activeSpan().errorOccurred().log(t);
    }
}
