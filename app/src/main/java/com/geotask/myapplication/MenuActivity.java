package com.geotask.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.geotask.myapplication.Adapters.FastTaskArrayAdapter;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.Helpers.DistanceCalculator;
import com.geotask.myapplication.Controllers.Helpers.GetKeywordMatches;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.google.gson.Gson;

import junit.framework.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

import java.util.List;
import java.util.concurrent.ExecutionException;


/** MenuActivity
 *
 * This is the main hub of the app. Here the user can filter through the database of tasks, view
 * their own tasks, view other tasks, go to a map view, view their own profile or logout.
 *
 * Resources:
 *
 * https://stackoverflow.com/questions/8244252/star-button-in-android
 *      FOR WISHLIST BUTTON LATER
 *
 * https://stackoverflow.com/questions/2354336/android-pressing-back-button-should-exit-the-app
 *      allows for user to use back button without closing app
 *
 * https://stackoverflow.com/questions/36694263/how-to-change-navigation-header-widgets-value-dynamically
 *      For editing the nav_header
 *      Author Exaqt, April 18, 2016, no license stated
 *
 * https://stackoverflow.com/questions/37458500/android-screen-rotation-detection
 *      For listening for screen rotation
 *      Author gRRosiminet, May 26, 2016, no licence stated
 *
 * https://android--code.blogspot.ca/2015/09/android-how-to-get-screen-width-and.html
 *      Author Saiful Alam, For getting Screen size, September 27, 2015, no licence stated
 *
 */
public class MenuActivity extends AbstractGeoTaskActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncCallBackManager {

    private ListView oldTasks; //named taskListView
    private ArrayAdapter<Task> adapter;
    private String mode;
    //private ArrayList<String> filterArray;
    private String[] filterArray;
    private FloatingActionButton fab;
    private Button clearFiltersButton;
    private ArrayList<Bid> bidFilterList;
    private OrientationEventListener orientationEventListener = null;
    public static int screenWidthInDPs;
    public static int curOrientation;
    private Context context;
    NavigationView navigationView;
    View headerView;
    ImageView drawerImage;
    TextView drawerUsername;
    TextView drawerEmail;
    TextView emptyText;
    SwipeRefreshLayout refreshLayout;
    BroadcastReceiver syncProgress;

    Task lastClickedTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        Bundle settings = new Bundle();
//        settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.requestSync(getAccount(), getString(R.string.SYNC_AUTHORITY), settings);

        //get current user location to store in the abstract class
        locationString = retrieveLocation(this);

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context=getApplicationContext();
        oldTasks = findViewById(R.id.taskListView);
        emptyText = findViewById(R.id.empty_task_string);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                syncTasksFromServer();
                populateTaskView();
                refreshLayout.setRefreshing(false);
                notifyUser();
            }
        });


        if((getTaskList() == null) || (getTaskList().size() == 0)) {
            setTaskList(new ArrayList<Task>());
        }
        adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
        oldTasks.setAdapter(adapter);

        screenWidthInDPs = this.getScreenWidthInDPs();

        //TODO - change these to match below
        if(getViewMode() ==  R.integer.MODE_INT_ALL) {
            getSupportActionBar().setTitle("All Tasks");
        } else if(getViewMode() == R.integer.MODE_INT_REQUESTER) {
            getSupportActionBar().setTitle("My Tasks");
        } else if(getViewMode() == R.integer.MODE_INT_PROVIDER) {
            getSupportActionBar().setTitle("Tasks I Have Bid On");
        }


        try {
            if(filterArray != null) {
                filterArray = getSearchKeywords().split(" ");
                //for
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            //filterArray = new ArrayList<String>();
            setSearchKeywords("");
        }


        //filterArray = new ArrayList<String>();
        //setSearchKeywords("");
        //Log.i("filter-a-------->", getSearchKeywords());

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = findViewById(R.id.fab);
        /*
            onClick Listener for the floating action button. Here we will start the AddTaskActivity
            and pass the current user to it
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("myTag","FAB Clicked");
                Intent intent = new Intent(MenuActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        clearFiltersButton = findViewById(R.id.clearFilterButton);
        //onClick Listener for the clear filters button.
        clearFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View snackView = getCurrentFocus();
                Log.v("myTag","Clear Filters Clicked");
                Snackbar.make(snackView, "Search Filters Cleared", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                setSearchKeywords("");
                setSearchStatus("All");
                getSupportActionBar().setTitle("All Tasks");
                setViewMode(R.integer.MODE_INT_ALL);
                navigationView.setCheckedItem(R.id.nav_browse);
                populateTaskView();
                clearFiltersButton.setVisibility(View.INVISIBLE);
            }
        });

        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        /*
            onClick listener for the oldTasks list of Tasks
         */
        oldTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(view.equals(R.id.btn_star)) {
                    Log.i("click --->", "clicked");
                } else {
                    Log.i("click --->", "not-clicked");
                }
//                if(getTaskList().size() >= position){
                    Task task = getTaskList().get(position);
                    MenuActivity.setLastClicked(task);
                    Intent intent = new Intent(MenuActivity.this, ViewTaskActivity.class);
                    setCurrentTask(task);
                    startActivity(intent);
                    Log.i("LifeCycle --->", "after activity return");
//                }

            }
        });

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            curOrientation = 0;
        }
        else {
            curOrientation = 1;
        }

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                setOrientation();
                Log.i("orient ------>", String.format("oriented: %d", orientation));
                screenWidthInDPs = getScreenWidthInDPs();
                adapter.notifyDataSetChanged();
            }
        };
        orientationEventListener.enable();
    }

    /**
     *  This method hides the floating action button
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        //Log.i("LifeCycle --->", "onStart is called");
        fab.show();
        navigationView.setCheckedItem(R.id.nav_browse);

        syncProgress = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("geotasksync", "received");
                populateTaskView();
            }
        };
        registerReceiver(syncProgress, new IntentFilter("broadcast"));
        Log.d("geotasksync", "registered");
        notifyUser();
    }

    /**
     * This button populates the listView and sets the navigation header to be custom to the current user
     *
     */
    @Override
    protected void onResume(){
        super.onResume();
        //populate the array on start
        populateTaskView();
        headerView = navigationView.getHeaderView(0);
        drawerImage = (ImageView) headerView.findViewById(R.id.drawer_image);
        drawerUsername = (TextView) headerView.findViewById(R.id.drawer_name);
        drawerEmail = (TextView) headerView.findViewById(R.id.drawer_email);




        //TODO - set drawerImage to user profile pic
        drawerUsername.setText(getCurrentUser().getName());
        drawerEmail.setText(getCurrentUser().getEmail());
        int n = getCurrentUser().getUserPhoto().length;
        Log.i("checklength",String.valueOf(n));
        if(getCurrentUser().getUserPhoto().length == 0){
            Glide.with(context).load(R.drawable.defaultheadshot).into(drawerImage);
        }else{
            Glide.with(context).load(getCurrentUser().getUserPhoto()).into(drawerImage);}

        drawerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ViewProfileActivity.class);
                intent.putExtra("user_being_viewed", getCurrentUser());
                startActivity(intent);
            }
        });




        drawerUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ViewProfileActivity.class);
                intent.putExtra("user_being_viewed", getCurrentUser());
                startActivity(intent);
            }
        });




        drawerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ViewProfileActivity.class);
                intent.putExtra("user_being_viewed", getCurrentUser());
                startActivity(intent);
            }
        });
    }

    /**
     * This method is responsible for grabbing the new user object
     */
    @Override
    protected void onRestart(){
        super.onRestart();
        setOrientation();
    }

    public void notifyUser(){
//        SQLQueryBuilder builder1 = new SQLQueryBuilder(Task.class);
//        builder1.addColumns(new String[] {"requesterID"});
//        builder1.addParameters(new String[] {getCurrentUser().getObjectID()});
//
//        MasterController.AsyncSearch asyncSearch =
//                new MasterController.AsyncSearch(this, this);
//        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));

        try {
            SuperBooleanBuilder superBuilder2 = new SuperBooleanBuilder();
            superBuilder2.put("requesterID", getCurrentUser().getObjectID());
            ArrayList<Task> newList = (ArrayList<Task>) MasterController.slowSearch(new AsyncArgumentWrapper(superBuilder2, Task.class));

//            ArrayList<Task> newList = (ArrayList<Task>) asyncSearch.get();
            Boolean nofifyBool = false;
            for (Task t: newList){
                if (!t.getBidList().isEmpty()){
                    nofifyBool = true;
                }
            }
            if (nofifyBool == true){
                int notifyID = 1;
                String CHANNEL_ID = "my_channel_01";// The id of the channel.
                CharSequence name = "yes";// The user-visible name of the channel.
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
// Create a notification and set the notification channel.
                Notification notification = new Notification.Builder(MenuActivity.this)
                        .setContentTitle("You Have New Bids")
                        .setContentText("Go to Notifications to View Bids")
                        .setSmallIcon(R.drawable.geotaskicon)
                        .setChannelId(CHANNEL_ID)
                        .build();

                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mNotificationManager.createNotificationChannel(mChannel);
                }

                mNotificationManager.notify(0, notification);
            }
        }catch (NullPointerException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e){
//            e.printStackTrace();
//        }catch (ExecutionException e){
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(syncProgress);
    }

    /**
     * This function populates the listView by querying the server based on the view mode
     *
     */
    private void populateTaskView(){
        Log.i("start with2------->", String.format("%d", getTaskList().size()));
        /*
            Handling cases where the taskList has already been set by outside
         */
        if(getViewMode() == R.integer.MODE_INT_STARRED){
            getTaskList().clear();
            setStarredMode();
            return;
        } else if (getViewMode() == R.integer.MODE_INT_HISTORY) {
            getSupportActionBar().setTitle("My History");
            clearFiltersButton.setVisibility(View.VISIBLE);
            adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
            oldTasks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setEmptyString();
            return;
        } else if (getViewMode() == R.integer.MODE_INT_OTHERS_TASKS) {
            getSupportActionBar().setTitle(String.format("%s\'s Tasks", getLastViewedUser().getName()));
            Log.i("other------>", String.format("%d", getViewMode()));
            clearFiltersButton.setVisibility(View.VISIBLE);
            adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
            oldTasks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setEmptyString();
            return;
        } else if (getViewMode() == R.integer.MODE_INT_NOTIFICATIONS){
            SuperBooleanBuilder superBuilder3 = new SuperBooleanBuilder();
            superBuilder3.put("requesterID", getCurrentUser().getObjectID());
            ArrayList<Task> newList = (ArrayList<Task>) MasterController.slowSearch(new AsyncArgumentWrapper(superBuilder3, Task.class));

            HashSet<String> bidList = new HashSet<>();
            ArrayList<Task> remove = new ArrayList<Task>();
            if (newList != null){
                for (Task t : newList) {
                    if (t.getBidList().isEmpty()) {
                        remove.add(t);
                    }
                    t.setBidList(bidList);
                    MasterController.AsyncUpdateDocument asyncUpdateDocument =
                            new MasterController.AsyncUpdateDocument(this);
                    asyncUpdateDocument.execute(t);
                }
                newList.removeAll(remove);
                clearFiltersButton.setVisibility(View.VISIBLE);
                adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, newList, getLastClicked(), getCurrentUser());
                oldTasks.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                setEmptyString();
            }else{
                clearFiltersButton.setVisibility(View.VISIBLE);
                adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, remove, getLastClicked(), getCurrentUser());
                oldTasks.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                setEmptyString();
            }

            return;
        }

        /*
            Otherwise the a builder is need and we need tro check the viewmode
         */
        Log.i("none------>", String.format("%d", getViewMode()));
        getTaskList().clear();
        SQLQueryBuilder builder1 = new SQLQueryBuilder(Task.class);

        Boolean anyStatus = false;
        //Only show tasks created by the user
        if(getViewMode() == R.integer.MODE_INT_REQUESTER) {
            setSearchStatus(null);
            builder1.addColumns(new String[] {"requesterID"});
            builder1.addParameters(new String[] {getCurrentUser().getObjectID()});
            anyStatus = true;
        }else if(getViewMode() == R.integer.MODE_INT_ACCEPTED) {
            setSearchStatus(null);
            builder1.addColumns(new String[] {"requesterID", "status"});
            builder1.addParameters(new String[] {getCurrentUser().getObjectID(), "Accepted"});
            anyStatus = true;
        } else if(getViewMode() == R.integer.MODE_INT_ASSIGNED) {
            setSearchStatus(null);
            builder1.addColumns(new String[] {"acceptedProviderID"});
            builder1.addParameters(new String[] {getCurrentUser().getObjectID()});
            anyStatus = true;
        }

        /*
            Add filter keywords to the builder if present
        */
        String inString = "";
        try {
            Boolean showClear = false;
            String test = getSearchKeywords();
            Boolean tes2 = (getSearchKeywords() != null);
            if((getSearchKeywords() != null) && !(getSearchKeywords().compareTo("") == 0)) {
                showClear = true;
                clearFiltersButton.setVisibility(View.VISIBLE);
                Boolean first = true;
                for (String searchTerm : getSearchKeywords().split(" ")){
                    if(!first){
                        inString+= "OR ";
                    }
                    inString += "description LIKE \"%" + searchTerm + "%\" ";
                    first = false;
                }

                if (getSearchKeywords().split(" ").length > 0){
                    Log.d("BUGSBUGSBUGSmenu", String.valueOf(builder1.build().getSql()));
                    builder1.addRaw(" ( " + inString + " ) ");
                    navigationView.setCheckedItem(R.id.nav_filter);
                }

            } else {
                clearFiltersButton.setVisibility(View.INVISIBLE);
            }

            /*
                Set status field of query if defined
             */
            if (getSearchStatus()!= null){
                clearFiltersButton.setVisibility(View.VISIBLE);
                int test2 = getViewMode();
                if(getViewMode() == R.integer.MODE_INT_ALL) {
                    if(getSearchStatus().compareTo("All") ==0){
                        if(!showClear) {
                            clearFiltersButton.setVisibility(View.INVISIBLE);
                        }
                    }
                    if(getSearchStatus().compareTo("Requested") == 0) {
                        builder1.addColumns(new String[] {"status"});
                        builder1.addParameters(new String[] {"Requested"});

                    } else if(getSearchStatus().compareTo("Bidded") == 0) {
                        builder1.addColumns(new String[] {"status"});
                        builder1.addParameters(new String[] {"Bidded"});
                    } else if(getSearchStatus().compareTo("All") == 0) {
                        builder1.addRaw(" (status = \"Bidded\" OR status = \"Requested\" ) ");
                    }
                }
            } else {
                if(!anyStatus){
                    builder1.addRaw(" ( status = \"Bidded\" OR status = \"Requested\" ) ");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        /*
               Perform the search if the mode is all (ie filter or all) or if status was set
        */
        if(anyStatus || getViewMode() == R.integer.MODE_INT_ALL) {
            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));

            try {
                setTaskList((ArrayList<Task>) asyncSearch.get());
                ArrayList<Task> newList = getTaskList();
                if (inString.compareTo("") != 0) {
                    newList = GetKeywordMatches.getSortedResults(newList, getSearchKeywords());
                }
                setTaskList(newList);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            //remove all the tasks that aren't in the specified range
            //https://www.geodatasource.com/developers/java
            //Used for calculating the distance between two points
            if(getSearchRange() != -1){
                //retrieve the user's current location
                String userloc = retrieveLocation(this);
                if(userloc != null) {
                    Double userX = Double.parseDouble((userloc.split(",")[0]));
                    Double userY = Double.parseDouble((userloc.split(",")[1]));

                    for (int i = 0; i < getTaskList().size(); i++) {
                        Task currentTask = getTaskList().get(i);
                        if (currentTask.getLocationX() == null || currentTask.getLocationX().equals("null")) {
                            getTaskList().remove(i);
                            i--;
                        } else {
                            Double taskX = currentTask.getLocationX();
                            Double taskY = currentTask.getLocationY();

                            Double distance = DistanceCalculator.distance(userX, userY, taskX, taskY, "K");

                            if (distance > getSearchRange()) {
                                getTaskList().remove(i);
                                i--;
                            }
                        }
                    }
                }
            }
        }

        /*
            If the mode is provider we query for all of the users bids, and then query for tasks with
            matching objects Ids
         */
        if(getViewMode() == R.integer.MODE_INT_PROVIDER) {
            //raw query, string will not cause security issues
            String query1 = String.format(
                    "SELECT\n" +
                    "  *\n" +
                    "FROM\n" +
                    "  bids\n" +
                    "WHERE\n" +
                    "  providerId = \"%s\"\n", getCurrentUser().getObjectID());


            Log.i("SQL:", query1);

            //make the builder
            SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
            builder.setRawQuery(query1);

            //call the search
            MasterController.AsyncSearch asyncSearch1 =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch1.execute(new AsyncArgumentWrapper(builder, Bid.class));

            try {
                ArrayList<Bid> tasksBidOn = ((ArrayList<Bid>) asyncSearch1.get());
                String keyList = "";
                Boolean first = true;

                /*
                    this for loop sets the set that is fed to SQL for matching in the form of:
                        ("item1", "item2", ..., "itemN")
                */
                for(Bid bid : tasksBidOn){
                    if(!first){
                        keyList += ", ";
                    }
                    keyList += "\"" + bid.getTaskID() + "\"";
                    first = false;
                }
                keyList = "(" + keyList + ")";

                SQLQueryBuilder builder2  = new SQLQueryBuilder(Task.class);
                String query2 = String.format(
                        "SELECT\n" +
                        "  *\n" +
                        "FROM\n" +
                        "  tasks\n" +
                        "WHERE\n" +
                        "  status != \"Requested\" " +
                        "    AND objectId IN %s\n", keyList);
                builder2.setRawQuery(query2);
                Log.i("SQL:", query2);

                MasterController.AsyncSearch asyncSearch2 =
                        new MasterController.AsyncSearch(this, this);
                asyncSearch2.execute(new AsyncArgumentWrapper(builder2, Task.class));

                setTaskList((ArrayList<Task>) asyncSearch2.get());

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        /*
            If the mode is provider we have one query
        */
        } else if(getViewMode() == R.integer.MODE_INT_ASSIGNED) {
            SQLQueryBuilder builder = new SQLQueryBuilder(Task.class);
            String query = String.format(
                    "SELECT\n" +
                    "  *\n" +
                    "FROM\n" +
                    "  tasks\n" +
                    "WHERE\n" +
                    "  status = \"Accepted\" " +
                            "AND acceptedProviderID = \"%s\"\n", getCurrentUser().getObjectID());

            builder.setRawQuery(query);

            MasterController.AsyncSearch asyncSearch1 =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch1.execute(new AsyncArgumentWrapper(builder, Task.class));

            try {
                setTaskList((ArrayList<Task>) asyncSearch1.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
        oldTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        setEmptyString();

    }

    public void setEmptyString(){
        Log.i("TaskList ----->", String.format("%d", getTaskList().size()));
        if(getTaskList().size() == 0){
            emptyText.setText("No Tasks");
            emptyText.setVisibility(View.VISIBLE);
            oldTasks.setVisibility(View.INVISIBLE);
        } else {
            emptyText.setText("");
            emptyText.setVisibility(View.INVISIBLE);
            oldTasks.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Prevent the user from returning to login with the back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(getViewMode() == R.integer.MODE_INT_HISTORY) {
            Intent intent = new Intent(MenuActivity.this, ViewProfileActivity.class);
            intent.putExtra("user_being_viewed", getCurrentUser());
            startActivity(intent);
        } else if(getViewMode() == R.integer.MODE_INT_OTHERS_TASKS){
            Intent intent = new Intent(MenuActivity.this, ViewProfileActivity.class);
            intent.putExtra("user_being_viewed", getLastViewedUser());
            startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    /**
     * This function sets the navigation bar variables and their click listeners
     *
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        View snackView = getCurrentFocus();
        Assert.assertNotNull(snackView);

        int id = item.getItemId();

        if (id == R.id.nav_browse) {
            getSupportActionBar().setTitle("All Tasks");
            setViewMode(R.integer.MODE_INT_ALL);
            setSearchKeywords("");
            setSearchStatus("All");
            populateTaskView();
            adapter.notifyDataSetChanged();

        } else if (id == R.id.nav_filter) {
            Intent intent = new Intent(getBaseContext(), FilterActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MenuActivity.this, ViewProfileActivity.class);
            intent.putExtra("user_being_viewed", getCurrentUser());
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(getBaseContext(), MapActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //MasterController.shutDown();
        }  else if (id == R.id.nav_assigned) {
            fab.hide();
            setViewMode(R.integer.MODE_INT_ASSIGNED); //TODO - add the map
            getSupportActionBar().setTitle("My Assigned Tasks");
            Snackbar.make(snackView, "Changed view to \"My Assigned\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        }  else if (id == R.id.nav_starred) {
            fab.hide();
            setViewMode(R.integer.MODE_INT_STARRED); //TODO - add the map
            getSupportActionBar().setTitle("My Starred Tasks");
            Snackbar.make(snackView, "Changed view to \"My Starred\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        } else if (id == R.id.nav_accepted) {
            fab.hide();
            setViewMode(R.integer.MODE_INT_ACCEPTED); //TODO - add the map
            getSupportActionBar().setTitle("My Accepted Tasks");
            Snackbar.make(snackView, "Changed view to \"My Accepted\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        } else if (id == R.id.nav_requester) {
            fab.show();
            setViewMode(R.integer.MODE_INT_REQUESTER);
            getSupportActionBar().setTitle("My Tasks");
            Snackbar.make(snackView, "Changed view to \"Requester\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        } else if (id == R.id.nav_notifications) {
            fab.show();
            setViewMode(R.integer.MODE_INT_NOTIFICATIONS);
            getSupportActionBar().setTitle("Tasks With New Bids");
            Snackbar.make(snackView, "Changed view to \"Requester\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        }else if (id == R.id.nav_provider) {
            fab.hide();
            setViewMode(R.integer.MODE_INT_PROVIDER);
            getSupportActionBar().setTitle("Tasks I Have Bid On");
            Snackbar.make(snackView, "Changed view to \"Provider\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        } else if (id == R.id.nav_all) {
            fab.show();
            setViewMode(R.integer.MODE_INT_ALL);
            getSupportActionBar().setTitle("All Tasks");
            Snackbar.make(snackView, "Changed view to \"All\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            setSearchStatus("All");
            populateTaskView();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        //navigationView.setCheckedItem(id);
        return true;
    }

    //by Saiful Alam (see above references)
    public int getScreenWidthInDPs(){
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int widthInDP = Math.round(dm.widthPixels / dm.density);
        return widthInDP;
    }

    private void setOrientation(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            curOrientation = 0;
        }
        else {
            curOrientation = 1;
        }
    }

    public void setStarredMode(){
        View snackView = getCurrentFocus();
        ArrayList<Task> starredTaskList = new ArrayList<Task>();
        //for(String taskID : getCurrentUser().getStarredList()){
        for(String taskID : getStarHash().keySet()){
            MasterController.AsyncGetDocument asyncGetDocument =
                    new MasterController.AsyncGetDocument(this, this);
            asyncGetDocument.execute(new AsyncArgumentWrapper(taskID, Task.class));
            Task task = null;
            try {
                task = (Task) asyncGetDocument.get();
                //starredTaskList.add(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(task != null) {
                starredTaskList.add(task);
            } else {
                Snackbar.make(snackView, "Some tasks no longer exist and were removed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                removeFromStarHash(taskID);
            }
        }
        setTaskList(starredTaskList);
        adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
        oldTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        saveStarHashToServer();
        setEmptyString();
    }

    private void syncTasksFromServer(){
        if(networkIsAvailable()) {
            //grab from server
            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            MasterController.AsyncSearchServer asyncSearchServer =
                    new MasterController.AsyncSearchServer(this);
            asyncSearchServer.execute(new AsyncArgumentWrapper(builder, Task.class));

            //grab from local
            SQLQueryBuilder builder2 = new SQLQueryBuilder(Task.class);
            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder2, Task.class));

            try {
                ArrayList<Task> serverList = (ArrayList<Task>) asyncSearchServer.get();
                ArrayList<Task> localList = (ArrayList<Task>) asyncSearch.get();

                if((serverList == null) || (localList == null)){
                    return;
                }
                HashMap<Task, Task> localHash = new HashMap<Task, Task>();
                for(Task task : localList){
                    localHash.put(task, task);
                }
                for(Task task : serverList){
                    if(localHash.containsKey(task)){
                        String string1 = new Gson().toJson(task);
                        String string2 = new Gson().toJson(localHash.get(task));
                        //if the tasks are not the same
                        if(string1.compareTo(string2) != 0){
                            MasterController.AsyncUpdateLocalDocument asyncUpdateLocalDocument =
                                    new MasterController.AsyncUpdateLocalDocument(this);
                            asyncUpdateLocalDocument.execute(task);
                        }
                    } else {
                        MasterController.AsyncCreateNewLocalDocument asyncCreateNewLocalDocument =
                                new MasterController.AsyncCreateNewLocalDocument(this);
                        asyncCreateNewLocalDocument.execute(task);
                    }
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPostExecute(GTData data) {
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        //taskList.clear();
        //taskList.addAll((Collection<? extends Task>) dataList);
        //adapter.notifyDataSetChanged();
    }
}
