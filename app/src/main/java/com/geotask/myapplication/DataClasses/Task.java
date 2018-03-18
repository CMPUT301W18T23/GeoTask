package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.geotask.myapplication.Controllers.Helpers.BidListConverter;

import java.util.ArrayList;
import java.util.Date;

@Entity(tableName = "tasks")
public class Task extends GTData{
	@ColumnInfo(name = "task_name")
	private String name;
	@ColumnInfo
	private String description;
	@ColumnInfo //ToDo change type to Enum
	private String status;
	@Ignore
	private ArrayList<String> photoList = new ArrayList<String>();
	@TypeConverters(BidListConverter.class)
	private ArrayList<String> bidList = new ArrayList<>();
	@ColumnInfo
	private Double accpetedBid;
	@ColumnInfo
	private String accpeptedBidID;
	@ColumnInfo(name = "requester_id")
	private String requesterID;
	@ColumnInfo
	private String acceptedProviderID;
	@ColumnInfo
	private int hitCounter;
	@ColumnInfo
	private String location;

	public String getLocation() {
		return location;
	}

	public float getLocationX() { return Float.parseFloat(location.split("[,]")[0]); }

	public float getLocationY() { return Float.parseFloat(location.split("[,]")[1]); }

	public void setLocation(String location) {
		this.location = location;
	}
	//ToDo pictures
	//ToDo locations


	public Task(String requesterID, String name, String description) { //need string for pictures
		super.setType(Task.class.toString());
		this.name = name;
		this.description = description;
		this.hitCounter = 0;
		this.status = "requested";
		super.setDate(new Date().getTime());
		this.accpetedBid = -1.0; //ToDo
		this.requesterID = requesterID;
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
	
	public void setRequesterID(String Provider) {
		this.requesterID = Provider;
	}
	public String getRequesterID(){
		return this.requesterID;
	}
	
	public void setAcceptedProviderID(String Requester) {
		this.acceptedProviderID = Requester;
	}
	public String getAcceptedProviderID() {
		return this.acceptedProviderID;
	}

	public void addHit(){
		this.hitCounter ++;
	}
	public Integer getHitCounter(){
		return this.hitCounter;
	}
	public Integer getNumBidders(){
		return this.bidList.size();
	}

	public void addBid(Bid bid){
		bidList.add(bid.getObjectID());
	}

	public ArrayList<String> getPhotoList() {
		return photoList;
	}

	public ArrayList<String> getBidList() {
		return bidList;
	}

	public Double getAccpetedBid() {
		return accpetedBid;
	}

	public void setBidList(ArrayList<String> bidList) {
		this.bidList = bidList;
	}

	public void setAccpetedBid(Double accpetedBid) {
		this.accpetedBid = accpetedBid;
	}

	public void setHitCounter(int hitCounter) {
		this.hitCounter = hitCounter;
	}


	public String getAccpeptedBidID() {
		return accpeptedBidID;
	}

	public void setAccpeptedBidID(String accpeptedBidID) {
		this.accpeptedBidID = accpeptedBidID;
	}

	public void setStatusAccepted(){
		this.status = "Accepted";
	}

	public void setStatusCompleted(){
		this.status = "Completed";
	}

	public String toString(){
		return this.name + " " + this.description + " " + bidList.toString();
	}
}
