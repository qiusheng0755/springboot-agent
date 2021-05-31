package com.qiusheng.agent.interfaces;

import com.qiusheng.agent.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author qiusheng
 * @Date 2021-5-30 13:23
 * @Version 1.0
 */
@FeignClient(name = "use-provier", path = "/v1/provider")
public interface ProviderApi {

    @GetMapping("/getUser")
    Result getUser(@RequestParam(value = "name") String name, @RequestParam(value = "phone") String phone);

}
