package com.emamagic.pages;

import com.emamagic.model.Auth;
import com.emamagic.util.Navigable;
import com.emamagic.annotation.Page;
import com.emamagic.annotation.Param;

public class HomePage extends Navigable {

    @Param
    private String greeting;

    @Param
    private Auth auth;

    @Override
    public void display() {
        System.out.println(greeting.concat(auth.name));
    }

}
