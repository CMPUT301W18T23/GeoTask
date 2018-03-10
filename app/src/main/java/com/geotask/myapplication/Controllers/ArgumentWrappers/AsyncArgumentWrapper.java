package com.geotask.myapplication.Controllers.ArgumentWrappers;

import java.lang.reflect.Type;

public class AsyncArgumentWrapper {

    private String ID;
    private Type type;

    public AsyncArgumentWrapper(String ID, Type type) {
        this.ID = ID;
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public Type getType() {
        return type;
    }
}
