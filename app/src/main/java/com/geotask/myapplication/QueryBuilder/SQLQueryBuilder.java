package com.geotask.myapplication.QueryBuilder;

import android.arch.persistence.db.SimpleSQLiteQuery;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;

import java.lang.reflect.Type;

public class SQLQueryBuilder {
    String query1;
    private Object[] object;

    public SQLQueryBuilder(Type type) {
        if(type.equals(Bid.class)){
            query1 = "SELECT * FROM bids ";
        } else if (type.equals(Task.class)) {
            query1 = "SELECT * FROM tasks ";
        }
    }

    public SimpleSQLiteQuery build() {
        return new SimpleSQLiteQuery(query1, object);
    }

    public void addColumns(String[] columns) {
        query1 += "WHERE ";
        for(String column : columns){
            query1 += column + " = ? AND ";
        }
        query1 = query1.substring(0, query1.length() - 4);
    }

    public void addColumns(String[] columns, String operator) {
        for(String column : columns){
            query1 += column + " " + operator + " ?";
        }
    }

    public void addParameters(Object[] object) {
        this.object = object;
    }
}
