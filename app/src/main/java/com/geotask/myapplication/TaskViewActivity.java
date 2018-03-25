package com.geotask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * handles veiwing a task
 * bids
 * adding bids
 * going to profile if username is clicked on
 */
//https://stackoverflow.com/questions/4127725/how-can-i-remove-a-button-or-make-it-invisible-in-android
public class TaskViewActivity extends AbstractGeoTaskActivity  implements AsyncCallBackManager {
    private TextView title;
    private TextView name;
    private TextView description;
    private TextView status;
    private TextView hitCount;
    private TextView dateSincePost;
    private Button editTaskButton;
    private Button bidButton;
    private Button addBidButton;
    private Button doneButton;
    private ImageView starIcon;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
    private PopupWindow POPUP_WINDOW_DONE = null;   //popup for error message
    private User userBeingViewed;

    /**
     * inits vars and view items, and button
     * also gets current Task, Current User, and the USer of the Task currently viewed
     * it hides the edit button if current user != the tasks User
     * it hides the add bit button if this is the requestes task
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("LifeCycle --->", "in viewtask");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_view);

        title = findViewById(R.id.textViewTitle);
        name = findViewById(R.id.textViewName);
        description = findViewById(R.id.textViewDescription);
        status = findViewById(R.id.status_header);
        hitCount = findViewById(R.id.num_views);
        dateSincePost = findViewById(R.id.textViewDate);

        editTaskButton = findViewById(R.id.editTaskButton);
        bidButton = findViewById(R.id.bidsButton);
        addBidButton = findViewById(R.id.addBidButton);
        doneButton = findViewById(R.id.doneButton);

        updateDisplayedValues();
        setupButtons();
        getTaskUser();

        starIcon = (ImageView) findViewById(R.id.btn_star_task_view);

        starIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("--------->", "clicked it");
                if (getCurrentUser().starred(getCurrentTask().getObjectID())) {
                    getCurrentUser().removeTaskFromStarredList(getCurrentTask().getObjectID());
                    starIcon.setImageResource(R.drawable.ic_star_outline_grey600_24dp);
                } else {
                    getCurrentUser().addTaskToStarredList(getCurrentTask().getObjectID());
                    starIcon.setImageResource(R.drawable.ic_star_grey600_24dp);
                }
                MasterController.AsyncUpdateDocument asyncUpdateDocument =
                        new MasterController.AsyncUpdateDocument();
                asyncUpdateDocument.execute(getCurrentUser());
            }
        });

        if (getCurrentUser().getObjectID().equals(getCurrentTask().getRequesterID())){   //hide editbutton if not user
            editTaskButton.setVisibility(View.VISIBLE);
            addBidButton.setVisibility(View.INVISIBLE);
            starIcon.setVisibility(View.INVISIBLE);
            if (!"Accepted".equals(getCurrentTask().getStatus())||!"Completed".equals(getCurrentTask().getStatus())){
                doneButton.setVisibility(View.INVISIBLE);   //if status is not accepted  hide button

            }
        } else {
            editTaskButton.setVisibility(View.INVISIBLE);
            addBidButton.setVisibility(View.VISIBLE);
            starIcon.setVisibility(View.VISIBLE);
            doneButton.setVisibility(View.INVISIBLE);   // if not user hide done button


                //Increasing Hits
            Log.i("cur ------>", getCurrentTask().getObjectID());
            Log.i("cur ------>", getCurrentUser().getName());
            if(!getCurrentUser().visited(getCurrentTask().getObjectID())) {
                getCurrentTask().addHit();
                MasterController.AsyncUpdateDocument asyncUpdateDocument =
                        new MasterController.AsyncUpdateDocument();
                asyncUpdateDocument.execute(getCurrentTask());
            }
            MasterController.AsyncUpdateDocument asyncUpdateDocument =
                    new MasterController.AsyncUpdateDocument();
            asyncUpdateDocument.execute(getCurrentUser());
        }
        name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }


    /**
     * sets up buttons (cleaner than in the one methood
     */
    private void setupButtons(){
        this.editTaskButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, EditTaskActivity.class);
                startActivityForResult(intent,1);
//                startActivity(intent);
            }
        });

        this.bidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(TaskViewActivity.this, ViewBidsActivity.class);
                startActivityForResult(intent, 2);
                updateStatus();  //for later ToDo ?????
            }
        });

        this.addBidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                triggerPopup(v);
            }
        });

        this.doneButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                triggerDone(v);
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
        asyncGetDocument.execute(new AsyncArgumentWrapper(getCurrentTask().getRequesterID(), User.class));
    }

    /**
     * updates edittext for the title, description and status
     * these are local changes
     */
    private void updateDisplayedValues(){
        this.title.setText(getCurrentTask().getName());
//        this.name.setText(taskUserId); //need to change to get user from the id
//        this.name.setText("placeolder");
        String e = getCurrentTask().getStatus();
        System.out.print(e);
        this.description.setText(getCurrentTask().getDescription());
        this.status.setText(String.format("Status: %s", StringUtils.capitalize(getCurrentTask().getStatus())));
        this.hitCount.setText(String.format("%d Views",getCurrentTask().getHitCounter()));
        int days = (int) (new Date().getTime() - getCurrentTask().getDate()) / (1000*60*60*24);
        int hours   = (int) ((new Date().getTime() - getCurrentTask().getDate()) / (1000*60*60) % 24);
        int mins = (int)((new Date().getTime() - getCurrentTask().getDate()) / (1000*60) % 60);
        int secs = (int) ((new Date().getTime() - getCurrentTask().getDate()) / 1000) % 60 ;
        if(days > 0){
            this.dateSincePost.setText(String.format("Posted %d days ago", days));
        } else if(hours > 0){
            this.dateSincePost.setText(String.format("Posted %d hours ago", hours));
        } else if (mins > 0){
            this.dateSincePost.setText(String.format("Posted %d minutes ago", mins));
        } else {
            this.dateSincePost.setText(String.format("Posted %d seconds ago", secs));
        }
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
                if ("1".equals(data.getStringExtra("del"))){
                    finish();
                }
                updateDisplayedValues();
            }
        }else if (requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                updateDisplayedValues();
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

                if (!value.getText().toString().isEmpty()) {
                    POPUP_WINDOW_DELETION.dismiss();
                    Double number = Double.parseDouble(value.getText().toString());
                    addBid(number);

                }
                //TODO - go back to previous intent
            }
        });

    }

    public void triggerDone(View view){

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.task_completed_popup, null);

        POPUP_WINDOW_DONE = new PopupWindow(this);
        POPUP_WINDOW_DONE.setContentView(layout);
        POPUP_WINDOW_DONE.setFocusable(true);
        POPUP_WINDOW_DONE.setBackgroundDrawable(null);
        POPUP_WINDOW_DONE.showAtLocation(layout, Gravity.CENTER, 1, 1);

        Button cancelBtn = (Button) layout.findViewById(R.id.btn_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DONE.dismiss();
            }
        });

        Button acceptBtn = (Button) layout.findViewById(R.id.btn_accept_done);
        acceptBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                POPUP_WINDOW_DONE.dismiss();
                Task newTask = getCurrentTask();
                newTask.setStatusCompleted();
                setCurrentTask(newTask);
                MasterController.AsyncUpdateDocument asyncUpdateDocument =
                        new MasterController.AsyncUpdateDocument();
                asyncUpdateDocument.execute(getCurrentTask());
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateDisplayedValues();
            }
        });

    }

    /**
     *  uses MasterController to add a new bid. value is passode over
      @throws InterruptedException

     * @param value
     */
    private void addBid(Double value){
        Bid bid = new Bid(getCurrentUser().getObjectID(), value, getCurrentTask().getObjectID());

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        if (getCurrentTask().getStatus().toLowerCase().equals("requested")) {
            taskBidded(); //need to uncomment when taskId is given
            updateDisplayedValues();
        }

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(getCurrentTask());
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    /**
     * updates tasks status to bided
     * @throws InterruptedException
     */
    private void taskBidded() {  //this should hopefully work when get really data to get
        getCurrentTask().setStatus(getString(R.string.TASK_STATUS_BIDDED));
    }


    /**
     * sets up clickavle viewtext to go to User profile
     * @see ViewProfileActivity
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
        asyncGetDocumentWhenDocumentExist.execute(new AsyncArgumentWrapper(getCurrentTask().getObjectID(), Task.class));

        try {
            setCurrentTask((Task) asyncGetDocumentWhenDocumentExist.get());
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
            this.name.setText(String.format("Requested by %s", userBeingViewed.getName()));
        } else if (data instanceof Task){
            //ToDo ?????
        }
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
    }


}
