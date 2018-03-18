package com.geotask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

/**
 * handles veiwing a task
 * bids
 * adding bids
 * going to profile if username is clicked on
 */
//https://stackoverflow.com/questions/4127725/how-can-i-remove-a-button-or-make-it-invisible-in-android
public class TaskViewActivity extends AppCompatActivity  implements AsyncCallBackManager {
    private TextView title;
    private TextView name;
    private TextView description;
    private TextView status;
    private Task currentTask;
    private User currentUser;
    private Button editTaskButton;
    private Button bidButton;
    private Button addBidButton;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
    private User userBeingViewed;


    /**
     * inits vars and view items, and button
     * also gets current Task, Current User, and the USer of the Task currently viewed
     * it hides the edit button if current user != the tasks User
     * it hides the add bit button if this is the requestes task
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);


        currentTask = (Task) getIntent().getSerializableExtra(getString(R.string.TASK_BEING_VIEWED));
        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER));

        String currentUserId = currentUser.getObjectID();

        title = findViewById(R.id.textViewTitle);
        name = findViewById(R.id.textViewName);
        description = findViewById(R.id.textViewDescription);
        status = findViewById(R.id.textViewStatus);

        editTaskButton = findViewById(R.id.editTaskButton);
        bidButton = findViewById(R.id.bidsButton);
        addBidButton = findViewById(R.id.addBidButton);

        updateDisplayedValues();
        setupButtons();
        getTaskUser();

        if (currentUserId.equals(currentTask.getRequesterID())){   //hide editbutton if not user
            editTaskButton.setVisibility(View.VISIBLE);
            addBidButton.setVisibility(View.INVISIBLE);
        } else {
            editTaskButton.setVisibility(View.INVISIBLE);
            addBidButton.setVisibility(View.VISIBLE);
        }
    }


    /**
     * sets up buttons (cleaner than in the one methood
     */
    private void setupButtons(){
        this.editTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, EditTaskActivity.class);
                intent.putExtra(getString(R.string.CURRENT_TASK_BEING_VIEWED), currentTask);
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivityForResult(intent,1);
//                startActivity(intent);
            }
        });

        this.bidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, ViewBidsActivity.class);
                intent.putExtra(getString(R.string.CURRENT_TASK_BEING_VIEWED), currentTask);
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivity(intent);
                updateStatus();  //for later ToDo ?????
            }
        });

        this.addBidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                triggerPopup(v);
            }
        });

    }

    /**
     * gets the User of the task
     * if no user is found, unknown is shown
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void getTaskUser(){  //this should work when get really data to get
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(currentTask.getRequesterID(), User.class));
    }

    /**
     * updates edittext for the title, description and status
     * these are local changes
     */
    private void updateDisplayedValues(){
        this.title.setText(currentTask.getName());
//        this.name.setText(taskUserId); //need to change to get user from the id
//        this.name.setText("placeolder");
        this.description.setText(currentTask.getDescription());
        this.status.setText(currentTask.getStatus());
    }


    /**
     * handles return from editTaskACtivity
     * if it was edit, it updates locally the data.
     * if deleted it returns to MenueActivity
     * @see EditTaskActivity
     * @see MenuActivity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){  //handles return values from addSub and SubDetails
        if(requestCode==1){  //update task
            if (resultCode == Activity.RESULT_OK) {
                this.currentTask = (Task) data.getSerializableExtra(getString(R.string.UPDATED_TASK_AFTER_EDIT));  //need to return that in implementation
                updateDisplayedValues();
            }else{
                finish();
            }
        }
    }

    /**
     * sets  up popup for bid
     * accept_bid_popout xml file for popout
     * @param view
     */
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

    /**
     *  uses MasterController to add a new bid. value is passode over
      @throws InterruptedException

     * @param value
     */
    private void addBid(Double value){
        Bid bid = new Bid(currentUser.getObjectID(), value, currentTask.getObjectID());

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        if (currentTask.getStatus() != getString(R.string.TASK_STATUS_BIDDED)) {
            taskBidded(); //need to uncomment when taskId is given
            updateDisplayedValues();
        }
    }
    /**
     * updates tasks status to bided
     * @throws InterruptedException
     */
    private void taskBidded(){  //this should hopefully work when get really data to get
        currentTask.setStatus(getString(R.string.TASK_STATUS_BIDDED));
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(currentTask);
    }


    /**
     * sets up clickavle viewtext to go to User profile
     * @see ViewProfile
     */
    private void profile(){  //need to wait for viewProfile activity to enable. this has not been tested because of that
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskViewActivity.this, ViewProfileActivity.class);
                intent.putExtra(getString(R.string.VIEW_USER), userBeingViewed);
                startActivity(intent);
            }
        });
    }


    /**
     *  gets new status in case it has changed and not updated locally
     *  @throws InterruptedException
     *  @throws ExecutionException
     */
    private void updateStatus(){
        MasterController.AsyncGetDocument asyncGetDocumentWhenDocumentExist =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocumentWhenDocumentExist.execute(new AsyncArgumentWrapper(currentTask.getObjectID(), Task.class));

        try {
            currentTask = (Task) asyncGetDocumentWhenDocumentExist.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    updateDisplayedValues();
    }

    @Override
    public void onPostExecute(GTData data) {
        if(data instanceof User) {
            userBeingViewed = (User) data;
            profile();
            this.name.setText(userBeingViewed.getName());
        } else if (data instanceof Task){
            //ToDo ?????
        }
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
    }


}
