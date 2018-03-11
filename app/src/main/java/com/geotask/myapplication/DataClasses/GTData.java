package com.geotask.myapplication.DataClasses;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;

public abstract class GTData implements Serializable {
    private String objectID = "";
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

    //ToDo body
    public void saveFile(Context context) {

    }

    public abstract GTData loadFile();
}
