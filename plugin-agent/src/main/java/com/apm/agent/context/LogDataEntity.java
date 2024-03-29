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

import com.apm.agent.context.util.KeyValuePair;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The <code>LogDataEntity</code> represents a collection of {@link KeyValuePair}, contains several fields of a logging
 * operation.
 */
public class LogDataEntity {
    private long timestamp;
    private List<KeyValuePair> logs;

    private LogDataEntity(long timestamp, List<KeyValuePair> logs) {
        this.timestamp = timestamp;
        this.logs = logs;
    }

    public List<KeyValuePair> getLogs() {
        return logs;
    }

    public static class Builder {
        protected List<KeyValuePair> logs;

        public Builder() {
            logs = new LinkedList<>();
        }

        public Builder add(KeyValuePair... fields) {
            Collections.addAll(logs, fields);
            return this;
        }

        public LogDataEntity build(long timestamp) {
            return new LogDataEntity(timestamp, logs);
        }
    }

//    public Log transform() {
//        Log.Builder logMessageBuilder = Log.newBuilder();
//        for (KeyValuePair log : logs) {
//            logMessageBuilder.addData(log.transform());
//        }
//        logMessageBuilder.setTime(timestamp);
//        return logMessageBuilder.build();
//    }
}
