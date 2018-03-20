package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "bids")
public class Bid extends GTData {

	@ColumnInfo(name = "provider_id")
	private String providerID;
	@ColumnInfo
	private Double value;
	@ColumnInfo(name = "task_id")
	private String taskID;
	@ColumnInfo
	private long date;

	@Ignore
	public Bid(){};

	public Bid(String providerID, Double value, String taskID) {
		super.setType(Bid.class);
		this.providerID = providerID;
		this.value = value;
		this.taskID = taskID;
		this.date = new Date().getTime();
	}
	
	public void setProviderID(String ProviderID) {
		this.providerID = ProviderID;
	}
	public String getProviderID() {
		return this.providerID;
	}
	public void setValue(Double Value) {
		this.value = Value;
	}
	public Double getValue() {
		return this.value;
	}
	public void setTaskID(String TaskID) {
		this.taskID = TaskID;
	}
	public String getTaskID() {
		return this.taskID;
	}
	public String getDateString(){
		String strDate = new SimpleDateFormat("EEEE MMMM d, yyyy").format(new java.util.Date((long)date));
		return strDate;
	}
    public long getDate(){
	    return this.date;
    }
    public void setDate(Date newDate){
        this.date = newDate.getTime();
    }
    public void setDate(long newDate){
        this.date = newDate;
    }
	@Override
	public Type getType(){
		return super.getType();
	}


	@Override
	public String toString(){
		return this.providerID + this.value + this.taskID ;
	}


}
