package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.geotask.myapplication.Adapters.BidArrayAdapter;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewBidsActivity extends AppCompatActivity implements AsyncCallBackManager {

    private ListView oldBids; //named taskListView
    private ArrayList<Bid> bidList;
    private ArrayAdapter<Bid> adapter;
    private User currentUser; //TODO - get current user
    private Task task;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bids);


        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER)); //ToDo switch to Parcelable

        oldBids = (ListView) findViewById(R.id.bidListView);
        bidList = new ArrayList<Bid>();
        MasterController.verifySettings();

        oldBids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.i("ViewBids --->",task.getRequesterID() + " " + currentUser.getObjectID());
                if(task.getRequesterID().compareTo(currentUser.getObjectID()) == 0){
                    Bid bid = bidList.get(position);
                    triggerPopup(view, bid, task);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void populateBidView(){
        //TODO - build query that returns list of bids that all have task ID == this.taskID
        /// THIS SHOULD WORK BUT IS CURRENTLY COMMENTED OUT
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("taskID", task.getObjectID());

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> result = null;

        try {
            result = (List<Bid>) asyncSearch.get();
            bidList = new ArrayList<Bid>(result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    protected void onStart() {
        super.onStart();
        Log.i("LifeCycle --->", "onStart is called");
        this.task = (Task) getIntent().getSerializableExtra(getString(R.string.CURRENT_TASK_BEING_VIEWED));
        Log.i("LifeCycle --->", "extracted task with name:" + this.task.getName());
        Log.i("LifeCycle --->", "extracted user with name:" + this.currentUser.getName());
        //populate the array on start
        populateBidView();
        adapter = new BidArrayAdapter(this, R.layout.bid_list_item, bidList);
        oldBids.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    protected void onResume() {
        super.onResume();
        Log.i("LifeCycle --->", "onResume is called");
        //populate the array on start
        populateBidView();
        adapter = new BidArrayAdapter(this, R.layout.bid_list_item, bidList);
        oldBids.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

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
        Intent intent = new Intent(ViewBidsActivity.this, TaskViewActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("Id", currentUser);
        intent.putExtra("task", task);
        startActivity(intent);

    }

    public void deleteBid(Bid bid, Task task){
        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
        bidList.remove(bid);
    }

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
                Log.i("LifeCycle --->", bid.getValue().toString() + " clicked");
                //TODO - Once ViewProfileActivity is added, uncomment this

                Intent intent = new Intent(ViewBidsActivity.this, ViewProfile.class);
//                intent.putExtra("userID", bid.getProviderID());
                intent.putExtra("user", currentUser);
                startActivity(intent);

            }
        });
    }

    public void setForTest(){
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
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
