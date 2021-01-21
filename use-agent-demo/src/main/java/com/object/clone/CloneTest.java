package com.object.clone;

import cn.hutool.core.util.StrUtil;
import com.pojo.Animal;
import com.pojo.Dog;

public class CloneTest {

    public static void main(String[] args) {
        Dog dog = new Dog();
        dog.setValue(10);
        dog.setName("旺财");
        dog.setAge(2);
        runChild(dog);
    }

    public static void runChild(Animal animal) {
        Animal clone = animal.clone();

        animal.setName("小强");
        System.out.println(animal);
        System.out.println(StrUtil.format("dog Name={}, Age={}", animal.getName(), animal.getAge()));

        System.out.println(clone);
        System.out.println(StrUtil.format("clone Name={}, Age={}", clone.getName(), clone.getAge()));

    }
}
