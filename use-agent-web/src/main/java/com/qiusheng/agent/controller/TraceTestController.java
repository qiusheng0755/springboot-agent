package com.qiusheng.agent.controller;

import com.alibaba.fastjson.JSON;
import com.qiusheng.agent.entity.User;
import com.qiusheng.agent.interfaces.ProviderApi;
import com.qiusheng.agent.result.Result;
import com.qiusheng.agent.service.TraceService;
import lombok.AllArgsConstructor;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Tag;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * VM Optition设置agent参数
 * -javaagent:F:\skywalking\apache-skywalking-apm-es7-8.1.0\agent\skywalking-agent.jar -Dskywalking.agent.service_name=use-consume -Dskywalking.collector.backend_service=172.18.8.105:11800
 *
 * @Author qiusheng
 * @Date 2021-5-29 21:03
 * @Version 1.0
 */
@RestController
@AllArgsConstructor
public class TraceTestController {

    private final TraceService traceService;
    private final ProviderApi providerApi;

    @Trace
    @GetMapping(value = "/hello/{msg}")
    public String hello(@PathVariable String msg) {
        ActiveSpan.tag("hello, 路径参数是", msg);
        return "success";
    }

    @Trace
    @GetMapping(value = "/getTraceId")
    public String getTraceId() {
        return "ok, TraceId=" + TraceContext.traceId();
    }

    @Trace
    @GetMapping(value = "/sleep")
    public String sleep() throws InterruptedException {
        ActiveSpan.tag("sleep测试", "ok");
        TimeUnit.SECONDS.sleep(3);
        return "sleep 3s, TraceId=" + TraceContext.traceId();
    }

    /**
     * The codes below will generate a span,
     * and two types of tags,
     * one type tag: keys are `tag1` and `tag2`, values are the passed-in parameters, respectively,
     * the other type tag: keys are `username`  and `age`, values are the return value in User, respectively
     *
     * http://127.0.0.1:8080/getUser?name=zhangsan&phone=18102755410
     * 跨度信息
     * 标记.
     * 端点:TraceTestController#getUser
     * 跨度类型:Local
     * 组件:Unknown
     * Peer:No Peer
     * 失败:false
     * tag1:zhangsan
     * tag2:18102755410
     * name:zhangsan
     * age:18
     */
    @Trace
    @Tag(key = "param1", value = "arg[0]")
    @Tag(key = "param2", value = "arg[1]")
    @Tag(key = "traceId", value = "returnedObj.data.traceId")
    @Tag(key = "name", value = "returnedObj.data.name")
    @Tag(key = "age", value = "returnedObj.data.age")
    @GetMapping(value = "/getUser")
    public Result getUser(String name, String phone) {
        ActiveSpan.setOperationName("TraceTestController#getUser");  //Customize an operation name
//        User user = new User();
//        user.setTraceId(TraceContext.traceId());
//        user.setName(name);
//        user.setPhone(phone);
//        user.setAge(18);

        Result remoteResult = providerApi.getUser(name, phone);
        if (!remoteResult.isSuccess()) {
            return remoteResult;
        }
        LinkedHashMap map = (LinkedHashMap) remoteResult.getData();
        User user = JSON.parseObject(JSON.toJSONString(map), User.class);
        Integer logCount = traceService.getLogCount(user);
        user.setTraceId(TraceContext.traceId());
        user.setLogCount(logCount);
        System.out.println(user.toString());

        return Result.success(user);
    }
}
