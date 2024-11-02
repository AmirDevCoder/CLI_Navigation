package com.emamagic.pages;

import com.emamagic.model.Auth;
import com.emamagic.util.Navigable;
import com.emamagic.annotation.Page;
import com.emamagic.navigator.Navigator;

public class LoginPage extends Navigable {

    @Override
    public void display() {
        Navigator.navToHomePage("hi ", new Auth("emamagic"));
    }

}
