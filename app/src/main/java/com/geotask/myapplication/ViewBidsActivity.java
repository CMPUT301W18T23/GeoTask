package com.geotask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.geotask.myapplication.Adapters.BidArrayAdapter;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewBidsActivity extends AbstractGeoTaskActivity implements AsyncCallBackManager {

    private ListView oldBids; //named taskListView
    private ArrayList<Bid> bidList;
    private ArrayAdapter<Bid> adapter;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    private TextView emptyText;

    /**
     * Initiate variables, and set an on click listener for
     * the ListView of bids
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bids);

        oldBids = findViewById(R.id.bidListView);
        bidList = new ArrayList<>();
        emptyText = (TextView) findViewById(R.id.empty_bid_string);

        oldBids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.i("ViewBids --->",task.getRequesterID() + " " + currentUser.getObjectID());
                Bid bid = bidList.get(position);
                if(getCurrentTask().getRequesterID().compareTo(getCurrentUser().getObjectID()) == 0){
//                    Bid bid = bidList.get(position);
                    triggerPopup(view, bid, getCurrentTask());
                    adapter.notifyDataSetChanged();
                }else if (getCurrentUser().getObjectID().equals(bid.getProviderID())){
                    triggerDeletePopup(view, bid, getCurrentTask());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Query the elastic search server and fill the list view
     * with all bids for the task
     */
    private void populateBidView(){
        //TODO - build query that returns list of bids that all have task ID == this.taskID
        /// THIS SHOULD WORK BUT IS CURRENTLY COMMENTED OUT
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("taskID", getCurrentTask().getObjectID());

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        try {
            bidList = (ArrayList<Bid>) asyncSearch.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();
        setEmptyString();
    }

    /**
     * Set up adapter for bid view and call populateBidView()
     */
    @Override
    protected void onStart() {
        super.onStart();
        //Log.i("LifeCycle --->", "onStart is called");
        adapter = new BidArrayAdapter(this, R.layout.bid_list_item, bidList);
        oldBids.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        populateBidView();
    }


    /**
     * Set up adapter for bid view and call populateBidView()
     */
    @Override
    protected void onResume() {
        super.onResume();
        //Log.i("LifeCycle --->", "onResume is called");
        //populate the array on start
        adapter = new BidArrayAdapter(this, R.layout.bid_list_item, bidList);
        oldBids.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        populateBidView();
    }


    /**
     * If the viewing user is the owner of the task, this option
     * will be visible to accept a specific bid and update the task on the
     * server.
     */
    private void acceptBid(Bid bid, Task task){
        Log.i("LifeCycle --->", "Accepted bid");
        //TODO - update the database by notifying the provider

        task.setAcceptedProviderID(bid.getProviderID());
        task.setAccpeptedBidID(bid.getObjectID());
        task.setStatusAccepted();

        //The following should wok, but needs to be tested after the array is truly populated by the
        //master controller

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(task);

        //go back to TaskViewActivity
        Intent intent = new Intent(ViewBidsActivity.this, MenuActivity.class);
        intent.putExtra("task", task);
        startActivity(intent);

    }

    /**
     * Allows a user to delete their bid on a task
     */
    public void deleteBid(Bid bid, Task task){
        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("taskID", task.getObjectID());


        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> result = null;

        try {
            result = (List<Bid>) asyncSearch.get();
            if(result.size() == 0){
                task.setStatusRequested();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        bidList.remove(bid);
        adapter = new BidArrayAdapter(this, R.layout.bid_list_item, bidList);
        oldBids.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        updateTask(bid, task);
    }


    /**
     * Method to push any changes to bids on a task to the server
     */
    public void updateTask(Bid bid , Task task){

        if (bid.getObjectID().equals(task.getAccpeptedBidID())){
            task.setAcceptedProviderID(null);
            task.setAccpeptedBidID(null);
        if (bidList.size() ==0 ){
            task.setStatus("Requested");
        } else {
            task.setStatus("Bidded");
        }
        } else if (bidList.size() ==0){
            task.setStatus("Requested");
        }
        Intent back = new Intent();
        back.putExtra(getString(R.string.UPDATED_TASK_AFTER_EDIT), task);
        back.putExtra("del", "1");

        //task.syncBidData();
        setResult(Activity.RESULT_OK, back);
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(task);
    }

    /**
     * Set up buttons in the view for the popup with various bid functionality
     */
    public void triggerPopup(View view, final Bid bid, final Task task){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_bidlist_popout, null);

        POPUP_WINDOW_DELETION = new PopupWindow(this);
        POPUP_WINDOW_DELETION.setContentView(layout);
        POPUP_WINDOW_DELETION.setFocusable(true);
        POPUP_WINDOW_DELETION.setBackgroundDrawable(null);
        POPUP_WINDOW_DELETION.showAtLocation(layout, Gravity.CENTER, 1, 1);

        Button deleteBtn = (Button) layout.findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
                deleteBid(bid, task);
                adapter.notifyDataSetChanged();
            }
        });

        Button acceptBtn = (Button) layout.findViewById(R.id.btn_accept);
        acceptBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
                acceptBid(bid, task);
            }
        });

        Button viewProfileBtn = (Button) layout.findViewById(R.id.btn_visit_profile);
        viewProfileBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
                //Log.i("LifeCycle --->", bid.getValue().toString() + " clicked");
                //TODO - Once ViewProfileActivity is added, uncomment this

                Intent intent = new Intent(ViewBidsActivity.this, ViewProfileActivity.class);
//                intent.putExtra("userID", bid.getProviderID());
                intent.putExtra(getString(R.string.CURRENT_USER), getCurrentUser()); //ToDo issue #31 here????
                startActivity(intent);
            }
        });
    }
    public void triggerDeletePopup(View view, final Bid bid, final Task task){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.layout_delete_bid_popout, null);

        POPUP_WINDOW_DELETION = new PopupWindow(this);
        POPUP_WINDOW_DELETION.setContentView(layout);
        POPUP_WINDOW_DELETION.setFocusable(true);
        POPUP_WINDOW_DELETION.setBackgroundDrawable(null);
        POPUP_WINDOW_DELETION.showAtLocation(layout, Gravity.CENTER, 1, 1);

        Button deleteBtn = (Button) layout.findViewById(R.id.btn_delete_my_bid);
        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
                deleteBid(bid, task);
                adapter.notifyDataSetChanged();

                Intent back = new Intent();
                back.putExtra(getString(R.string.UPDATED_TASK_AFTER_EDIT), task);
                back.putExtra("del", "1");

                setResult(Activity.RESULT_OK, back);
                finish();
            }
        });

        Button dontDeleteBtn = (Button) layout.findViewById(R.id.btn_do_not_delete_my_bid);
        dontDeleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
                finish();
            }
        });
    }

    public void setEmptyString(){
        Log.i("BidList ----->", String.format("%d", bidList.size()));

        if(bidList.size() == 0){
            emptyText.setText("No Bids");
            emptyText.setVisibility(View.VISIBLE);
            oldBids.setVisibility(View.INVISIBLE);
        } else {
            emptyText.setText("");
            emptyText.setVisibility(View.INVISIBLE);
            oldBids.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPostExecute(GTData data) {
        this.data = data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        //bidList.clear();
        //bidList.clear();
        //bidList.addAll((Collection<? extends Bid>) dataList);
        //adapter.notifyDataSetChanged();
    }
}
