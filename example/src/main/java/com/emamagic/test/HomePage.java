package com.emamagic.test;

import com.emamagic.Navigable;
import com.emamagic.annotation.Page;
import com.emamagic.navigator.Navigator;

@Page
public class HomePage implements Navigable {


    @Override
    public void display() {
        Navigator.navToAdminPage(22, "emamagic here");
    }

}
