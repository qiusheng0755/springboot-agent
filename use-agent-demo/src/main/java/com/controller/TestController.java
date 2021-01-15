package com.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试服务是否启动成功
 *
 * @Author qiusheng
 * @Date 2021-1-15 10:44
 * @Version 1.0
 */
@RestController
public class TestController {

    @GetMapping(value = "/test")
    public String test() {
        return "test 123";
    }
}
