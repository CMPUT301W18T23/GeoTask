package com.geotask.myapplication.DataClasses;

import com.geotask.myapplication.DataClasses.Bid;

import java.util.ArrayList;

public class BidList {
    private ArrayList<Bid> bidArrayList = new ArrayList<Bid>();
    private String taskID;

    public BidList(String TaskID) {
        this.taskID = TaskID;
    }

    public void deleteBid(int index) {
        bidArrayList.remove(index);
    }

    public Bid getBid(int index) {
        return bidArrayList.get(index);
    }

    public void addBid(Bid bid) {
        bidArrayList.add(bid);
    }

    public int getNumBids() {
        return bidArrayList.size();
    }

}