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
import android.widget.ListView;

import com.geotask.myapplication.Adapters.TaskArrayAdapter;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import junit.framework.Assert;

import java.util.ArrayList;

/*
*
* FOR WISHLIST BUTTON LATER https://stackoverflow.com/questions/8244252/star-button-in-android
*
*/
public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView oldTasks; //named taskListView
    private ArrayList<Task> taskList;
    private ArrayAdapter<Task> adapter;
    private String mode;
    FloatingActionButton fab;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LifeCycle --->", "onCreate is called");
        super.onCreate(savedInstanceState);

        currentUser = (User) getIntent().getSerializableExtra("currentUser"); //ToDo switch to Parcelable

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        oldTasks = (ListView) findViewById(R.id.taskListView);
        taskList = new ArrayList<Task>();
        MasterController.verifySettings();

        mode = "all";

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(MenuActivity.this, AddTaskActivity.class);
                //startActivity(intent);
                adapter.notifyDataSetChanged();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        oldTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO - this needs to work lol

                Task task = taskList.get(position);
                Intent intent = new Intent(MenuActivity.this, TaskViewActivity.class);
                intent.putExtra("task", task);
                startActivity(intent);
                adapter.notifyDataSetChanged();
            }
        });



    }
    /* This method loads the subList from savefile and sets the array adapter for the ListView
     *
     */
    protected void onStart() {
        super.onStart();
        Log.i("LifeCycle --->", "onStart is called");
        //populate the array on start
        fab.hide();
        //TODO - need to get the mode of user, assuming all rn
        populateTaskView(mode, new ArrayList<String>());
        adapter = new TaskArrayAdapter(this, R.layout.task_list_item, taskList);
        oldTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("LifeCycle --->", "onResume is called");
        //populate the array on start
        //TODO - need to get the mode of user, assuming all rn
        //populateTaskView(mode, new ArrayList<String>());
        Task myTask = new Task("Hi2", "weehasdfsdfsdfsdfsdfsdfsjghfjghfjgfyughjfjgfjgfjgfgjhfgjfghjfjgfjgfgjfjgfjgfdfsdfsdfsdfsdfsw");
        taskList.add(myTask);
        adapter = new TaskArrayAdapter(this, R.layout.task_list_item, taskList);
        oldTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     *
     *
     * @param mode
     * @param terms
     */
    protected void populateTaskView(String mode, ArrayList<String> terms){
        Task myTask = new Task("Ken Wong", "weehaw");
        myTask.addBid(new Bid());
        taskList.add(myTask);

        Task myTask2 = new Task("Ken Wong2", "weehaw");
        myTask2.addBid(new Bid());
        myTask2.setAcceptedBid(0.0);
        myTask2.setStatus("Accepted");
        taskList.add(myTask2);
        /*
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        //TODO - for loop to add terms

        try {
            taskList = (ArrayList<Task>) controller.search(builder.toString(), "Task");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


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

        } else if (id == R.id.nav_profile) {

           //currentUser = (User) getIntent().getSerializableExtra("currentUser");
           // String userName = currentUser.getName();
           // String userPhone = currentUser.getPhonenum();
            //String userEmail = currentUser.getEmail();
            Intent intent = new Intent(MenuActivity.this, EditProfile.class);
            intent.putExtra("user", currentUser);
            startActivity(intent);





        } else if (id == R.id.nav_map) {

        } else if (id == R.id.nav_logout) {

        }  else if (id == R.id.nav_requester) {
            fab.show();
            mode = "Requester";
            Snackbar.make(snackView, "Changed view to \"Requester\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.nav_provider) {
            fab.hide();
            mode = "Provider";
            Snackbar.make(snackView, "Changed view to \"Provider\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (id == R.id.nav_all) {
            fab.hide();
            mode = "All";
            Snackbar.make(snackView, "Changed view to \"All\"", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
