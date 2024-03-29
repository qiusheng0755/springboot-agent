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

package com.apm.agent.plugin.interceptor;

import com.apm.agent.plugin.interceptor.enhance.MethodInterceptResult;
import com.apm.agent.plugin.match.ClassMatch;

import java.lang.reflect.Method;

/**
 * A interceptor, which intercept method's invocation.
 */
public interface InstanceMethodsAroundInterceptor {

    ClassMatch enhanceClass();

    InstanceMethodsInterceptPoint getInstanceMethodsInterceptPoint();

//    ElementMatcher<MethodDescription> getMethodsMatcher();

    /**
     * called before target method invocation.
     *
     * @param result change this result, if you want to truncate the method.
     */
    void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                      MethodInterceptResult result) throws Throwable;

    /**
     * called after target method invocation. Even method's invocation triggers an exception.
     *
     * @param ret the method's original return value. May be null if the method triggers an exception.
     * @return the method's actual return value.
     */
    Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes,
                       Object ret) throws Throwable;

    /**
     * called when occur exception.
     *
     * @param t the exception occur.
     */
    void handleMethodException(Object objInst, Method method, Object[] allArguments,
                               Class<?>[] argumentsTypes, Throwable t);
}
