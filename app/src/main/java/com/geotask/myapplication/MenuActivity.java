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
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/*
*
* FOR WISHLIST BUTTON LATER https://stackoverflow.com/questions/8244252/star-button-in-android
*
*/
public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AsyncCallBackManager {

    private ListView oldTasks; //named taskListView
    private ArrayList<Task> taskList;
    private ArrayAdapter<Task> adapter;
    private String mode;
    private FloatingActionButton fab;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER)); //ToDo switch to Parcelable

        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        oldTasks = findViewById(R.id.taskListView);
        taskList = new ArrayList<>();
        adapter = new TaskArrayAdapter(this, R.layout.task_list_item, taskList);
        oldTasks.setAdapter(adapter);

        mode = getString(R.string.MODE_ALL);

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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("myTag","FAB Clicked");
                Intent intent = new Intent(MenuActivity.this, AddTaskActivity.class);
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivity(intent);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        oldTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //TODO - this needs to work lol

                Task task = taskList.get(position);
                Intent intent = new Intent(MenuActivity.this, TaskViewActivity.class);
                intent.putExtra(getString(R.string.TASK_BEING_VIEWED), task);
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivity(intent);
            }
        });

    }
    /* This method loads the subList from savefile and sets the array adapter for the ListView
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        //Log.i("LifeCycle --->", "onStart is called");
        //populate the array on start
        fab.hide();
        populateTaskView();
        //TODO - need to get the mode of user, assuming all rn


    }

    @Override
    protected void onResume(){
        super.onResume();
        populateTaskView();

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER)); //ToDo switch to Parcelable
    }

    /**
     *
     *
     */
    private void populateTaskView(){
        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        if(mode.compareTo(getString(R.string.MODE_REQUESTER)) == 0){
            builder1.put("requesterID", currentUser.getObjectID());
        }

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));

        try {
            taskList = (ArrayList<Task>) asyncSearch.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        adapter = new TaskArrayAdapter(this, R.layout.task_list_item, taskList);
        oldTasks.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
