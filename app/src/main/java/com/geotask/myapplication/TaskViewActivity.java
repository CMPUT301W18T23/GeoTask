package com.geotask.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;
import java.util.concurrent.ExecutionException;


//https://stackoverflow.com/questions/4127725/how-can-i-remove-a-button-or-make-it-invisible-in-android
public class TaskViewActivity extends AppCompatActivity  implements AsyncCallBackManager {
    private TextView title;
    private TextView name;
    private TextView description;
    private TextView status;
    private Task viewTask;
    private User currentUser;
    private String currentuserId;
    private String taskUserId;
    private Button editTaskButton;
    private Button bidButton;
    private GTData data = null;
    private List<? extends GTData> searchResult = null;


//    private ArrayList<String> bidArray;  //Array of bid Objects
//    private ArrayAdapter<String> adapter; //Adapter for bidView
//    private ListView bidList; //named taskListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);


        Intent intent = getIntent();
        this.viewTask = (Task)intent.getSerializableExtra("task");
        this.currentUser = (User)intent.getSerializableExtra("Id");
        this.currentuserId = currentUser.getObjectID(); //get specific ID

        this.taskUserId = viewTask.getRequesterID();
        this.title = (TextView)findViewById(R.id.textViewTitle);
        this.name = (TextView)findViewById(R.id.textViewName);
        this.description = (TextView)findViewById(R.id.textViewDescription);
        this.status = (TextView)findViewById(R.id.textViewStatus);
        this.editTaskButton = (Button) findViewById(R.id.editTaskButton);
        this.bidButton = (Button) findViewById(R.id.bidsButton);


        update();
        setupButtons();
        getTaskUser();
        if (currentuserId != taskUserId){
            View b = findViewById(R.id.editTaskButton);
//            b.setVisibility(View.GONE);
            this.editTaskButton.setVisibility(b.GONE);
        }
    }


    private void setupButtons(){
        this.editTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, EditTaskActivity.class);
                intent.putExtra("Task", viewTask);
                startActivityForResult(intent,1);
//                startActivity(intent);
            }
        });

        this.bidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, ViewBidsActivity.class);
                intent.putExtra("task", viewTask);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

    }

    private void getTaskUser(){  //this should work when get really data to get
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(taskUserId, User.class));

        User remote = null;
        try {
            remote = (User) asyncGetDocument.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (remote == null){
            this.name.setText("Unknown");
        }else{
            this.name.setText(remote.getName());
        }
    }
    private void update(){
        this.title.setText(viewTask.getName());
//        this.name.setText(taskUserId); //need to change to get user from the id
        this.name.setText("placeolder");

        this.description.setText(viewTask.getDescription());
        this.status.setText(viewTask.getStatus());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){  //handles return values from addSub and SubDetails
        if(requestCode==1){  //update task
            if (resultCode == Activity.RESULT_OK) {
                this.viewTask = (Task) data.getSerializableExtra("updatedTask");  //need to return that in implementation
                update();
            }else{
                finish();
            }
        }
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
