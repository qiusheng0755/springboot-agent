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

package com.apm.agent.plugin;

import com.apm.agent.plugin.bytebuddy.AbstractJunction;
import com.apm.agent.plugin.interceptor.InstanceMethodsAroundInterceptor;
import com.apm.agent.plugin.match.ClassMatch;
import com.apm.agent.plugin.match.IndirectMatch;
import com.apm.agent.plugin.match.NameMatch;
import com.apm.agent.plugin.match.ProtectiveShieldMatcher;
import lombok.Getter;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static net.bytebuddy.matcher.ElementMatchers.isInterface;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * The <code>PluginFinder</code> represents a finder , which assist to find the one from the given {@link
 * AbstractClassEnhancePluginDefine} list.
 */
public class PluginFinder {
    @Getter
    private final InstanceMethodsAroundInterceptor interceptor;

    private final Map<String, LinkedList<AbstractClassEnhancePluginDefine>> nameMatchDefine = new HashMap<String, LinkedList<AbstractClassEnhancePluginDefine>>();
//    private final List<AbstractClassEnhancePluginDefine> signatureMatchDefine = new ArrayList<AbstractClassEnhancePluginDefine>();

    public PluginFinder(InstanceMethodsAroundInterceptor interceptor) {
        this.interceptor = interceptor;

        ClassMatch match = interceptor.enhanceClass();
        if (match!=null && match instanceof NameMatch) {
            NameMatch nameMatch = (NameMatch) match;
            LinkedList<AbstractClassEnhancePluginDefine>pluginDefines = new LinkedList<AbstractClassEnhancePluginDefine>();
            nameMatchDefine.put(nameMatch.getClassName(), pluginDefines);
        }
    }

    public ElementMatcher<? super TypeDescription> buildMatch() {
        ElementMatcher.Junction judge = new AbstractJunction<NamedElement>() {
            @Override
            public boolean matches(NamedElement target) {
                return nameMatchDefine.containsKey(target.getActualName());
            }
        };
        judge = judge.and(not(isInterface()));

        ClassMatch match = interceptor.enhanceClass();
        if (match instanceof IndirectMatch) {
            judge = judge.or(((IndirectMatch) match).buildJunction());
        }

        return new ProtectiveShieldMatcher(judge);
    }

}
