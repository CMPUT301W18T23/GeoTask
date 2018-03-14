package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;
import android.support.annotation.NonNull;

import com.geotask.myapplication.Controllers.Helpers.UniqueIDGenerator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class GTData implements Serializable{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "object_id")
    private String objectID = UniqueIDGenerator.generate();
    @Ignore
    private transient Type type;
    //ToDo refactor date from children


    public void setType(Type type) {
        this.type = type;
    }

    public Type getType(){
        return type;
    }

    public void setObjectID(String ID) {
        this.objectID = ID;
    }

    public String getObjectID() {
        return objectID;
    }
}
