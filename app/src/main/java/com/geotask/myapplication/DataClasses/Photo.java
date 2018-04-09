package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.geotask.myapplication.Controllers.Helpers.ByteConverter;

import java.util.List;

/**
 * Created by kehanwang on 2018/3/31.
 */

/**
 * Hold the lication of photo list and the taskid
 */
@Entity(tableName = "photos") //ToDo foreign key link to task so can cascade delete
public class Photo extends GTData {
    @ColumnInfo
    private String TaskID;
    @TypeConverters(ByteConverter.class)
    public List<byte[]> photolistbyte;

    /**
     *the constructor of the photo
     * @param TaskID
     * @param photolistbyte
     */
    public Photo(String TaskID, List<byte[]> photolistbyte){
        super.setType(Photo.class.toString());
        this.TaskID = TaskID;
        this.photolistbyte = photolistbyte;
        super.setType(Photo.class.toString());
        super.setClientOriginalFlag(true);
    }

    public String getTaskID() {
        return TaskID;
    }

    public void setTaskID(String taskID) {
        TaskID = taskID;
    }

    public List<byte[]> getPhotolistbyte() {
        return photolistbyte;
    }

    public void setPhotolistbyte(List<byte[]> photolistbyte) {
        this.photolistbyte = photolistbyte;
    }
}
