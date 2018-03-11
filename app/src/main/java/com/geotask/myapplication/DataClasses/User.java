package com.geotask.myapplication.DataClasses;

import com.geotask.myapplication.Controllers.Helpers.EmailConverter;

import java.lang.reflect.Type;

public class User extends GTData{
    private String name;
    private String email;
    private String phonenum;

    public User(String name, String email, String phonenum){
        super.setType(User.class);
        this.name = name;
        this.email = EmailConverter.convertEmailForElasticSearch(email);
        this.phonenum = phonenum;
    }

    public User() {}

    public String getName(){
        return name;
    }

    public String getEmail(){
        return EmailConverter.revertEmailFromElasticSearch(email);
    }

    public void setEmail(String email){
        this.email = EmailConverter.convertEmailForElasticSearch(email);
    }

    public String getPhonenum(){
        return phonenum;
    }

    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    public Type getType() { return super.getType(); }

    //ToDo
    @Override
    public GTData loadFile() {
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }
}
