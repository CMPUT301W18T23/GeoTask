package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.geotask.myapplication.Controllers.Helpers.UniqueIDGenerator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class GTData implements Serializable{

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "object_id")
    private String objectID = UniqueIDGenerator.generate();
    @ColumnInfo
    private String type;
    @ColumnInfo
    private long date;
    //ToDo refactor date from children


    public void setType(String type) {
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

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setDate(Date newDate){
        this.date = newDate.getTime();
    }

    public String getDateString(){
        String strDate = new SimpleDateFormat("EEEE MMMM d, yyyy").format(new java.util.Date((long)date));
        return strDate;
    }
}
