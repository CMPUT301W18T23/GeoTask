package com.geotask.myapplication.DataClasses;

import java.io.Serializable;

public abstract class GTData implements Serializable{
    private transient String objectID;
    private transient String type;

    protected void setType(String type) {
        this.type = type;
    }

    public String getType(){
        return type;
    }

    public void setObjectID(String ID) {
        this.objectID = ID;
    }

    public String getObjectID() {
        return objectID;
    }
}
