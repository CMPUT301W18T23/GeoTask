package com.geotask.myapplication.DataClasses;

import java.io.Serializable;

public abstract class GTData implements Serializable{
    private transient String objectID;

    public String getType(){
        return "GTData";
    }

    public void setObjectID(String ID) {
        this.objectID = ID;
    }

    public String getObjectID() {
        return objectID;
    }
}
