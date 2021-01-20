package com.object.clone;

import cn.hutool.core.util.StrUtil;
import com.pojo.Dog;

public class CloneTest {

    public static void main(String[] args) {
        Dog dog = new Dog();
        dog.setValue(10);
        dog.setName("旺财");
        dog.setAge(2);

        Dog clone = dog.clone();    //进行对象clone浅克隆

        dog.setName("小强");
        dog.setValue(113);
        System.out.println(dog);
        System.out.println(StrUtil.format("dog Name={}, Age={}, Value={}", dog.getName(), dog.getAge(), dog.getValue()));

        System.out.println(clone);
        System.out.println(StrUtil.format("clone Name={}, Age={}, Value={}", clone.getName(), clone.getAge(), clone.getValue()));

    }
}
