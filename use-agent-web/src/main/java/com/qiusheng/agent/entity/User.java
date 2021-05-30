package com.qiusheng.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author qiusheng
 * @Date 2021-5-29 21:35
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -2991079560370444036L;

    private String traceId;
    private String name;
    private String phone;
    private Integer age;
    private String remark;
    private Integer logCount;

}
