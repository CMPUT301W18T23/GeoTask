package com.geotask.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.List;

/**
 * handles editing a task by the task requester
 */
public class EditTaskActivity extends AbstractGeoTaskActivity implements AsyncCallBackManager {
    private EditText editTitle;
    private EditText editDescription;
    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    /**
     * inits vars and view items, and buttons

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);

        editTitle.setText(getCurrentTask().getName(), TextView.BufferType.EDITABLE);
        editDescription.setText(getCurrentTask().getDescription(),TextView.BufferType.EDITABLE);

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
            getCurrentTask().setName(name);
            getCurrentTask().setDescription(description);

            updateTask();

            Intent back = new Intent();
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
        builder.put("taskID", getCurrentTask().getObjectID());

        MasterController.AsyncDeleteDocument asyncDeleteTask =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteTask.execute(new AsyncArgumentWrapper(getCurrentTask().getObjectID(), Task.class));

        MasterController.AsyncDeleteDocumentByQuery asyncDeleteDocumentByQuery =
                new MasterController.AsyncDeleteDocumentByQuery();
        asyncDeleteDocumentByQuery.execute(new AsyncArgumentWrapper(builder, Bid.class));

        Intent back = new Intent();
        back.putExtra("del", "1");
        setResult(Activity.RESULT_OK, back);
        finish();
    }


    /**
     *sends updated class to elasticsearch
     * @throws InterruptedException
     */
    private void updateTask(){  //this should hopefully work when get really data to get
        //taskBeingEdited.syncBidData();
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(getCurrentTask());
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
