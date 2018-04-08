package com.geotask.myapplication.DataClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kehanwang on 2018/3/31.
 */

public class Photo extends GTData {
    private String TaskID;
    public List<byte[]> photolistbyte;

    public Photo(String TaskID, List<byte[]> photolist){
        super.setType(Photo.class.toString());
        this.TaskID = TaskID;
        this.photolistbyte = new ArrayList<>();
        super.setType(Photo.class.toString());
    }


    public List<byte[]> getPhotolist(){ return this.photolistbyte; }

    public void setPhotolist(List<byte[]> photolistbyte) {this.photolistbyte = photolistbyte;}

}
