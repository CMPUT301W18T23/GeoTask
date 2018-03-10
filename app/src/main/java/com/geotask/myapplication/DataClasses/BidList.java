package com.geotask.myapplication.DataClasses;

import android.content.Context;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BidList extends GTData{
    private ArrayList<String> bidArrayList = new ArrayList<>();
    //private String taskID;

    public BidList(String TaskID) {
        super.setType(BidList.class);
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

    public Type getType() {
        return super.getType();
    }


    @Override
    public void writeFile(Context context) {
        this.writeFile(context);
    }

    @Override
    public GTData readFile(String filename, Context context, Type type) {
        return super.readFile(filename, context, type);
    }
}