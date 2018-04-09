package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * simple class to view user data
 */
public class ViewProfileActivity extends AbstractGeoTaskActivity implements AsyncCallBackManager{

    private TextView name;
    private TextView phone;
    private TextView email;
    private User viewUser;
    private TextView date;
    private TextView completed;
    private ImageView profilePic;
    private Button historyBtn;
    private MenuItem editBtn;
    private Toolbar toolbar;
    private Context context;

    /**
     * sets up vars from intent to view user data
     * can be called from viewBidsActivity
     * can be called from ViewTaskActivity
     *
     * @see ViewBidsActivity
     * @see ViewTaskActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_view_profile);
        setContentView(R.layout.app_bar_menu_profile);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context= getApplicationContext();


        Intent intent = getIntent();
        this.viewUser = (User) intent.getSerializableExtra(getString(R.string.VIEW_USER));
        this.name = findViewById(R.id.profileName);
        this.phone = findViewById(R.id.profilePhone);
        this.email = findViewById(R.id.profileEmail);
        this.date = findViewById(R.id.joinDate);
        this.profilePic = findViewById(R.id.profile_pic);
        this.historyBtn = findViewById(R.id.btn_history);
        this.completed = findViewById(R.id.num_completed);

        refresh();

        this.historyBtn.setVisibility(View.INVISIBLE);

        if(getCurrentUser().getObjectID().compareTo(viewUser.getObjectID()) ==0){
            //TODO make edit button visible
            this.historyBtn.setVisibility(View.VISIBLE);
        }
        if(getCurrentUser().getUserPhoto() != null){
            Glide.with(context).load(getCurrentUser().getUserPhoto()).into(profilePic);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getSupportActionBar().setTitle("Profile View");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        editBtn = toolbar.getMenu().findItem(R.id.action_edit);
        if(getCurrentUser().getObjectID().compareTo(viewUser.getObjectID())!=0){
            editBtn.setVisible(false);
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
        if (id == R.id.action_edit) {
            Intent intent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
            refresh();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        this.name.setText(viewUser.getName());
        this.phone.setText(viewUser.getPhonenum());
        this.email.setText(viewUser.getEmail());
        this.date.setText("Joined on " + viewUser.getDateString());
        this.completed.setText(String.format("%d", viewUser.getCompletedTasks()));
    }

    public ArrayList<Task> getViewedTasks(){
        ArrayList<Task> viewedTaskList = new ArrayList<Task>();
        //for(String taskID : getCurrentUser().getHistoryList()){
        for(String taskID : getHistoryHash().keySet()){
            MasterController.AsyncGetDocument asyncGetDocument =
                    new MasterController.AsyncGetDocument(this, this);
            asyncGetDocument.execute(new AsyncArgumentWrapper(taskID, Task.class));

            Task task = null;
            try {
                task = (Task) asyncGetDocument.get();
                if((task != null) && (task.getStatus().toLowerCase().compareTo("accepted") != 0)
                        && (task.getStatus().toLowerCase().compareTo("completed") != 0)){
                    viewedTaskList.add(task);
                } else {
                    removeFromHistoryHash(taskID);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(viewedTaskList);
        saveHistoryHashToServer();
        return viewedTaskList;
    }

    public ArrayList<Task> getUsersTasks(){
        ArrayList<Task> taskList = new ArrayList<Task>();
        SQLQueryBuilder builder2 = new SQLQueryBuilder(Task.class);
        builder2.addColumns(new String[] {"requesterID"});
        builder2.addParameters(new String[] {viewUser.getObjectID()});

        MasterController.AsyncSearch asyncSearch2 =
                new MasterController.AsyncSearch(this, this);
        asyncSearch2.execute(new AsyncArgumentWrapper(builder2, Task.class));

        try {
            taskList = (ArrayList<Task>) asyncSearch2.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.i("size1----->", String.format("%d",taskList.size()));
        return taskList;
    }

    public void viewHistory(View view){ //TODO
        Intent intent = new Intent(ViewProfileActivity.this, MenuActivity.class);
        setTaskList(getViewedTasks()); //this is getting overwritten by something in onstart
        setViewMode(R.integer.MODE_INT_HISTORY);
        startActivity(intent);
    }

    public void viewTasks(View view){ //TODO
        Intent intent = new Intent(ViewProfileActivity.this, MenuActivity.class);
        /*
        if(getCurrentUser().getObjectID().compareTo(viewUser.getObjectID()) ==0){
            setViewMode(R.integer.MODE_INT_REQUESTER);
        } else {
            setTaskList(getUsersTasks());
            Log.i("size2----->", String.format("%d",getTaskList().size()));
            setViewMode(R.integer.MODE_INT_OTHERS_TASKS);
        }
        */
        setTaskList(getUsersTasks());
        Log.i("size2----->", String.format("%d",getTaskList().size()));
        setViewMode(R.integer.MODE_INT_OTHERS_TASKS);
        setLastViewedUser(viewUser);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPostExecute(GTData data) {
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
    }
}