package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class GTData implements Serializable {

    public long getId() {
        return id;
    }

    //ToDo put in some ID voodoo
    @PrimaryKey
    @ColumnInfo(name = "local_id")
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    @ColumnInfo(name = "object_id")
    private String objectID = "";

    @Ignore
    private transient Type type;

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
