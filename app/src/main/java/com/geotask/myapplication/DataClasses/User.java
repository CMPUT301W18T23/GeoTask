package com.geotask.myapplication.DataClasses;

import android.content.Context;

import com.geotask.myapplication.Controllers.ElasticsearchController;

import java.lang.reflect.Type;

public class User extends GTData{
    private String name;
    private String email;
    private String phonenum;

    public User(String name, String email, String phonenum){
        super.setType(User.class);
        this.name = name;
        this.email = ElasticsearchController.convertEmailForElasticSearch(email);
        this.phonenum = phonenum;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return ElasticsearchController.revertEmailFromElasticSearch(email);
    }

    public void setEmail(String email){
        this.email = ElasticsearchController.convertEmailForElasticSearch(email);
    }

    public String getPhonenum(){
        return phonenum;
    }

    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    public Type getType() { return super.getType(); }
}
