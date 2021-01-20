package com.pojo;

import cn.hutool.core.clone.CloneRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import cn.hutool.core.clone.Cloneable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dog extends Animal implements Cloneable<Dog> {

    private Integer value;

    @Override
    public void call() {
        System.out.println("旺旺……");
    }

    @Override
    public Dog clone() {
        try {
            return (Dog) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new CloneRuntimeException(e);
        }
    }
}
