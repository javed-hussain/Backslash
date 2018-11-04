package com.jhbros.backslash.models;

/*
 * Created by javed on 11/4/2018
 */

public class DrawerMenuModel {

    public String menuName;
    public boolean hasChildren, isGroup;

    public DrawerMenuModel(String menuName, boolean isGroup, boolean hasChildren) {

        this.menuName = menuName;
        this.isGroup = isGroup;
        this.hasChildren = hasChildren;
    }
}
