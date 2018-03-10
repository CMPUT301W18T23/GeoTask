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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.R;

import java.util.ArrayList;

public class TaskArrayAdapter extends ArrayAdapter<Task> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<Task> data = null;

    public TaskArrayAdapter(Context context, int resource, ArrayList<Task> objects){
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
            headerSub.name = (TextView) row.findViewById(R.id.task_list_title);
            headerSub.hits = (TextView) row.findViewById(R.id.task_list_hits);
            headerSub.desc = (TextView) row.findViewById(R.id.task_list_desc);
            headerSub.bids = (TextView) row.findViewById(R.id.task_list_bids);
            headerSub.date = (TextView) row.findViewById(R.id.task_list_date);
            headerSub.icon = (ImageView) row.findViewById(R.id.taskIcon);

            row.setTag(headerSub);

        } else {
            headerSub = (HeaderSub) row.getTag();
        }

        Task item = data.get(position);
        headerSub.name.setText(item.getName());
        headerSub.hits.setText(String.format("Viewed %d times", item.getHitCounter()));
        headerSub.desc.setText(item.getDescription());
        headerSub.bids.setText(String.format("Bids: %d", item.getNumBidders()));
        //headerSub.date.setText(item.getDate());

        if (item.getStatus().compareTo("Accepted") == 0){
            headerSub.icon.setImageResource(R.drawable.ic_checkbox_blank_circle_grey600_24dp);
        } else if (item.getStatus().compareTo("Completed") == 0) {
            headerSub.icon.setImageResource(R.drawable.ic_check_circle_grey600_24dp);
        } else if(item.getNumBidders() > 0) {
            headerSub.icon.setImageResource(R.drawable.ic_cisco_webex_grey600_24dp);
        } else {
            headerSub.icon.setImageResource(R.drawable.ic_circle_outline_grey600_24dp);
        }

        return row;
    }

    private class HeaderSub{
        private TextView name;
        private TextView desc;
        private TextView hits;
        private TextView bids;
        private TextView date;
        private ImageView icon;
    }
}