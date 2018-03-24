package com.geotask.myapplication.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/** BidArrayAdapter
 *
 * This is the Array adapter used for the Bid objects. For the moment, it is being populated by
 * communicating with the database, this will hopefully be changed later to save some time.
 *
 * Created by Kyle on 2018-03-12.
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
*/
public class BidArrayAdapter extends ArrayAdapter<Bid> implements AsyncCallBackManager{

    private Context context;
    private int layoutResourceId;
    private ArrayList<Bid> listData = null;
    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    public BidArrayAdapter(Context context, int resource, ArrayList<Bid> objects) {
        super(context, resource, objects);
        this.layoutResourceId = resource;
        this.context = context;
        this.listData = objects;
    }

    @SuppressLint("CutPasteId")
    @Override
    /*
     * There are 4 fields of the list item that are filled:
     *
     * _providerName_: The name of the provider
     * _value_: The value of the bid
     * _date_: The date that the bid was added
     * _numProvided_: The number of completed tasks by the provider
     *
     * The XML for the item is in the file bid_list_item.xml
     */
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

            Bid item = listData.get(position);

            /*
                Setting the TextView values
             */
            headerSub.value.setText(String.format("$%.2f", item.getValue()));

            //get the provider profile
            MasterController.AsyncGetDocument asyncGetDocument =
                    new MasterController.AsyncGetDocument(this, getContext());
            asyncGetDocument.execute(new AsyncArgumentWrapper(item.getProviderID(), User.class));

            User remote = null;
            try {
                remote = (User) asyncGetDocument.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            User provider = remote;

            headerSub.providerName.setText(provider.getName());
            headerSub.numProvided.setText(String.format("%d tasks completed", provider.getCompletedTasks()));
            headerSub.date.setText(item.getDateString());

            return row;
    }

    /**
     * Template for the bid list item
     */
    private class HeaderSub{
        private TextView providerName;
        private TextView value;
        private TextView date;
        private TextView numProvided;

        //maybe implement later?
        //private ImageView icon;
        //private TextView desc;
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

