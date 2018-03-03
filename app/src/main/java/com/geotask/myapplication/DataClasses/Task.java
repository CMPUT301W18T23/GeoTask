package com.geotask.myapplication.DataClasses;

import java.util.ArrayList;

public class Task extends GTData{
	private String name;
	private String description;
	private String status;
	private ArrayList<String> photoList = new ArrayList<String>();
	private Double accpetedBid;
	private String provider;
	private String requester;
	//i am not sure of what datatype for pictures
	public Task(String Name, String Description) { //need string for pictures 
		this.name = Name;
		this.description = Description;
		this.status = "Requested";
	}
	public String getName() {
		return this.name;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String Description) {
		this.description = Description;
	}
	
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String Status) {
		this.status = Status;
	}
	
	public ArrayList<String> getPictures() { 
		return this.photoList; 
	}
	public void setPicture(String Picture) { 	
		this.photoList.add(Picture);
	}
	public void deletePicture(String Picture) { 
		this.photoList.remove(Picture);
	}
	public void setAcceptedBid(Double Bid) {
		this.accpetedBid = Bid;
	}
	public Double getAcceptedBid() {
		return this.accpetedBid;
	}
	
	public void setProvider(String Provider) {
		this.provider = Provider;
	}
	public String getProvider(){
		return this.provider;
	}
	
	public void setRequester(String Requester) {
		this.requester = Requester;
	}
	public String getRequester() {
		return this.requester;
	}
}
