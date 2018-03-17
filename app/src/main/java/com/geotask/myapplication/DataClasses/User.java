package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.util.TableInfo;

import com.geotask.myapplication.Controllers.Helpers.EmailConverter;

import java.lang.reflect.Type;
import java.util.Date;

@Entity(tableName = "users")
public class User extends GTData{
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    private String name;
    @ColumnInfo(name = "email", typeAffinity = ColumnInfo.TEXT)
    private String email;
    @ColumnInfo(name = "phonenum", typeAffinity = ColumnInfo.TEXT) //ToDo validation
    private String phonenum;
    @ColumnInfo
    private Integer completedTasks; //metric of completed tasks
    @ColumnInfo(name = "location", typeAffinity = ColumnInfo.TEXT)
    private String location;                                        //format example: "47.55,-82.11"

    public User(String name, String email, String phonenum){
        super.setType(User.class.toString());
        this.name = name;
        this.email = EmailConverter.convertEmailForElasticSearch(email);
        this.phonenum = phonenum;
        this.completedTasks = 0;
        super.setDate(new Date().getTime());
    }

    public String getLocation() {
        return location;
    }

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

    public float getLocationX() { return Float.parseFloat(location.split("[,]")[0]); }

    public float getLocationY() { return Float.parseFloat(location.split("[,]")[1]); }

    public void setLocation(String location) { this.location = location; }

    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    public void incrementCompletedTasks() {
        this.completedTasks++;
    }

    public String toString()  {
        return this.name + " " +
                EmailConverter.revertEmailFromElasticSearch(email) + " " +
                this.phonenum;
    }
}
