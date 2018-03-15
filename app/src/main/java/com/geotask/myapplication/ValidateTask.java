package com.geotask.myapplication;

/**
 * Created by james on 2018-03-14.
 */

public class ValidateTask {

    public Boolean checkText(String Title, String Description){
        if (Title.length() > 30 || Title.length() <=0 || Description.length() > 300 || Description.length() <= 0) {
            return false;
        }
        return true;
    }
}