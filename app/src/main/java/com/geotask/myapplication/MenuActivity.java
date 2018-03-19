package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.geotask.myapplication.Adapters.FastTaskArrayAdapter;
import com.geotask.myapplication.Adapters.TaskArrayAdapter;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.Helpers.GetLowestBidFromServer;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import junit.framework.Assert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.geotask.myapplication.Controllers.Helpers.BidListConverter.JsonToList;
import static java.sql.DriverManager.println;


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
 */
public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncCallBackManager {

    private ListView oldTasks; //named taskListView
    private ArrayList<Task> taskList;
    private ArrayAdapter<Task> adapter;
    private String mode;
    private String filters;
    private String[] filterArray;
    private FloatingActionButton fab;
    private User currentUser;
    private ArrayList<Bid> bidFilterList;
    NavigationView navigationView;
    View headerView;
    ImageView drawerImage;
    TextView drawerUsername;
    TextView drawerEmail;
    Task lastClickedTask = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER)); //ToDo switch to Parcelable

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        oldTasks = findViewById(R.id.taskListView);
        taskList = new ArrayList<>();
        adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, taskList, lastClickedTask);
        oldTasks.setAdapter(adapter);

        mode = getString(R.string.MODE_ALL);
        filters = "";
        try {
            filters = getIntent().getStringExtra("searchFilters");
            filterArray = filters.split(" ");
        } catch (NullPointerException e) {
            e.printStackTrace();
            filters = "";
        }

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
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivity(intent);
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
                Task task = taskList.get(position);
                lastClickedTask = task;
                Intent intent = new Intent(MenuActivity.this, TaskViewActivity.class);
                intent.putExtra(getString(R.string.TASK_BEING_VIEWED), task);
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivity(intent);
                Log.i("LifeCycle --->", "after activity return");
            }
        });

    }

    /**
     *  This method hides the floating action button
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        //Log.i("LifeCycle --->", "onStart is called");
        //populate the array on start
        fab.hide();
        //TODO - set the email and name

    }

    /**
     * This button populates the listView and sets the navigation header to be custom to the current user
     *
     */
    @Override
    protected void onResume(){
        super.onResume();
        populateTaskView();
        headerView = navigationView.getHeaderView(0);
        drawerImage = (ImageView) headerView.findViewById(R.id.drawer_image);
        drawerUsername = (TextView) headerView.findViewById(R.id.drawer_name);
        drawerEmail = (TextView) headerView.findViewById(R.id.drawer_email);

        //TODO - set drawerImage to user profile pic
        drawerUsername.setText(currentUser.getName());
        drawerEmail.setText(currentUser.getEmail());

    }

    /**
     * This method is responsible for grabbing the new user object
     */
    @Override
    protected void onRestart(){
        super.onRestart();
        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER)); //ToDo switch to Parcelable
    }

    /**
     * This function populates the listView by querying the server based on the view mode
     *
     */
    private void populateTaskView(){
        taskList.clear();
        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        if(mode.compareTo(getString(R.string.MODE_REQUESTER)) == 0){
            builder1.put("requesterID", currentUser.getObjectID());
        }

        try {
            if(!filters.equals("")) {
                for(int i = 0; i < filterArray.length; i++) {
                    builder1.put("description", filterArray[i].toLowerCase());
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));

        try {
            taskList = (ArrayList<Task>) asyncSearch.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

       if(mode.compareTo("Provider") == 0) {
            //Only show tasks which have been bidded on by current user
            //Need to do this after elastic search by removing results without bids by the user
           for(int i = 0; i < taskList.size(); i++) {
               SuperBooleanBuilder builder2 = new SuperBooleanBuilder();
               builder2.put("taskID", taskList.get(i).getObjectID());

               MasterController.AsyncSearch asyncSearch2 =
                       new MasterController.AsyncSearch(this);
               asyncSearch2.execute(new AsyncArgumentWrapper(builder2, Bid.class));

               try {
                   bidFilterList = (ArrayList<Bid>) asyncSearch2.get();
               } catch (InterruptedException | ExecutionException e) {
                   e.printStackTrace();
               }
               Boolean bidBool = false;
               for(int j = 0; j < bidFilterList.size(); j++) {
                   try {
                       if (bidFilterList.get(j).getProviderID().compareTo(currentUser.getObjectID()) == 0) {
                           bidBool = true;
                       }
                   } catch (IndexOutOfBoundsException e) {
                       e.printStackTrace();
                   }
               }
               if(!bidBool) {
                   taskList.remove(i);
                   i--;
               }
           }
       }

        adapter = new FastTaskArrayAdapter(this, R.layout.task_list_item, taskList, lastClickedTask);
        oldTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * Prevent the user from returning to login with the back button
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
//        else {
//            super.onBackPressed();
//        }
//

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
            // Handle the camera action

        } else if (id == R.id.nav_filter) {
            Intent intent = new Intent(getBaseContext(), FilterActivity.class);
            intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(MenuActivity.this, EditProfileActivity.class);
            intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
            startActivity(intent);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(getBaseContext(), MapActivity.class);
            intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }  else if (id == R.id.nav_requester) {
            fab.show();
            mode = getString(R.string.MODE_REQUESTER);
            Snackbar.make(snackView, "Changed view to \"Requester\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();

        } else if (id == R.id.nav_provider) {
            fab.hide();
            mode = getString(R.string.MODE_PROVIDER);
            Snackbar.make(snackView, "Changed view to \"Provider\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();

        } else if (id == R.id.nav_all) {
            fab.hide();
            mode = getString(R.string.MODE_ALL);
            Snackbar.make(snackView, "Changed view to \"All\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            populateTaskView();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
