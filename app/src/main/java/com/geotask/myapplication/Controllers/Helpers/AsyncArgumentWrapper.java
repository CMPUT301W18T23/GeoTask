package com.geotask.myapplication.Controllers.Helpers;

import android.arch.persistence.db.SimpleSQLiteQuery;

import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.lang.reflect.Type;

/**
 * Used to pass multiple arguments to AsyncTask
 */
public class AsyncArgumentWrapper {

    private SimpleSQLiteQuery SQLQuery;
    private String ID;
    private Type type;
    private String searchQuery;

    /**
     * Use for all elasticsearch functions except search
     * @param ID ID of ? extends GTData
     * @param type specific type of output, not check, not safe to use wrong
     */
    public AsyncArgumentWrapper(String ID, Type type) {
        this.ID = ID;
        this.type = type;
    }

    /**
     * Use for search
     * @param builder contains the query string
     * @param type specfic type of output, not check, not safe to use wrong
     */
    public AsyncArgumentWrapper(SuperBooleanBuilder builder, Type type) {
        this.searchQuery = builder.toString();
        this.type = type;
    }

    public AsyncArgumentWrapper(SQLQueryBuilder builder, Type type) {
        this.SQLQuery = builder.build();
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

    public SimpleSQLiteQuery getSQLQuery() {
        return SQLQuery;
    }
}
