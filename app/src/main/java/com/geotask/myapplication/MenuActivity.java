package com.geotask.myapplication;

import android.content.Intent;
import android.content.res.Configuration;
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

import com.geotask.myapplication.Adapters.FastTaskArrayAdapter;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.Helpers.GetKeywordMatches;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

import junit.framework.Assert;

import java.util.ArrayList;
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
    NavigationView navigationView;
    View headerView;
    ImageView drawerImage;
    TextView drawerUsername;
    TextView drawerEmail;
    TextView emptyText;
    SwipeRefreshLayout refreshLayout;
    private Boolean statusSearchBool;

    Task lastClickedTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        statusSearchBool = false;
        oldTasks = findViewById(R.id.taskListView);
        emptyText = findViewById(R.id.empty_task_string);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(true);
                populateTaskView();
                refreshLayout.setRefreshing(false);
//                Bundle settings = new Bundle();
//                settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//                settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//                ContentResolver.requestSync(getAccount(), getString(R.string.SYNC_AUTHORITY), settings);
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
                Task task = getTaskList().get(position);
                MenuActivity.setLastClicked(task);
                Intent intent = new Intent(MenuActivity.this, ViewTaskActivity.class);
                setCurrentTask(task);
                startActivity(intent);
                Log.i("LifeCycle --->", "after activity return");
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
    }

    /**
     * This method is responsible for grabbing the new user object
     */
    @Override
    protected void onRestart(){
        super.onRestart();
        setOrientation();
    }

    /**
     * This function populates the listView by querying the server based on the view mode
     *
     */
    private void populateTaskView(){
        Log.i("start with2------->", String.format("%d", getTaskList().size()));
        if(getViewMode() == R.integer.MODE_INT_STARRED){
            getTaskList().clear();
            setStarredMode();
            return;
        } else if (getViewMode() == R.integer.MODE_INT_HISTORY) {
            clearFiltersButton.setVisibility(View.VISIBLE);
            adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
            oldTasks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setEmptyString();
            return;
        } else if (getViewMode() == R.integer.MODE_INT_OTHERS_TASKS) {
            Log.i("other------>", String.format("%d", getViewMode()));
            clearFiltersButton.setVisibility(View.VISIBLE);
            adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, getTaskList(), getLastClicked(), getCurrentUser());
            oldTasks.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            setEmptyString();
            return;
        }
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
        } else if(getViewMode() == R.integer.MODE_INT_ACCEPTED) {
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

        //Add filter keywords to the builder if present
        String inString = "";
        try {
            Boolean showClear = false;
            String test = getSearchKeywords();
            Boolean tes2 = (getSearchKeywords() != null);
            if((getSearchKeywords() != null) && !(getSearchKeywords().compareTo("") == 0)) {
                Log.i("filter-------->", getSearchKeywords());
                Log.i("filter-------->",  getSearchKeywords().split(" ").toString());
                showClear = true;
                //filterArray = getSearchKeywords().split(" ");
                clearFiltersButton.setVisibility(View.VISIBLE);
                Boolean first = true;
                for (String searchTerm : getSearchKeywords().split(" ")){
                    if(!first){
                        inString+= ", ";
                    }
                    inString += "\"" + searchTerm + "\"";
                    first = false;
                }

                if (getSearchKeywords().split(" ").length > 0){
                    Log.d("BUGSBUGSBUGSmenu", String.valueOf(builder1.build().getSql()));
                    builder1.addRaw(" description IN (" + inString + ") ");
                    navigationView.setCheckedItem(R.id.nav_filter);
                }

            } else {
                clearFiltersButton.setVisibility(View.INVISIBLE);
            }

            statusSearchBool = false;
            if (getSearchStatus()!= null){
                clearFiltersButton.setVisibility(View.VISIBLE);
                int test2 = getViewMode();
                if(getViewMode() == R.integer.MODE_INT_ALL) {
                    if(getSearchStatus().compareTo("All") ==0){
                        if(!showClear) {
                            clearFiltersButton.setVisibility(View.INVISIBLE);
                        }
                        statusSearchBool = true;
                    }
                    if(getSearchStatus().compareTo("Requested") == 0) {
                        builder1.addColumns(new String[] {"status"});
                        builder1.addParameters(new String[] {"Requested"});

                    } else if(getSearchStatus().compareTo("Bidded") == 0) {
                        builder1.addColumns(new String[] {"status"});
                        builder1.addParameters(new String[] {"Bidded"});
                    }
                }
            } else {
                statusSearchBool = true;
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if(anyStatus || (!statusSearchBool)){
            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));

            try {
                setTaskList((ArrayList<Task>) asyncSearch.get());
                ArrayList<Task> newList = getTaskList();
                if(inString.compareTo("") != 0){
                    newList = GetKeywordMatches.getSortedResults(newList, getSearchKeywords());
                }
                setTaskList(newList);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            ArrayList<Task> tempTaskList1 = new ArrayList<Task>();
            ArrayList<Task> tempTaskList2 = new ArrayList<Task>();
            SQLQueryBuilder builder3  = builder1.clone();
            builder1.addColumns(new String[] {"status"});
            builder1.addParameters(new String[] {"Requested"});
            builder3.addColumns(new String[] {"status"});
            builder3.addParameters(new String[] {"Bidded"});
            Log.d("BUGSBUGSBUGSstatus", builder1.build().getSql() + " " + builder1.build().getArgCount() + " " + builder3.build().getSql() + " " + builder3.build().getArgCount());


            MasterController.AsyncSearch asyncSearch1 =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch1.execute(new AsyncArgumentWrapper(builder1, Task.class));
            try {
                tempTaskList1 = (ArrayList<Task>) asyncSearch1.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            MasterController.AsyncSearch asyncSearch2 =
                    new MasterController.AsyncSearch(this, this);
            asyncSearch2.execute(new AsyncArgumentWrapper(builder3, Task.class));
            try {
                tempTaskList2 = (ArrayList<Task>) asyncSearch2.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            tempTaskList1.addAll(tempTaskList2);
            if(inString.compareTo("") != 0){
                tempTaskList1 = GetKeywordMatches.getSortedResults(tempTaskList1, getSearchKeywords());
            }
            setTaskList(tempTaskList1);
        }

        /*
        if(getViewMode() == R.integer.MODE_INT_ALL) {
            clearFiltersButton.setVisibility(View.INVISIBLE);
        }
        */

        //Only show tasks which have been bidded on by current user
        //Need to do this after elastic search by removing results without bids by the user
       if(getViewMode() == R.integer.MODE_INT_PROVIDER) {
           SQLQueryBuilder builder2 = new SQLQueryBuilder(Bid.class);
           builder2.addColumns(new String[] {"providerID"});
           builder2.addParameters(new String[] {getCurrentUser().getObjectID()});

           MasterController.AsyncSearch asyncSearch2 =
                   new MasterController.AsyncSearch(this, this);
           asyncSearch2.execute(new AsyncArgumentWrapper(builder2, Bid.class));

           try {
               bidFilterList = (ArrayList<Bid>) asyncSearch2.get();
           } catch (InterruptedException | ExecutionException e) {
               e.printStackTrace();
           }

           try {
               for (int i = 0; i < getTaskList().size(); i++) {
                   Boolean bidbool = false;
                   String tempTaskID = getTaskList().get(i).getObjectID();
                   for(int j = 0; j < bidFilterList.size(); j++) {
                       if(tempTaskID.compareTo(bidFilterList.get(j).getTaskID()) == 0) {
                           bidbool = true;
                       }
                   }
                   if (!bidbool) {
                       getTaskList().remove(i);
                       i--;
                   }
               }
           } catch (IndexOutOfBoundsException e) {
               e.printStackTrace();
           }
       }

        if(getViewMode() == R.integer.MODE_INT_ASSIGNED) {
            try {
                for (int i = 0; i < getTaskList().size(); i++) {
                    Task tempTask = getTaskList().get(i);
                    if (tempTask.getAcceptedProviderID().compareTo(getCurrentUser().getObjectID()) != 0) {
                        getTaskList().remove(i);
                        i--;
                    }
                }
            } catch (IndexOutOfBoundsException e) {
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
        } else if (id == R.id.nav_provider) {
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
