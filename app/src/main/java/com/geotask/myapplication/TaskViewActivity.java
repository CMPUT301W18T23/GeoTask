package com.geotask.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.geotask.myapplication.DataClasses.Task;


//https://stackoverflow.com/questions/4127725/how-can-i-remove-a-button-or-make-it-invisible-in-android
public class TaskViewActivity extends AppCompatActivity {
    private TextView title;
    private TextView name;
    private TextView description;
    private TextView status;
    private Task viewTask;
    private String currentuserId;
    private String taskUserId;
    private Button editTaskButton;



//    private ArrayList<String> bidArray;  //Array of bid Objects
//    private ArrayAdapter<String> adapter; //Adapter for bidView
//    private ListView bidList; //named taskListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);


        Intent intent = getIntent();
        this.viewTask = (Task)intent.getSerializableExtra("task");
        this.currentuserId = (String)intent.getSerializableExtra("ID");
        this.taskUserId = viewTask.getRequesterID();
        this.title = (TextView)findViewById(R.id.textViewTitle);
        this.name = (TextView)findViewById(R.id.textViewName);
        this.description = (TextView)findViewById(R.id.textViewDescription);
        this.status = (TextView)findViewById(R.id.textViewStatus);
        this.editTaskButton = (Button) findViewById(R.id.editTaskButton);

//        this.bidList = (ListView) findViewById(R.id.taskListView);
//        this.bidArray = viewTask.getBidList();
//
//        this.adapter = new BidArrayAdapter(this, R.layout.activity_task_view, bidArray);
//        bidList.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
        update();


        this.editTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, EditTaskActivity.class);
                intent.putExtra("Task", viewTask);
                startActivityForResult(intent,1);
//                startActivity(intent);
            }
        });
        if (currentuserId != taskUserId){
            View b = findViewById(R.id.editTaskButton);
            b.setVisibility(View.GONE);
//            this.editTaskButton.setVisibility(View.GONE);
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













}
