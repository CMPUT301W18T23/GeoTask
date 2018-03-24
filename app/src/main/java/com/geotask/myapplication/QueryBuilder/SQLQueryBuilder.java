package com.geotask.myapplication.QueryBuilder;

import android.arch.persistence.db.SimpleSQLiteQuery;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;

import java.lang.reflect.Type;

public class SQLQueryBuilder {
    String query1;
    private String[] object;

    public SQLQueryBuilder(Type type) {
        if(type.equals(Bid.class)){
            query1 = "SELECT * FROM bids WHERE ";
        } else if (type.equals(Task.class)) {
            query1 = "SELECT * FROM tasks WHERE ";
        }
    }



    public SimpleSQLiteQuery build() {
        return new SimpleSQLiteQuery(query1, object);
    }

    public void addColumns(String[] columns) {
        for(String column : columns){
            query1 += column + " = ?";
        }
    }

    public void addParameters(String[] object) {
        this.object = object;
    }
}
