package com.geotask.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Photo;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * handles veiwing a task
 * bids
 * adding bids
 * going to profile if username is clicked on
 */
//https://stackoverflow.com/questions/4127725/how-can-i-remove-a-button-or-make-it-invisible-in-android
public class ViewTaskActivity extends AbstractGeoTaskActivity  implements AsyncCallBackManager {
    private TextView title;
    private TextView name;
    private TextView description;
    private TextView status;
    private TextView hitCount;
    private TextView dateSincePost;
    private Button bidButton;
    private Button addBidButton;
    private Button doneButton;
    private Button notCompleteButton;
    private Button viewphoto;
    private PopupWindow POPUP_WINDOW_DELETION = null;   //popup for error message
    private PopupWindow POPUP_WINDOW_DONE = null;   //popup for error message
    private User userBeingViewed;
    private Toolbar toolbar;
    private MenuItem editBtn;
    private MenuItem starBtn;
    private MenuItem deleteBtn;
    private ArrayList<Bid> bidList;
    private Photo currentPhoto;
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
        //setContentView(R.layout.activity_task_view);
        setContentView(R.layout.app_bar_menu_view_task);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(networkIsAvailable()) {
            if(!syncTaskAndBidData()){
                onBackPressed();
            }
        }


//        Bundle settings = new Bundle();
//        settings.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//        settings.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
//        ContentResolver.requestSync(getAccount(), getString(R.string.SYNC_AUTHORITY), settings);

        title = findViewById(R.id.textViewTitle);
        name = findViewById(R.id.textViewName);
        description = findViewById(R.id.textViewDescription);
        status = findViewById(R.id.status_header);
        hitCount = findViewById(R.id.num_views);
        dateSincePost = findViewById(R.id.textViewDate);

        bidButton = findViewById(R.id.bidsButton);
        addBidButton = findViewById(R.id.addBidButton);
        doneButton = findViewById(R.id.doneButton);
        notCompleteButton = findViewById(R.id.notCompleteButton);
        viewphoto = findViewById(R.id.viewPhoto);

        updateDisplayedValues();
        setupButtons();
        getTaskUser();
        try {
            getPhotolist();
            //System.out.println(photolist.get(0).toString());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (getCurrentUser().getObjectID().equals(getCurrentTask().getRequesterID())){   //hide editbutton if not user
            viewphoto.setVisibility(View.GONE);
            addBidButton.setVisibility(View.INVISIBLE);
            System.out.print("ye");
            if ("Bidded".equals(getCurrentTask().getStatus())||"Requested".equals(getCurrentTask().getStatus())||"Completed".equals(getCurrentTask().getStatus())){
                doneButton.setVisibility(View.INVISIBLE);   //if status is not accepted  hide button
                notCompleteButton.setVisibility(View.INVISIBLE);   //if status is not accepted  hide button

            }
        } else {
            viewphoto.setVisibility(View.VISIBLE);
            if ("Bidded".equals(getCurrentTask().getStatus())||"Requested".equals(getCurrentTask().getStatus())) {
                addBidButton.setVisibility(View.VISIBLE);

            } else {
                addBidButton.setVisibility(View.INVISIBLE);
            }
            doneButton.setVisibility(View.INVISIBLE);   // if not user hide done button
            notCompleteButton.setVisibility(View.INVISIBLE);   // if not user hide done button


            //Increasing Hits
            Log.i("cur ------>", getCurrentTask().getObjectID());
            Log.i("cur ------>", getCurrentUser().getName());

            if(!userViewed(getCurrentTask().getObjectID())){
                getCurrentTask().addHit();
                MasterController.AsyncUpdateDocument asyncUpdateDocument =
                        new MasterController.AsyncUpdateDocument(this);
                asyncUpdateDocument.execute(getCurrentTask());

                saveHistoryHashToServer();
            }
            MasterController.AsyncUpdateDocument asyncUpdateDocument =
                    new MasterController.AsyncUpdateDocument(this);
            asyncUpdateDocument.execute(getCurrentUser());
        }
        if ("Completed".equals(getCurrentTask().getStatus())){
            addBidButton.setVisibility(View.INVISIBLE);

        }
        name.setPaintFlags(name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        addBidButton.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateDisplayedValues();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_task, menu);
        editBtn = toolbar.getMenu().findItem(R.id.action_edit);
        starBtn = toolbar.getMenu().findItem(R.id.action_star);
        deleteBtn = toolbar.getMenu().findItem(R.id.action_delete);
        deleteBtn.setEnabled(true);
        if((getCurrentUser().getObjectID().compareTo(getCurrentTask().getRequesterID()) == 0)
                && (getCurrentTask().getStatus().toLowerCase().compareTo("requested") == 0)) {
            editBtn.setVisible(true);
            deleteBtn.setVisible(true);
        } else if ((getCurrentUser().getObjectID().compareTo(getCurrentTask().getRequesterID()) == 0)
                && (getCurrentTask().getStatus().toLowerCase().compareTo("completed") == 0)){
            deleteBtn.setVisible(false);
            editBtn.setVisible(false);
        } else if (getCurrentUser().getObjectID().compareTo(getCurrentTask().getRequesterID()) == 0){
            deleteBtn.setVisible(true);
            editBtn.setVisible(false);
        } else {
            editBtn.setVisible(false);
            deleteBtn.setVisible(false);
        }
        if((getCurrentUser().getObjectID().compareTo(getCurrentTask().getRequesterID()) != 0)
                && (getCurrentTask().getStatus().toLowerCase().compareTo("accepted") != 0)
                && (getCurrentTask().getStatus().toLowerCase().compareTo("completed") != 0)){
            //if (getCurrentUser().starred(getCurrentTask().getObjectID())) {
            if(userStarred(getCurrentTask().getObjectID())){
                starBtn.setIcon(R.drawable.ic_star_yellow_24dp);
            } else {
                starBtn.setIcon(R.drawable.ic_star_outline_white_24dp);
            }
            starBtn.setVisible(true);
        } else {
            starBtn.setVisible(false);
        }
        if(!networkIsAvailable()){
            editBtn.setVisible(false);
            deleteBtn.setVisible(false);
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
            Intent intent = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_star) {
            if(userStarred(getCurrentTask().getObjectID())){
                starBtn.setIcon(R.drawable.ic_star_outline_white_24dp);
                toggleStar(getCurrentTask().getObjectID());
            } else {
                starBtn.setIcon(R.drawable.ic_star_yellow_24dp);
                toggleStar(getCurrentTask().getObjectID());
            }
            saveStarHashToServer();
            return true;
        } else if (id == R.id.action_delete){
            String taskStatus = getCurrentTask().getStatus();
            if(taskStatus.compareTo("Accepted") != 0 && taskStatus.compareTo("Completed") != 0) {
                deleteBtn.setEnabled(false);
                deleteData();
                Intent intent = new Intent(ViewTaskActivity.this, MenuActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this,
                        R.string.CANT_DELETE_TASK,
                        Toast.LENGTH_LONG)
                        .show();
            }

        }
        return super.onOptionsItemSelected(item);
    }


    private Boolean syncTaskAndBidData(){
        MasterController.AsyncGetDocumentNewest asyncGetDocument =
                new MasterController.AsyncGetDocumentNewest(this, this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(getCurrentTask().getObjectID(), Bid.class));
        Task task = null;
        try {
            task = (Task) asyncGetDocument.get();
            setCurrentTask(task);

            if(task == null){
                return false;
            } else {
                SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
                builder.addColumns(new String[] {"taskId"});
                builder.addParameters(new String[] {task.getObjectID()});

                SuperBooleanBuilder superBuilder = new SuperBooleanBuilder();
                superBuilder.put("taskId", task.getObjectID());

                MasterController.AsyncSearch asyncSearch =
                        new MasterController.AsyncSearch(this, this);
                asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

                ArrayList<Bid> updatedBidList = (ArrayList<Bid>) MasterController.slowSearch(new AsyncArgumentWrapper(superBuilder.toString(), Bid.class));
                ArrayList<Bid> oldBidList = (ArrayList<Bid>) asyncSearch.get();

                /*
                    insert the new bids into the local database
                 */
                for(Bid bid : updatedBidList){
                    if(!oldBidList.contains(bid)){
                        MasterController.AsyncCreateNewLocalDocument asyncCreateNewLocalDocument =
                                new MasterController.AsyncCreateNewLocalDocument(this);
                        asyncCreateNewLocalDocument.execute(bid);
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     *handles data deleting - called from deleteButton press
     * nothing returned and no result code returned
     * gets all bids for the task
     * deletes them 1 by 1
     * then deletes task
     * @see ViewTaskActivity
     */
    private void deleteData() {

        SQLQueryBuilder builder = new SQLQueryBuilder(Task.class);
        builder.addColumns(new String[]{"taskId"});
        builder.addParameters(new String[] {getCurrentTask().getObjectID()});

        MasterController.AsyncDeleteDocument asyncDeleteTask =
                new MasterController.AsyncDeleteDocument(this);
        asyncDeleteTask.execute(new AsyncArgumentWrapper(getCurrentTask().getObjectID(), Task.class));
        setCurrentTask(null);

        MasterController.AsyncDeleteBidsByTaskID asyncDeleteDocumentByQuery =
                new MasterController.AsyncDeleteBidsByTaskID(this);
        asyncDeleteDocumentByQuery.execute(new AsyncArgumentWrapper(builder, Bid.class));

        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * sets up buttons (cleaner than in the one methood
     */
    private void setupButtons(){
        this.viewphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if (currentPhoto.photolistbyte.size() != 0){
                Intent intent = new Intent(ViewTaskActivity.this, SelectPhotoActivity.class);
                intent.putExtra("type","view");
                intent.putExtra(getString(R.string.PHOTO_LIST_SIZE), currentPhoto.photolistbyte.size());
                //System.out.println("1234567890"+currentPhoto.photolistbyte.size());
                for (int i = 0; i < currentPhoto.photolistbyte.size(); i++) {
                    intent.putExtra("list" + i, currentPhoto.photolistbyte.get(i));
                }
                startActivity(intent);
            }else{
                Toast.makeText(ViewTaskActivity.this,"There are no photos",Toast.LENGTH_LONG).show();
            }
            }
        });

        this.bidButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(ViewTaskActivity.this, ViewBidsActivity.class);
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
            if(networkIsAvailable()) {
                triggerDone(v);
            } else {
                Toast.makeText(getBaseContext(),
                        R.string.CANNOT_COMPLETE_TASK_OFFLINE,
                        Toast.LENGTH_LONG)
                        .show();
            }
            }
        });

        this.notCompleteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
            if(networkIsAvailable()) {
                triggerNotComplete(v);
            } else {
                Toast.makeText(getBaseContext(),
                        R.string.CANNOT_DECLINE_ACCEPTED_OFFLINE,
                        Toast.LENGTH_LONG)
                        .show();
            }
            }
        });

    }

    private void getPhotolist() throws ExecutionException, InterruptedException {
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this,this);
        Log.i("checkout_currnettask", getCurrentTask().toString()+getCurrentTask().getPhotoList());
        asyncGetDocument.execute(new AsyncArgumentWrapper(getCurrentTask().getPhotoList(),Photo.class));
        currentPhoto = (Photo) asyncGetDocument.get();
    }

    /**
     * gets the User of the task
     * if no user is found, unknown is shown
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void getTaskUser(){  //this should work when get really data to get
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, this);
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

    /**
     * On click actions for the not complete button
     * @param view
     * not_completeD_popout xml file for popout
     */
    public void triggerNotComplete(View view){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.not_completed_popout, null);

        final Task curTask = getCurrentTask();

        //get accepted bid and delete it
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
                notComplete();
            }
        });
    }

    /**
     * Delete accepted bid and update task status
     */
    public void notComplete() {
        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument(this);
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(getCurrentTask().getAcceptedBidID(), Bid.class));

        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskId"});
        builder.addParameters(new String[] {getCurrentTask().getObjectID()});

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this, this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        try {
            bidList = (ArrayList<Bid>) asyncSearch.get();
            if(bidList != null) {
                Collections.sort(bidList);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        getCurrentTask().setAcceptedBid(-1.0);
        if(bidList.size() <= 0) {
            getCurrentTask().setStatus("Requested");
        } else if(bidList.size() > 0) {
            getCurrentTask().setStatus("Bidded");
        }

        MasterController.AsyncUpdateDocument asyncUpdateDocument
                = new MasterController.AsyncUpdateDocument(this);
        asyncUpdateDocument.execute(getCurrentTask());

        updateTaskMetaData(this);
        updateDisplayedValues();
        MenuActivity.setLastClicked(getCurrentTask());
        Intent intent = new Intent(getBaseContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    /**
     * On click actions for the done button
     * @param view
     * task_completed_popup xml file for popout
     */
    public void triggerDone(View view){
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.task_completed_popup, null);

        /*
            Grabbing the accepted user in case we need to increment their completed tasks
         */
        User acceptedUser = null;
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(getCurrentTask().getAcceptedProviderID(), User.class));

        try {
            acceptedUser = (User) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
        final User finalAcceptedUser = acceptedUser;
        acceptBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
            /*
                increment the user's completed tasks
             */
                finalAcceptedUser.incrementCompletedTasks();
                MasterController.AsyncUpdateDocument asyncUpdateDocument =
                        new MasterController.AsyncUpdateDocument(getBaseContext());
                asyncUpdateDocument.execute(finalAcceptedUser);

                POPUP_WINDOW_DONE.dismiss();
                Task newTask = getCurrentTask();
                newTask.setStatusCompleted();
                setCurrentTask(newTask);
                MasterController.AsyncUpdateDocument asyncUpdateDocument2 =
                        new MasterController.AsyncUpdateDocument(getBaseContext());
                asyncUpdateDocument2.execute(getCurrentTask());
                try {
                    asyncUpdateDocument.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
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
                new MasterController.AsyncCreateNewDocument(this);
        asyncCreateNewDocument.execute(bid);

        if (getCurrentTask().getStatus().toLowerCase().equals("requested")) {
            taskBidded(); //need to uncomment when taskId is given
            updateDisplayedValues();
        }

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument(this);
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
                Intent intent = new Intent(ViewTaskActivity.this, ViewProfileActivity.class);
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
                new MasterController.AsyncGetDocument(this, this);
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
    public void updateNotifications(){
        String e = getCurrentUser().getObjectID();
        String x = getCurrentTask().getRequesterID();
        if (getCurrentUser().getObjectID().equals(getCurrentTask().getRequesterID())){
            HashSet<String> bidList = new HashSet<>();
            Task t = getCurrentTask();
            t.setBidList(bidList);
            MasterController.AsyncUpdateDocument asyncUpdateDocument =
                    new MasterController.AsyncUpdateDocument(this);
            asyncUpdateDocument.execute(t);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateNotifications();
    }
    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
    }


    @Override
    public void onBackPressed() {
        MenuActivity.setLastClicked(getCurrentTask());
        Intent intent = new Intent(getBaseContext(), MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }



}