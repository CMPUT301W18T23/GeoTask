package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.geotask.myapplication.Controllers.Helpers.BidListConverter;
import com.geotask.myapplication.Controllers.Helpers.EmailConverter;

import java.util.ArrayList;
import java.util.Date;

/**
 * data structure for A User
 * keeps track of Id for login
 * their login name and contact information
 * email is unique
 */
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
    @TypeConverters(BidListConverter.class)
    private ArrayList<String> historyList;
    @TypeConverters(BidListConverter.class)
    private ArrayList<String> starredList;

    /**
     *constructor
     * @param name
     * @param email
     * @param phonenum
     */
    public User(String name, String email, String phonenum){
        super.setType(User.class.toString());
        this.name = name;
        this.email = EmailConverter.convertEmailForElasticSearch(email);
        this.phonenum = phonenum;
        this.completedTasks = 0;
        super.setDate(new Date().getTime());
        this.starredList = new ArrayList<String>();
        this.historyList = new ArrayList<String>();
    }

    /**
     *gets  the location of user
     *@return location
     */
    public String getLocation() {
        return location;
    }

    /**
     *gets the name of the user
     * @return name
     */
    public String getName(){
        return name;
    }
    /**
     *gets email email is converted into a nice way for elasticseach to handle
     * @return EmailConverter.revertEmailFromElasticSearch(email)
     */
    public String getEmail(){
        return EmailConverter.revertEmailFromElasticSearch(email);
    }

    /**
     * sets email. converted into a good way for elasticsearch to handle
     * @param email
     */
    public void setEmail(String email){
        this.email = EmailConverter.convertEmailForElasticSearch(email);
    }

    /**
     * retunrs phonenumber
     * @return
     */
    public String getPhonenum(){
        return phonenum;
    }

    /**
     * retunrs x coord for location
     * @return Float.parseFloat(location.split("[,]")[0])
     */
    public float getLocationX() { return Float.parseFloat(location.split("[,]")[0]); }

    /**
     * retunrs y coord for location
     * @return Float.parseFloat(location.split("[,]")[0])
     */
    public float getLocationY() { return Float.parseFloat(location.split("[,]")[1]); }

    /**
     * sets the loaction
     * @param location
     */
    public void setLocation(String location) { this.location = location; }

    /**
     * sets the phonenumber
     * @param phonenum
     */
    public void setPhonenum(String phonenum){
        this.phonenum = phonenum;
    }

    /**
     * sets the name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *gets a number of completed tasks
     */
    public Integer getCompletedTasks() {
        return completedTasks;
    }

    /**
     *sets ammount of completed tasks
     * @param completedTasks
     */
    public void setCompletedTasks(Integer completedTasks) {
        this.completedTasks = completedTasks;
    }

    /**
     *increments the ammount of completed tasks
     */
    public void incrementCompletedTasks() {
        this.completedTasks++;
    }

    /**
     *returns the name email and phonenumber in string format
     * again email is converted back to normal form
     * @return this.name + " " +
    EmailConverter.revertEmailFromElasticSearch(email) + " " +
    this.phonenum;
     */
    public String toString()  {
        return this.name + " " +
                EmailConverter.revertEmailFromElasticSearch(email) + " " +
                this.phonenum;
    }

    public ArrayList<String> getStarredList() {
        return starredList;
    }

    public void setStarredList(ArrayList<String> starredList) {
        this.starredList = starredList;
    }

    public ArrayList<String> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(ArrayList<String> historyList) {
        this.historyList = historyList;
    }

    public Boolean starred(String taskID){
        if(starredList.contains(taskID)){
            return true;
        }
        return false;
    }

    public void removeTaskFromStarredList(String taskID){
        starredList.remove(taskID);
    }

    public void addTaskToStarredList(String taskID){
        if(!starredList.contains(taskID)) {
            starredList.add(taskID);
        }
    }

    public Boolean visited(String taskID){
        if(historyList.contains(taskID)){
            return true;
        }
        if(historyList.size() > 100){
            historyList.remove(0);
        }
        if(!historyList.contains(taskID)) {
            historyList.add(taskID);
        }
        return false;
    }
}
