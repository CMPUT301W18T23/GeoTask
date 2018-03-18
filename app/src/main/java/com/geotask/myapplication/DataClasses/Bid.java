package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import java.util.Date;

@Entity(tableName = "bids")
public class Bid extends GTData {

	@ColumnInfo(name = "provider_id")
	private String providerID;
	@ColumnInfo
	private Double value;
	@ColumnInfo(name = "task_id")
	private String taskID;

	@Ignore
	public Bid(){};

	public Bid(String providerID, Double value, String taskID) {
		super.setType(Bid.class.toString());
		this.providerID = providerID;
		this.value = value;
		this.taskID = taskID;
		super.setDate(new Date().getTime());
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


	@Override
	public String toString(){
		return this.providerID + this.value + this.taskID ;
	}


}
