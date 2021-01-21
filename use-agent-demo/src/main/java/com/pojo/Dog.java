package com.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dog extends Animal {

    private Integer value;

    @Override
    public void call() {
        System.out.println("旺旺……");
    }

}
