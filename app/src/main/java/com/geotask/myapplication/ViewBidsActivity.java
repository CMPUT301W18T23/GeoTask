package com.geotask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ViewBidsActivity extends AbstractGeoTaskActivity implements AsyncCallBackManager {

    private ListView oldBids; //named taskListView
    private ArrayList<Bid> bidList;
    private ArrayAdapter<Bid> adapter;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
    private PopupWindow POPUP_WINDOW_PROFILE = null;   //popup for error message
    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    private TextView emptyText;
    private User profile;
    private Toolbar toolbar;
    private MenuItem bidBtn;
    private Button acceptBtn;
    private Button cancelBtn;
    SwipeRefreshLayout refreshLayout;

    /**
     * Initiate variables, and set an on click listener for
     * the ListView of bids
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_bids);
        setContentView(R.layout.app_bar_menu_view_bids);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        oldBids = findViewById(R.id.bidListView);
        bidList = new ArrayList<>();
        emptyText = (TextView) findViewById(R.id.empty_bid_string);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.bid_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                populateBidView();
                refreshLayout.setRefreshing(false);
//                Bundle settings = new Bundle();
//                settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//                settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//                ContentResolver.requestSync(getAccount(), getString(R.string.SYNC_AUTHORITY), settings);
            }
        });

        oldBids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //Log.i("ViewBids --->",task.getRequesterID() + " " + currentUser.getObjectID());
                Bid bid = bidList.get(position);
                if(getCurrentTask().getRequesterID().compareTo(getCurrentUser().getObjectID()) == 0){
//                    Bid bid = bidList.get(position);
                    if ("Completed".equals(getCurrentTask().getStatus())){
                        triggerViewProfile(view, bid, getCurrentTask());
                    }else if ("Accepted".equals(getCurrentTask().getStatus())){
                        triggerDeletePopup(view, bid, getCurrentTask());
                    }else{
                        triggerPopup(view, bid, getCurrentTask());
                        adapter.notifyDataSetChanged();
                    }
                }else if (getCurrentUser().getObjectID().equals(bid.getProviderID())){
                    triggerDeletePopup(view, bid, getCurrentTask());
                    adapter.notifyDataSetChanged();
                }else{
                    triggerViewProfile(view, bid, getCurrentTask());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_bids, menu);
        bidBtn = toolbar.getMenu().findItem(R.id.action_bid);
        if((getCurrentTask().getRequesterID().compareTo(getCurrentUser().getObjectID()) != 0)
                && ("Bidded".equals(getCurrentTask().getStatus())||"Requested".equals(getCurrentTask().getStatus()))){
            bidBtn.setVisible(true);
        } else {
            bidBtn.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bid) {
            triggerPopup();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Query the elastic search server and fill the list view
     * with all bids for the task
     */
    private void populateBidView(){

        /// THIS SHOULD WORK BUT IS CURRENTLY COMMENTED OUT
        if ("Accepted".equals(getCurrentTask().getStatus())) {

            MasterController.AsyncGetDocument asyncGetDocument =
                    new MasterController.AsyncGetDocument(this, this);
            asyncGetDocument.execute(new AsyncArgumentWrapper(getCurrentTask().getAccpeptedBidID(), Bid.class));

            Bid accepted = null;
            try {
                accepted = (Bid) asyncGetDocument.get();
                if(bidList.isEmpty()){
                    bidList.add(accepted);
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else{
            SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
            builder.addColumns(new String[] {"taskId"});
            builder.addParameters(new String[] {getCurrentTask().getObjectID()});

            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

            try {
                bidList = (ArrayList<Bid>) asyncSearch.get();
                if(bidList != null) {
                    Collections.sort(bidList);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
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

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument(this);
        asyncUpdateDocument.execute(task);
        //The following should wok, but needs to be tested after the array is truly populated by the
        //master controller
        deleteAllBut(task);

        //go back to ViewTaskActivity
        Intent intent = new Intent(ViewBidsActivity.this, MenuActivity.class);
        intent.putExtra("task", task);
        MenuActivity.setLastClicked(getCurrentTask());
        startActivity(intent);

    }

    public void deleteAllBut(Task task){
        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskId"});
        builder.addParameters(new String[] {task.getObjectID()});

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this, this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));
        ArrayList<Bid> bidList = new ArrayList<Bid>();
        try {
            bidList = (ArrayList<Bid>) asyncSearch.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (Bid bid : bidList) {
            if (!task.getAccpeptedBidID().equals(bid.getObjectID())){
                MasterController.AsyncDeleteDocument asyncDeleteDocument =
                        new MasterController.AsyncDeleteDocument(this);
                asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
            }
        }
    }

    /**
     * sets  up popup for bid
     * accept_bid_popout xml file for popout
     */
    public void triggerPopup(){

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.accept_bid_popout, null);

        POPUP_WINDOW_DELETION = new PopupWindow(this);
        POPUP_WINDOW_DELETION.setContentView(layout);
        POPUP_WINDOW_DELETION.setFocusable(true);
        POPUP_WINDOW_DELETION.setBackgroundDrawable(null);
        POPUP_WINDOW_DELETION.showAtLocation(layout, Gravity.CENTER, 1, 1);

        final EditText value = (EditText) layout.findViewById(R.id.editTextAmmount);

        cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
            }
        });

        acceptBtn = (Button) layout.findViewById(R.id.btn_accept_bid);
        acceptBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (!value.getText().toString().isEmpty()) {
                    POPUP_WINDOW_DELETION.dismiss();
                    Double number = Double.parseDouble(value.getText().toString());
                    addBid(number);
                }
                //TODO - go back to previous intent
            }
        });
    }

    private void deleteOldBids(String keeper){
        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskId"});
        builder.addParameters(new String[] {getCurrentTask().getObjectID()});

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this, this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        try {
            for(Bid bid : (ArrayList<Bid>) asyncSearch.get()){
            //for(Bid bid : bidList){
                if((bid.getObjectID().compareTo(keeper) !=0) && (bid.getProviderID().compareTo(getCurrentUser().getObjectID()) == 0)){
                    MasterController.AsyncDeleteDocument asyncDeleteDocument =
                            new MasterController.AsyncDeleteDocument(this);
                    asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
                    if(bidList.contains(bid)){
                        bidList.remove(bid);
                    }
                }
            }
            for(Bid bid : bidList){
                if((bid.getObjectID().compareTo(keeper) !=0) && (bid.getProviderID().compareTo(getCurrentUser().getObjectID()) == 0)){
                    MasterController.AsyncDeleteDocument asyncDeleteDocument =
                            new MasterController.AsyncDeleteDocument(this);
                    asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
                    if(bidList.contains(bid)){
                        bidList.remove(bid);
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     *  uses MasterController to add a new bid. value is passode over
     @throws InterruptedException

      * @param value
     */
    private void addBid(Double value){
        Bid bid = new Bid(getCurrentUser().getObjectID(), value, getCurrentTask().getObjectID());
        deleteOldBids(bid.getObjectID());

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(this);
        asyncCreateNewDocument.execute(bid);

        //populateBidView();
        Log.i("Adding --->", bid.getValue().toString());
        bidList.add(bid);
        Collections.sort(bidList);
        Log.i("Size --->",String.format("%d", bidList.size()));

        adapter = new BidArrayAdapter(this, R.layout.bid_list_item, bidList);
        oldBids.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        updateTaskMetaData(this);
    }

    /**
     * Allows a user to delete their bid on a task
     */
    public void deleteBid(Bid bid, Task task){
        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument(this);
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskId"});
        builder.addParameters(new String[] {task.getObjectID()});

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this, this);
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
        updateTaskMetaData(this);
        updateTaskAfterDelete(bid, task);
    }


    /**
     * Method to push any changes to bids on a task to the server
     */
    public void updateTaskAfterDelete(Bid bid , Task task){

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

        setResult(Activity.RESULT_OK, back);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument(this);
        asyncUpdateDocument.execute(task);
    }

    /**
     * Set up buttons in the view for the popup with various bid functionality
     */
    public void triggerPopup(View view, final Bid bid, final Task task){

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getProviderID(), User.class));

        try {
            profile = (User) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

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

                intent.putExtra(getString(R.string.VIEW_USER), profile); //ToDo issue #31 here????
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

        if(getCurrentUser().getObjectID().compareTo(getCurrentTask().getRequesterID()) == 0) {
            deleteBtn.setText("DECLINE");
        }

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
    public void triggerViewProfile(View view, final Bid bid, final Task task){
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getProviderID(), User.class));

        try {
            profile = (User) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.view_profile_bid_popout, null);

        POPUP_WINDOW_PROFILE = new PopupWindow(this);
        POPUP_WINDOW_PROFILE.setContentView(layout);
        POPUP_WINDOW_PROFILE.setFocusable(true);
        POPUP_WINDOW_PROFILE.setBackgroundDrawable(null);
        POPUP_WINDOW_PROFILE.showAtLocation(layout, Gravity.CENTER, 1, 1);

        Button profileBtn = (Button) layout.findViewById(R.id.btn_delete_my_bid);  // this goes to profile. for some reason if i change the name, the layout messes up
        profileBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_PROFILE.dismiss();
                Intent intent = new Intent(ViewBidsActivity.this, ViewProfileActivity.class);
//                intent.putExtra("userID", bid.getProviderID());

                intent.putExtra(getString(R.string.VIEW_USER), profile); //ToDo issue #31 here????
                startActivity(intent);
            }
        });

        Button backBtn = (Button) layout.findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_PROFILE.dismiss();
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
