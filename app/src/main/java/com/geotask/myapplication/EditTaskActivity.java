package com.geotask.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * handles editing a task by the task requester
 */
public class EditTaskActivity extends AppCompatActivity implements AsyncCallBackManager {
    private EditText editTitle;
    private EditText editDescription;
    private Task taskBeingEdited;
    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    private User currentUser;


    /**
     * inits vars and view items, and buttons

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        taskBeingEdited = (Task)getIntent().getSerializableExtra(getString(R.string.CURRENT_TASK_BEING_VIEWED));

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);

        editTitle.setText(taskBeingEdited.getName(), TextView.BufferType.EDITABLE);
        editDescription.setText(taskBeingEdited.getDescription(),TextView.BufferType.EDITABLE);

        Button editButton = findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editData();
            }
        });

        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteData();
            }
        });
    }

    /**
     *handles data editing - called from editButton press
     * takes data from paramaters and sends to the update
     * returns udated task to update data
     * @see TaskViewActivity
     */
    private void editData() {
        String name =   editTitle.getText().toString();
        String description = editDescription.getText().toString();

        UserEntryStringValidator check = new UserEntryStringValidator();
        if (check.checkText(name, description)){
            taskBeingEdited.setName(name);
            taskBeingEdited.setDescription(description);

            updateTask();

            Intent back = new Intent();
            back.putExtra(getString(R.string.UPDATED_TASK_AFTER_EDIT), taskBeingEdited);
            back.putExtra(getString(R.string.CURRENT_USER), currentUser);
            setResult(Activity.RESULT_OK, back);
            finish();
        }else{
            Toast.makeText(this, getString(R.string.INVALID_TASK_DATA_WHEN_CREATING_NEW_TASK), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     *handles data deleting - called from deleteButton press
     * nothing returned and no result code returned
     * gets all bids for the task
     * deletes them 1 by 1
     * then deletes task
     * @see TaskViewActivity
     */
    private void deleteData() {

        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("taskID", taskBeingEdited.getObjectID());

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));
        ArrayList<Bid> bidList = new ArrayList<Bid>();
        try {
            bidList = (ArrayList<Bid>) asyncSearch.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (Bid bid : bidList) {
            MasterController.AsyncDeleteDocument asyncDeleteDocument =
                    new MasterController.AsyncDeleteDocument();
            asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
        }


        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(taskBeingEdited.getObjectID(), Task.class));

        finish();
    }


    /**
     *sends updated class to elasticsearch
     * @throws InterruptedException
     */
    private void updateTask(){  //this should hopefully work when get really data to get
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(taskBeingEdited);
    }

    @Override
    public void onPostExecute(GTData data) {
        this.data = data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        //bidList.clear();
        //bidList.addAll((Collection<? extends Bid>) dataList);
        //adapter.notifyDataSetChanged();
    }
}
