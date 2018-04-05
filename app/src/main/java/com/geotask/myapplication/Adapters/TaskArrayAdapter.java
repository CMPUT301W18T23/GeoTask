package com.geotask.myapplication.Adapters;


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
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;
import com.geotask.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** TaskArrayAdapter
 *
 * This is the Array adapter used for the Task objects. For the moment, it is being populated by
 * communicating with the database, this will hopefully be changed later to save some time.
 *
 * Created by Kyle on 2018-03-09.
 *
 * References:
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
 *      For setting a drawable.
 *      Author jlopez, Mar 9, 2015, no licence stated
 */
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
    /*
     * There are 7 fields of the list item that are filled here:
     *
     * _name_: The name of the task
     * _hits_: The number of clicks times the task has been viewed by someone who is not the requester
     * _desc_: The description of the task
     * _bids_: The number of bids on the task
     * _date_: The date that the task was instantiated
     * _lowestBid_: This is the lowest bid currently on the task
     * _icon_: For now, this is set to be an icon that relates to the status of a task. Later, we would
     *         like this to be a photo if a photo was provided.
     *
     * The XML for the item is in the file task_list_item.xml
     */
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

        /*
            Setting the TextView values
         */
        Task item = tdata.get(position);
        headerSub.name.setText(item.getName());
        headerSub.hits.setText(String.format("Viewed %d times", item.getHitCounter()));
        headerSub.desc.setText(item.getDescription());
        headerSub.date.setText(item.getDateString());

        Log.i("-------->", item.getDateString());

        /*
            Setting the icon for the task
         */
        if (item.getStatus().compareTo("Accepted") == 0){
            headerSub.icon.setImageResource(R.drawable.ic_checkbox_blank_circle_grey600_24dp);
        } else if (item.getStatus().compareTo("Completed") == 0) {
            headerSub.icon.setImageResource(R.drawable.ic_check_circle_grey600_24dp);
        } else if(item.getStatus().compareTo("Bidded") == 0) {
            headerSub.icon.setImageResource(R.drawable.ic_cisco_webex_grey600_24dp);
        } else {
            headerSub.icon.setImageResource(R.drawable.ic_circle_outline_grey600_24dp);
        }

        headerSub.lowestBid.setText(String.format("Lowest Bid: %.2f", 69.69));
        /*
            Finding the lowest bid, and number of bids
         */


        //make the query
        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskID"});
        builder.addParameters(new String[] {item.getObjectID()});

        //perform the search
        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this, getContext());
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> result = null;
        ArrayList<Bid> bidList;

        try {
            //get the result
            result = (List<Bid>) asyncSearch.get();
            bidList = new ArrayList<Bid>(result);

            /*
                set the lowestBid TextView
             */

            if(bidList.size() == 0){
                headerSub.lowestBid.setText("");                            //give no text
                item.setStatusRequested();                                  //change the status
                MasterController.AsyncUpdateDocument asyncUpdateDocument =  //update the status
                        new MasterController.AsyncUpdateDocument(getContext());
                asyncUpdateDocument.execute(item);
            } else  if(bidList.size() == 1) {
                Double lowest = bidList.get(0).getValue();
                headerSub.lowestBid.setText(String.format("Lowest Bid: %.2f", lowest)); //set text
            } else {
                Double lowest = bidList.get(0).getValue();
                for(Bid bid : bidList){                                     //iterate to find lowest
                    if(bid.getValue() < lowest){
                        lowest = bid.getValue();
                    }
                }
                headerSub.lowestBid.setText(String.format("Lowest Bid: %.2f", lowest)); //set text
            }
            headerSub.bids.setText(String.format("Bids: %d", bidList.size()));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        /*
            Set the lowestBid field to be special for accepted bids
         */

        if(item.getStatus().compareTo("Accepted") == 0){
            MasterController.AsyncGetDocument asyncGetDocument =
                    new MasterController.AsyncGetDocument(this, getContext());
            asyncGetDocument.execute(new AsyncArgumentWrapper(item.getAccpeptedBidID(), Bid.class));

            Bid remote = null;
            try {
                remote = (Bid) asyncGetDocument.get();
                headerSub.lowestBid.setText(String.format("Accepted for: %.2f", remote.getValue()));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return row;
    }

    /**
     * Template for the task list item
     */
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