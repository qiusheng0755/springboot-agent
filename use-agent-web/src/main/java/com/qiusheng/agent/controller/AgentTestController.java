package com.qiusheng.agent.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * VM Optition设置agent参数
 * -javaagent:"D:\Workspaces\IntelliJ IDEA\springboot-agent\plugin-agent\target\plugin-agent-1.0.0.jar=abcdefg"
 *
 * @Author qiusheng
 * @Date 2021-5-28 17:08
 * @Version 1.0
 */
@RestController
public class AgentTestController {

    @GetMapping(value = "/test")
    public String test() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return "test 123";
    }

    @RequestMapping(value = "/test2", method = RequestMethod.GET)
    public String test2() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2);
        return "test2 2222";
    }

}
