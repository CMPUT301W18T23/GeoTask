package com.geotask.myapplication.DataClasses;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.geotask.myapplication.Controllers.Helpers.BidListConverter;
import com.geotask.myapplication.Controllers.Helpers.GetLowestBidFromServer;

import java.util.ArrayList;
import java.util.Date;

/**
 *	data stucture for a task
 * stores general needed information and the Ids of people related to the task
 *
 * Resources:
 *
 * 		https://stackoverflow.com/questions/12960265/retrieve-all-values-from-hashmap-keys-in-an-arraylist-java
 * 			for basic arraylist operations
 * 			Author Rohit Jain, Oct 18, 2012, no licence stated
 */
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
	@ColumnInfo
	private Double lowestBid;
	@ColumnInfo
	private Integer numBids;

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


	@Ignore
	public Task(String requesterID, String name, String description, String location) { //need string for pictures
		super.setType(Task.class.toString());
		this.name = name;
		this.description = description;
		this.hitCounter = 0;
		this.status = "requested";
		super.setDate(new Date().getTime());
		this.accpetedBid = -1.0; //ToDo
		this.requesterID = requesterID;
		this.location = location;
		this.lowestBid = -1.0;
		this.numBids = 0;
	}

	/**
	 *constructor for task
	 * gets reuqiester ID, anme and the description. locatiion is later to be inplemented by overload
	 * @param requesterID
	 * @param name
	 * @param description
	 */
	public Task(String requesterID, String name, String description) { //need string for pictures
		super.setType(Task.class.toString());
		this.name = name;
		this.description = description;
		this.hitCounter = 0;
		this.status = "requested";
		super.setDate(new Date().getTime());
		this.accpetedBid = -1.0; //ToDo
		this.requesterID = requesterID;
		this.lowestBid = -1.0;
		this.numBids = 0;
	}

	/**
	 *gets nae
	 * @return this.name
	 */
    public String getName() {
		return this.name;
	}
	/**
	 *sets Name
	 * @param Name
	 */
	public void setName(String Name) {
		this.name = Name;
	}
	/**
	 *gets description
	 * @return this.description
	 */
	public String getDescription() {
		return this.description;
	}
	/**
	 *sets description
	 * @param Description
	 */
	public void setDescription(String Description) {
		this.description = Description;
	}

	/**
	 *gets status
	 * @return this.status
	 */
	public String getStatus() {
		return this.status;
	}
	/**
	 *set status
	 * @param Status
	 */
	public void setStatus(String Status) {
		this.status = Status;
	}

	/**
	 *gets list of pictures
	 * @return this.photoList
	 */
	public ArrayList<String> getPictures() { 
		return this.photoList; 
	}
	/**
	 *sets a picture
	 * @param Picture
	 */
	public void setPicture(String Picture) { 	
		this.photoList.add(Picture);
	}
	/**
	 *deletes a picture from arraylist
	 * @param Picture
	 */
	public void deletePicture(String Picture) { 
		this.photoList.remove(Picture);
	}
	/**
	 *sets the ammount of accepted Bid
	 * @param Bid
	 */
	public void setAcceptedBid(Double Bid) {
		this.accpetedBid = Bid;
	}
	/**
	 *get ammount of the accepted bid
	 * @return this.accpetedBid
	 */
	public Double getAcceptedBid() {
		return this.accpetedBid;
	}

	/**
	 * sets  the requester ID
	 * @param Provider
	 */
	public void setRequesterID(String Provider) {
		this.requesterID = Provider;
	}

	/**
	 * gets the requesterId
	 * @return this.requesterID
	 */
	public String getRequesterID(){
		return this.requesterID;
	}

	/**
	 * sets the provider ID from the accepted bid
	 * @param Requester
	 */
	public void setAcceptedProviderID(String Requester) {
		this.acceptedProviderID = Requester;
	}

	/**
	 * gets the provider Id of the requested bid
	 * @return this.acceptedProviderID
	 */
	public String getAcceptedProviderID() {
		return this.acceptedProviderID;
	}

	/**
	 * increment it counter
	 */
	public void addHit(){
		this.hitCounter ++;
	}
	/**
	 *get hit counter ammount
	 */
	public Integer getHitCounter(){
		return this.hitCounter;
	}
	/**
	 *gets the number of biders
	 * @return bidList.size()
	 */
	public Integer getNumBidders(){
		return this.bidList.size();
	}

	/**
	 *add a bid id to bids associated with task
	 * @param bid
	 */
	public void addBid(Bid bid){
		bidList.add(bid.getObjectID());
	}

	/**
	 *gets list of photolist
	 * @return photolist
	 */
	public ArrayList<String> getPhotoList() {
		return photoList;
	}

	/**
	 *gets list of bids
	 * @return bidList
	 */
	public ArrayList<String> getBidList() {
		return bidList;
	}

	/**
	 *gets the accepted bid
	 * @return acceptedBid
	 */
	public Double getAccpetedBid() {
		return accpetedBid;
	}

	/**
	 *sets bidlist from given value
	 * @param bidList
	 */
	public void setBidList(ArrayList<String> bidList) {
		this.bidList = bidList;
	}

	/**
	 *sets the amount of accepted bids
	 * @param accpetedBid
	 */
	public void setAccpetedBid(Double accpetedBid) {
		this.accpetedBid = accpetedBid;
	}

	/**
	 *sets the hitcounter to specified ammount
	 * @param hitCounter
	 */
	public void setHitCounter(int hitCounter) {
		this.hitCounter = hitCounter;
	}

	/**
	 *gets the id of the accpted bid
	 */
	public String getAccpeptedBidID() {
		return accpeptedBidID;
	}

	/**
	 *sets Id of the accpted Bid
	 * @param accpeptedBidID
	 */
	public void setAccpeptedBidID(String accpeptedBidID) {
		this.accpeptedBidID = accpeptedBidID;
	}

	/**
	 *sets the status to Requested
	 */
	public void setStatusRequested(){
		this.status = "Requested";
	}

	/**
	 *sets the status to Accepted
	 */
	public void setStatusAccepted(){
		this.status = "Accepted";
	}

	/**
	 *sets the status to Completed
	 */
	public void setStatusCompleted(){
		this.status = "Completed";
	}

	/**
	 *sets the status to Bidded
	 */
	public void setStatusBidded(){
		this.status = "Bidded";
	}

	/**
	 *retunrs all items in string format
	 * @return  this.name + " " + this.description + " " + bidList.toString()
	 */
	public String toString(){
		return this.name + " " + this.description + " " + bidList.toString();
	}

	public Double getLowestBid() {
		return lowestBid;
	}

	public void setLowestBid(Double lowestBid) {
		this.lowestBid = lowestBid;
	}

	public Integer getNumBids() {
		return numBids;
	}

	public void setNumBids(Integer numBids) {
		this.numBids = numBids;
	}

	public void syncBidData(){
		ArrayList<Double> newVals = new GetLowestBidFromServer().searchAndReturnLowest(this);
		assert(newVals.size() == 2);
		this.numBids = newVals.get(1).intValue();
		this.lowestBid = newVals.get(0);
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(this.getNumBids() == 0){
			this.setStatusRequested();
		} else if (this.getNumBids() > 0){
			this.setStatusBidded();
		}
	}

}
