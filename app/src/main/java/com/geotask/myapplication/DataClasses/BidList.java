package com.geotask.myapplication.DataClasses;

import com.geotask.myapplication.DataClasses.Bid;

import java.util.ArrayList;

public class BidList extends GTData{
    private ArrayList<String> bidArrayList = new ArrayList<>();
    //private String taskID;

    public BidList() {
        super.setType("bidList");
        //this.taskID = TaskID;
    }

    public void deleteBid(int index) {
        bidArrayList.remove(index);
    }

    public String getBid(int index) {
        return bidArrayList.get(index);
    }

    public void addBid(String ID) {
        bidArrayList.add(ID);
    }

    public int getNumBids() {
        return bidArrayList.size();
    }

    public String getType() {
        return super.getType();
    }
}