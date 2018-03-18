package com.geotask.myapplication.Adapters;

/**
 * Created by Kyle on 2018-03-09.
 */

/*
 *  SubArrayAdapter
 *
 *  Author: Kyle LePoidevin-Gonzales
 *
 * https://stackoverflow.com/questions/36579485/how-do-i-use-an-array-of-objects-with-the-android-arrayadapter
 *      For general form of custom array adapter.
 *      Author Benjamin Molina, Apr 12, 2016, no licence stated
 *
 * https://stackoverflow.com/questions/11281952/listview-with-customized-row-layout-android
 *      For use of RelativeLayout.
 *      Author Sajmon, Jul 1, 2012, no licence stated
 *
 * https://stackoverflow.com/questions/8642823/using-setimagedrawable-dynamically-to-set-image-in-an-imageview
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.geotask.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class TaskArrayAdapter extends ArrayAdapter<Task> implements AsyncCallBackManager{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Task> tdata = null;
    private GTData data = null;
    private List<? extends GTData> searchResult = null;


    public TaskArrayAdapter(Context context, int resource, ArrayList<Task> objects){
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.tdata = objects;
    }

    @SuppressLint("CutPasteId")
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;
        HeaderSub headerSub = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();

            row = inflater.inflate(layoutResourceId, parent, false);

            headerSub = new HeaderSub();
            headerSub.name = (TextView) row.findViewById(R.id.task_list_title);
            headerSub.hits = (TextView) row.findViewById(R.id.task_list_hits);
            headerSub.desc = (TextView) row.findViewById(R.id.task_list_desc);
            headerSub.bids = (TextView) row.findViewById(R.id.task_list_bids);
            headerSub.date = (TextView) row.findViewById(R.id.task_list_date);
            headerSub.lowestBid = (TextView) row.findViewById(R.id.task_list_lowest);
            headerSub.icon = (ImageView) row.findViewById(R.id.taskIcon);

            row.setTag(headerSub);

        } else {
            headerSub = (HeaderSub) row.getTag();
        }

        Task item = tdata.get(position);
        headerSub.name.setText(item.getName());
        headerSub.hits.setText(String.format("Viewed %d times", item.getHitCounter()));
        headerSub.desc.setText(item.getDescription());
        headerSub.bids.setText(String.format("Bids: %d", item.getNumBidders()));
        headerSub.date.setText(item.getDateString());
        Log.i("-------->", item.getDateString());

        if (item.getStatus().compareTo("Accepted") == 0){ //ToDo: avoid using strings, write function in Task
            headerSub.icon.setImageResource(R.drawable.ic_checkbox_blank_circle_grey600_24dp);
        } else if (item.getStatus().compareTo("Completed") == 0) {
            headerSub.icon.setImageResource(R.drawable.ic_check_circle_grey600_24dp);
        } else if(item.getNumBidders() > 0) {
            headerSub.icon.setImageResource(R.drawable.ic_cisco_webex_grey600_24dp);
        } else {
            headerSub.icon.setImageResource(R.drawable.ic_circle_outline_grey600_24dp);
        }

        headerSub.lowestBid.setText("test");

        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("taskID", item.getObjectID());


        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> result = null;
        ArrayList<Bid> bidList;
        try {
            result = (List<Bid>) asyncSearch.get();
            bidList = new ArrayList<Bid>(result);

            if(bidList.size() == 0){
                headerSub.lowestBid.setText("");
            } else  if(bidList.size() == 1) {
                Double lowest = bidList.get(0).getValue();
                headerSub.lowestBid.setText(String.format("Lowest Bid: %.2f", lowest));
            } else {
                Double lowest = bidList.get(0).getValue();
                for(Bid bid : bidList){
                    if(bid.getValue() < lowest){
                        lowest = bid.getValue();
                    }
                }
                headerSub.lowestBid.setText(String.format("Lowest Bid: %.2f", lowest));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*
        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("taskID", item.getObjectID());

        MasterController.verifySettings();
        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Bid.class));

        List<Bid> result1 = null;
        /*
        try {
            result1 = (List<Bid>) asyncSearch.get();
            if(result1.size() == 0){
                headerSub.lowestBid.setText("");
            } else  if(result1.size() == 1) {
                Double lowest = result1.get(0).getValue();
                headerSub.lowestBid.setText(String.format("Lowest Bid: %d", lowest));
            } else {
                Double lowest = result1.get(0).getValue();
                for(Bid bid : result1){
                    if(bid.getValue() < lowest){
                        lowest = bid.getValue();
                    }
                }
                headerSub.lowestBid.setText(String.format("Lowest Bid: %d", lowest));
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

        return row;
    }

    private class HeaderSub{
        private TextView name;
        private TextView desc;
        private TextView hits;
        private TextView bids;
        private TextView date;
        private ImageView icon;
        private TextView lowestBid;
    }

    @Override
    public void onPostExecute(GTData data) {
        this.data = data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        this.searchResult = dataList;
    }
}