package com.geotask.myapplication;


public class UserEntryStringValidator {

    /**
     * This method is used to validate input for a new task title and description
     */
    public Boolean checkText(String Title, String Description){
        if (Title.length() > 30 || Title.length() <=0 || Description.length() > 300 || Description.length() <= 0) {
            return false;
        }
        return true;
    }
}
