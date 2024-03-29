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

package com.apm.agent.context;

public class SW8ExtensionCarrierItem extends CarrierItem {
    public static final String HEADER_NAME = "sw8-x";
    private final ExtensionContext extensionContext;

    public SW8ExtensionCarrierItem(ExtensionContext extensionContext, CarrierItem next) {
        super(HEADER_NAME, extensionContext.serialize(), next);
        this.extensionContext = extensionContext;
    }

    @Override
    public void setHeadValue(String headValue) {
        this.extensionContext.deserialize(headValue);
    }

}
