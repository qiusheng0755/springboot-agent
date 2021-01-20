package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author qiusheng
 * @Date 2021-1-15 10:37
 * @Version 1.0
 */
@SpringBootApplication
public class SpringbootApplication {

	//POSTMAN测试  http://localhost:8080/graphql   body->raw 参数 {"query": "query {getUserById(id:1) {id,name,age,card{cardNumber}}}"}
    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}
