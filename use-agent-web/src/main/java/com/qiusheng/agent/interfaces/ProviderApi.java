package com.qiusheng.agent.interfaces;

import com.qiusheng.agent.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author qiusheng
 * @Date 2021-5-30 13:23
 * @Version 1.0
 */
@FeignClient(name = "use-provier", path = "/v1/provider")
public interface ProviderApi {

    @GetMapping("/getUser")
    User getUser();

}
