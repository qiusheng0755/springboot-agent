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

import com.apm.agent.base64.Base64;
import com.apm.agent.conf.Constants;
import com.apm.agent.util.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * {@link ContextCarrier} is a data carrier of {@link TracingContext}. It holds the snapshot (current state) of {@link
 * TracingContext}.
 * <p>
 */
@Setter
@Getter
public class ContextCarrier implements Serializable {
    private String traceId;
    private String traceSegmentId;
    private int spanId = -1;
    private String parentService = Constants.EMPTY_STRING;
    private String parentServiceInstance = Constants.EMPTY_STRING;
    private String parentEndpoint;
    private String addressUsedAtClient;

    private CorrelationContext correlationContext = new CorrelationContext();
    private ExtensionContext extensionContext = new ExtensionContext();

    public CarrierItem items() {
        SW8ExtensionCarrierItem sw8ExtensionCarrierItem = new SW8ExtensionCarrierItem(extensionContext, null);
        SW8CorrelationCarrierItem sw8CorrelationCarrierItem = new SW8CorrelationCarrierItem(correlationContext, sw8ExtensionCarrierItem);
        SW8CarrierItem sw8CarrierItem = new SW8CarrierItem(this, sw8CorrelationCarrierItem);
        return new CarrierItemHead(sw8CarrierItem);
    }

    /**
     * Serialize this {@link ContextCarrier} to a {@link String}, with '|' split.
     *
     * @return the serialization string.
     */
    String serialize(HeaderVersion version) {
        if (this.isValid(version)) {
            return StringUtil.join(
                '-',
                "1",
                Base64.encode(this.getTraceId()),
                Base64.encode(this.getTraceSegmentId()),
                this.getSpanId() + "",
                Base64.encode(this.getParentService()),
                Base64.encode(this.getParentServiceInstance()),
                Base64.encode(this.getParentEndpoint()),
                Base64.encode(this.getAddressUsedAtClient())
            );
        }
        return "";
    }

    /**
     * Initialize fields with the given text.
     *
     * @param text carries {@link #traceSegmentId} and {@link #spanId}, with '|' split.
     */
    ContextCarrier deserialize(String text, HeaderVersion version) {
        if (text == null) {
            return this;
        }
        if (HeaderVersion.v3.equals(version)) {
            String[] parts = text.split("-", 8);
            if (parts.length == 8) {
                try {
                    // parts[0] is sample flag, always trace if header exists.
                    this.traceId = Base64.decode2UTFString(parts[1]);
                    this.traceSegmentId = Base64.decode2UTFString(parts[2]);
                    this.spanId = Integer.parseInt(parts[3]);
                    this.parentService = Base64.decode2UTFString(parts[4]);
                    this.parentServiceInstance = Base64.decode2UTFString(parts[5]);
                    this.parentEndpoint = Base64.decode2UTFString(parts[6]);
                    this.addressUsedAtClient = Base64.decode2UTFString(parts[7]);
                } catch (IllegalArgumentException ignored) {

                }
            }
        }
        return this;
    }

    public boolean isValid() {
        return isValid(HeaderVersion.v3);
    }

    /**
     * Make sure this {@link ContextCarrier} has been initialized.
     *
     * @return true for unbroken {@link ContextCarrier} or no-initialized. Otherwise, false;
     */
    boolean isValid(HeaderVersion version) {
        if (HeaderVersion.v3 == version) {
            return StringUtil.isNotEmpty(traceId)
                && StringUtil.isNotEmpty(traceSegmentId)
                && getSpanId() > -1
                && StringUtil.isNotEmpty(parentService)
                && StringUtil.isNotEmpty(parentServiceInstance)
                && StringUtil.isNotEmpty(parentEndpoint)
                && StringUtil.isNotEmpty(addressUsedAtClient);
        }
        return false;
    }

    public CorrelationContext getCorrelationContext() {
        return correlationContext;
    }

    public enum HeaderVersion {
        v3
    }
}
