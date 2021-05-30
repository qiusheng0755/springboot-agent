package com.qiusheng.provider.controller;

import com.qiusheng.provider.entity.User;
import lombok.AllArgsConstructor;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * VM Optition设置agent参数
 * -javaagent:F:\skywalking\apache-skywalking-apm-es7-8.1.0\agent\skywalking-agent.jar -Dskywalking.agent.service_name=use-provider -Dskywalking.collector.backend_service=172.18.8.105:11800
 *
 * @Author qiusheng
 * @Date 2021-5-29 21:03
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping(value = "/v1/provider")
public class ProviderController {

    /**
     *
     */
    @Trace
    @Tag(key = "tag1", value = "arg[0]")
    @Tag(key = "tag2", value = "arg[1]")
    @Tag(key = "name", value = "returnedObj.name")
    @Tag(key = "age", value = "returnedObj.age")
    @GetMapping(value = "/getUser")
    public User getUser(String name, String phone) {
        ActiveSpan.setOperationName("TraceTestController#getUser");  //Customize an operation name
        User user = new User();
        user.setTraceId(TraceContext.traceId());
        user.setName(name);
        user.setPhone(phone);
        user.setAge(18);
        System.out.println(user.toString());

        return user;
    }
}
