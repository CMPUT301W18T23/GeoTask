package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.ArrayList;
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

    /**
     * sets up vars from intent to view user data
     * can be called from viewBidsActivity
     * can be called from TaskViewActivity
     *
     * @see ViewBidsActivity
     * @see TaskViewActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        Intent intent = getIntent();
        this.viewUser = (User) intent.getSerializableExtra(getString(R.string.VIEW_USER));
        this.name = findViewById(R.id.profileName);
        this.phone = findViewById(R.id.profilePhone);
        this.email = findViewById(R.id.profileEmail);
        this.date = findViewById(R.id.joinDate);
        this.profilePic = findViewById(R.id.profile_pic);
        this.historyBtn = findViewById(R.id.btn_history);
        this.completed = findViewById(R.id.num_completed);

        this.name.setText(viewUser.getName());
        this.phone.setText(viewUser.getPhonenum());
        this.email.setText(viewUser.getEmail());
        this.date.setText("Joined on " + viewUser.getDateString());
        this.completed.setText(String.format("%d", viewUser.getCompletedTasks()));

        this.historyBtn.setVisibility(View.INVISIBLE);

        if(getCurrentUser().getObjectID().compareTo(viewUser.getObjectID()) ==0){
            //TODO make edit button visible
            this.historyBtn.setVisibility(View.VISIBLE);
        }
    }

    public ArrayList<Task> getViewedTasks(){
        ArrayList<Task> viewedTaskList = new ArrayList<Task>();
        for(String taskID : getCurrentUser().getHistoryList()){
            MasterController.AsyncGetDocument asyncGetDocument =
                    new MasterController.AsyncGetDocument(this);
            asyncGetDocument.execute(new AsyncArgumentWrapper(taskID, Task.class));

            Task task = null;
            try {
                task = (Task) asyncGetDocument.get();
                viewedTaskList.add(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return viewedTaskList;
    }

    public void viewHistory(View view){ //TODO
        Intent intent = new Intent(ViewProfileActivity.this, MenuActivity.class);
        setTaskList(getViewedTasks()); //this is getting overwritten by something in onstart
        startActivity(intent);
    }

    public void viewTasks(View view){ //TODO
        Intent intent = new Intent(ViewProfileActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPostExecute(GTData data) {
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
    }
}