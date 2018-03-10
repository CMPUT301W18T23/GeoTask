package com.geotask.myapplication.DataClasses;

import java.lang.reflect.Type;

public class Bid extends GTData {
	private String providerID;
	private Double value;
	private String taskID;

	public Bid() {
		super.setType(Bid.class);
	}
	public Bid(String providerID, Double value, String taskID) {
		super.setType(Bid.class);
		this.providerID = providerID;
		this.value = value;
		this.taskID = taskID;
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
	public Type getType(){
		return super.getType();
	}

	@Override
	public String toString(){
		return this.providerID + this.value + this.taskID ;
	}
}
