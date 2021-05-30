package com.qiusheng.agent.service;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qiusheng.agent.entity.User;
import lombok.AllArgsConstructor;
import org.apache.skywalking.apm.toolkit.trace.ActiveSpan;
import org.apache.skywalking.apm.toolkit.trace.Trace;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author qiusheng
 * @Date 2021-5-29 22:20
 * @Version 1.0
 */
@Service
@AllArgsConstructor
public class TraceService {

    private final StringRedisTemplate stringRedisTemplate;

    @Trace
    public Integer getLogCount(User user) {
        ActiveSpan.setOperationName("TraceService#getLogCount");  //Customize an operation name
        Integer count = 0;
        HttpResponse httpResponse = HttpUtil.createGet("http://172.18.8.112:8011/v1/gateway/transactionLog/count")
                .header("Authorization", "Bearer 2ff76f45-ea78-436d-813c-adb7cccdb902")
                .execute();
        if (httpResponse.getStatus() == 200) {
            JSONObject jsonObject = JSON.parseObject(httpResponse.body());
            count = jsonObject.getInteger("data");
        }

        HashOperations<String, String, String> stringHashOperations = stringRedisTemplate.opsForHash();
        String remark = stringHashOperations.get("srma:service:list", "1621247394797390");
        user.setRemark(remark);

        return count;
    }
}
