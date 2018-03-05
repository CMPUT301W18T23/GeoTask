package com.geotask.myapplication.DataClasses;

import java.io.Serializable;

public abstract class GTData implements Serializable{
    //template for GTData superclass for all data classes
    public String getType(){
        return "GTData";
    }
}
