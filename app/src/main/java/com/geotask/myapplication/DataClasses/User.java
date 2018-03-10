package com.geotask.myapplication.DataClasses;

import com.geotask.myapplication.Controllers.ElasticsearchController;

public class User extends GTData{
    private String name;
    private String email;
    private String phonenum;

    public User(String name, String email, String phonenum){
        super.setType("user");
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

    public String getType() { return super.getType(); }

}
