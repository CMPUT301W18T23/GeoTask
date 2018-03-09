package com.geotask.myapplication.DataClasses;

public class User extends GTData{
    private String name;
    private String email;
    private String phonenum;

    public User(String name, String email, String phonenum){
        super.setType("user");
        this.name = name;
        this.email = email;
        this.phonenum = phonenum;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public String getPhonenum(){
        return phonenum;
    }

    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    public String getType() { return super.getType(); }
}
