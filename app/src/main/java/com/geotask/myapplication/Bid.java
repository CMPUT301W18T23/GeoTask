package com.geotask.myapplication;

public class Bid {
	private String providerID;
	private Double value;
	private String taskID;
	public Bid(String ProviderID, Double Value, String TaskID) {
		this.providerID = ProviderID;
		this.value = Value;	
		this.taskID = TaskID;
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

}
