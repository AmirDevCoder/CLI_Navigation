package com.emamagic.test;

import com.emamagic.Navigable;
import com.emamagic.annotation.Page;
import com.emamagic.annotation.Param;

@Page
public class AdminPage implements Navigable {

    @Param
    private Integer age;

    @Param
    private String adminName;

    @Override
    public void display() {
        System.out.println("hi  ".concat(adminName).concat(" age: ").concat(String.valueOf(age)));
    }


}
