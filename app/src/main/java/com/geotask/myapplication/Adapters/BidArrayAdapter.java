package com.geotask.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.room.Ignore;
import android.content.Context;
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
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.R;

import java.util.ArrayList;

/**
 * Created by Kyle on 2018-03-12.
*/


public class BidArrayAdapter extends ArrayAdapter<Bid>{ //implements AsyncCallBackManager{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Bid> data = null;

    public BidArrayAdapter(Context context, int resource, ArrayList<Bid> objects) {
    super(context, resource, objects);
    this.layoutResourceId = resource;
    this.context = context;
    this.data = objects;
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
            headerSub.providerName = (TextView) row.findViewById(R.id.bid_list_name);
            headerSub.value = (TextView) row.findViewById(R.id.bid_list_value);
            headerSub.date = (TextView) row.findViewById(R.id.bid_list_date);
            headerSub.numProvided = (TextView) row.findViewById(R.id.bid_list_numProvided);
           // headerSub.icon = (ImageView) row.findViewById(R.id.bidIcon);

            row.setTag(headerSub);

            } else {
            headerSub = (HeaderSub) row.getTag();
            }

            Bid item = data.get(position);

            //setting values
            headerSub.value.setText(String.format("%.2d", item.getValue()));
            AsyncArgumentWrapper aAW = new AsyncArgumentWrapper(item.getProviderID(), Bid.class);
            //User provider = (User) MasterController.AsyncGetDocument.execute(); //TODO - get the User of the provider
            User provider = new User("Kyle", "kyleG@email.es","911"); //TEMP
            headerSub.providerName.setText(provider.getName());
            headerSub.numProvided.setText(String.format("%d tasks completed", provider.getCompletedTasks()));
            headerSub.date.setText(item.getDateString());

            return row;
    }

    private class HeaderSub{
        private TextView providerName;
        private TextView value;
        private TextView date;
        private TextView numProvided;

        //maybe implement later?
        //private ImageView icon;
        //private TextView desc;
    }
}

