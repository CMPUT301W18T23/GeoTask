package com.geotask.myapplication.Controllers.Helpers;

import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.lang.reflect.Type;

public class AsyncArgumentWrapper {

    private String ID;
    private Type type;
    private String searchQuery;

    public AsyncArgumentWrapper(String ID, Type type) {
        this.ID = ID;
        this.type = type;
    }

    public AsyncArgumentWrapper(SuperBooleanBuilder builder, Type type) {
        this.searchQuery = builder.toString();
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public Type getType() {
        return type;
    }

    public String getSearchQuery() {
        return searchQuery;
    }
}
