package com.geotask.myapplication.QueryBuilder;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.util.Log;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;

import java.lang.reflect.Type;

public class SQLQueryBuilder implements Cloneable {
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
        if(query1.length() < 21) {
            query1 += "WHERE ";
        } else {
            query1 += "AND ";
        }
        for(String column : columns){
            query1 += column + " = ? AND ";
        }
        query1 = query1.substring(0, query1.length() - 4);
    }

    public void addColumns(String[] columns, String operator) {
        if(query1.length() < 21) {
            query1 += "WHERE ";
        } else {
            query1 += "AND ";
        }
        for(String column : columns){
            query1 += column + " " + operator + " ?";
        }
    }

    public void addParameters(Object[] object) {
        if (this.object == null) {
            this.object = object;
        } else {
            String[] temp = new String[this.object.length + object.length];
            System.arraycopy(this.object, 0, temp, 0, this.object.length);
            System.arraycopy(object, 0, temp, this.object.length, object.length);
            this.object = temp;

            Log.d("dd", "dd");
        }
    }

    //DO NOT USE FOR ANYTHING BUT TASK BUILDERS
    @Override
    public SQLQueryBuilder clone() {
        try {
            return (SQLQueryBuilder) super.clone();
        } catch(CloneNotSupportedException e) {
            return new SQLQueryBuilder(Task.class);
        }
    }
}
