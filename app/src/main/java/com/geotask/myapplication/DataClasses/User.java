package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.util.TableInfo;

import com.geotask.myapplication.Controllers.Helpers.EmailConverter;

import java.lang.reflect.Type;

@Entity(tableName = "users")
public class User extends GTData{
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT)
    private String name;
    @ColumnInfo(name = "email", typeAffinity = ColumnInfo.TEXT)
    private String email;
    @ColumnInfo(name = "phonenum", typeAffinity = ColumnInfo.TEXT)
    private String phonenum;
    @ColumnInfo
    private Integer completedTasks; //metric of completed tasks

    public User(String name, String email, String phonenum){
        super.setType(User.class);
        this.name = name;
        this.email = EmailConverter.convertEmailForElasticSearch(email);
        this.phonenum = phonenum;
        this.completedTasks = 0;
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

    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    public Type getType() { return super.getType(); }

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
}
