package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.geotask.myapplication.Controllers.Helpers.UniqueIDGenerator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * parent class of all data classes that are exchanded with the server
 */
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

    /**
     *sets the type of the data class
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *gets the type of the data class
     */
    public String getType(){
        return type;
    }

    /**
     *sets the ID given by elasticsearch
     * @param ID
     */
    public void setObjectID(String ID) {
        this.objectID = ID;
    }

    /**
     *gets ID given by
     * @return ObjectID
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     *gets the data of the item
     * @return data
     */
    public long getDate() {
        return date;
    }

    /**
     * sets the date
     * @param date
     */
    public void setDate(long date) {
        this.date = date;
    }

    /**
     *gets a new date if no data is given
     * @param newDate
     */
    public void setDate(Date newDate){
        this.date = newDate.getTime();
    }

    /**
     *converts the date to a string
     * @return strDate
     */
    public String getDateString(){
        String strDate = new SimpleDateFormat("MMM d, yyyy").format(new java.util.Date((long)date));
        //String strDate = new SimpleDateFormat("EEEE MMMM d, yyyy").format(new java.util.Date((long)date));
        return strDate;
    }
}
