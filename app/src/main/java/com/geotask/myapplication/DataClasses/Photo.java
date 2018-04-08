package com.geotask.myapplication.DataClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kehanwang on 2018/3/31.
 */

/**
 * Hold the lication of photo list and the taskid
 */

public class Photo extends GTData {
    private String TaskID;
    public List<byte[]> photolistbyte;

    /**
     *the constructor of the photo
     * @param TaskID
     * @param photolist
     */
    public Photo(String TaskID, List<byte[]> photolist){
        super.setType(Photo.class.toString());
        this.TaskID = TaskID;
        this.photolistbyte = new ArrayList<>();
        super.setType(Photo.class.toString());
    }

    /**
     * return the list<byte>
     * @return
     */

    public List<byte[]> getPhotolist(){ return this.photolistbyte; }


    /**
     * reset the list<byte></byte>
     * @param photolistbyte
     */

    public void setPhotolist(List<byte[]> photolistbyte) {this.photolistbyte = photolistbyte;}

}
