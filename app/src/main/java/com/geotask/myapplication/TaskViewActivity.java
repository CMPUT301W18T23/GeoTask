package com.geotask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


//https://stackoverflow.com/questions/4127725/how-can-i-remove-a-button-or-make-it-invisible-in-android
public class TaskViewActivity extends AppCompatActivity  implements AsyncCallBackManager {
    private TextView title;
    private TextView name;
    private TextView description;
    private TextView status;
    private Task viewTask;
    private User currentUser;
    private User remote;
    private String currentuserId;
    private String taskUserId;
    private Button editTaskButton;
    private Button bidButton;
    private Button addBid;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
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
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        this.taskUserId = viewTask.getRequesterID();
        this.title = (TextView)findViewById(R.id.textViewTitle);
        this.name = (TextView)findViewById(R.id.textViewName);
        this.description = (TextView)findViewById(R.id.textViewDescription);
        this.status = (TextView)findViewById(R.id.textViewStatus);
        this.editTaskButton = (Button) findViewById(R.id.editTaskButton);
        this.bidButton = (Button) findViewById(R.id.bidsButton);
        this.addBid = (Button) findViewById(R.id.addBidButton);




        update();
        setupButtons();
        getTaskUser();
        profile();

        if (!currentuserId.equals(taskUserId)){   //hide editbutton if not user
            View b = findViewById(R.id.editTaskButton);
//            b.setVisibility(View.GONE);
            this.editTaskButton.setVisibility(b.GONE);
        }

        if (currentuserId.equals(taskUserId)){  //
            View b = findViewById(R.id.addBidButton);
            this.addBid.setVisibility(b.GONE);
        }
    }


    private void setupButtons(){
        this.editTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, EditTaskActivity.class);
                intent.putExtra("Task", viewTask);
                intent.putExtra("currentUser", currentUser);
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

        this.addBid.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                triggerPopup(v);
            }
        });

    }

    private void getTaskUser(){  //this should work when get really data to get
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(taskUserId, User.class));

        remote = null;
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

    public void triggerPopup(View view){

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.accept_bid_popout, null);

        POPUP_WINDOW_DELETION = new PopupWindow(this);
        POPUP_WINDOW_DELETION.setContentView(layout);
        POPUP_WINDOW_DELETION.setFocusable(true);
        POPUP_WINDOW_DELETION.setBackgroundDrawable(null);
        POPUP_WINDOW_DELETION.showAtLocation(layout, Gravity.CENTER, 1, 1);

        final EditText value = (EditText) layout.findViewById(R.id.editTextAmmount);

        Button cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
            }
        });

        Button acceptBtn = (Button) layout.findViewById(R.id.btn_accept_bid);
        acceptBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DELETION.dismiss();
//                String test = value.getText().toString();
//                System.out.print(test);
                Double number = Double.parseDouble(value.getText().toString());
                addBid(number);
                //TODO - go back to previous intent
            }
        });

    }

    private void addBid(Double value){
    Bid bid = new Bid(currentUser.getObjectID(), value, viewTask.getObjectID());
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
//    taskBidded(); //need to uncomment when taskId is given
    }

    private void taskBidded(){  //this should hopefully work when get really data to get
        viewTask.setStatus("Bidded");
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(viewTask);

        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }

    }




    private void profile(){  //need to wait for viewProfile activity to enable. this has not been tested because of that
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskViewActivity.this, ViewProfile.class);
                intent.putExtra("user", remote);
                startActivity(intent);
            }
        });
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
