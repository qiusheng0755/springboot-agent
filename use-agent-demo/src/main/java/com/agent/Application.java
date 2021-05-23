package com.agent;

import java.util.concurrent.TimeUnit;

public class Application {
    /**
     * 使用自定义agent探针
     * 在VM options添加，因为有空格，所以要加双引号
     * -javaagent:"D:\Workspaces\IntelliJ IDEA\springboot-agent\simple-agent\target\simple-agent-1.0.0.jar=abcdefg"
     *
     * 输出打印结果：
     * =========premain方法执行========
     * abcdefg
     * Hello World!
     *
     * @param args
     */
    public static void main( String[] args ) throws InterruptedException {
        System.out.println( "Hello World!" );
        TimeUnit.SECONDS.sleep(2);
    }
}
