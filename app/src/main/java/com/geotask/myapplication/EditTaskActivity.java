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
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EditTaskActivity extends AppCompatActivity  implements AsyncCallBackManager {
    private EditText editTitle;
    private EditText editDescription;
    private Button editButton;
    private Button deleteButton;
    private Task editTask;
    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

//        Task testTask = new Task("need to test task", "i need a task to test so here is my task");
//        testTask.setRequesterID("h55p98a2ac9ye1kf");    //user named tennis
//        Intent intent = new Intent(EditTaskActivity.this, EditTaskActivity.class);
//        intent.putExtra("user", testTask);
        Intent i = getIntent();
        this.editTask = (Task)i.getSerializableExtra("Task");

//        h55p98a2ac9ye1kf
        editTitle = (EditText) findViewById(R.id.editTitle);
        editDescription = (EditText) findViewById(R.id.editDescription);

        editTitle.setText(editTask.getName(), TextView.BufferType.EDITABLE);
        editDescription.setText(editTask.getDescription(),TextView.BufferType.EDITABLE);

        editButton = (Button) findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                editData();
            }
        });

        deleteButton = (Button) findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteData();
            }
        });
    }


    private void editData() {
        String name =   editTitle.getText().toString();
        String description = editDescription.getText().toString();
        UserEntryStringValidator check = new UserEntryStringValidator();
        if (check.checkText(name, description)){
            editTask.setName(name);
            editTask.setDescription(description);
            updateTask();
            Intent back = new Intent();
            back.putExtra(getString(R.string.UPDATED_TASK_AFTER_EDIT), editTask);
            back.putExtra("currentUser", currentUser);
            setResult(Activity.RESULT_OK, back);
            finish();

        }else{
            Toast.makeText(this, "Please enter valid task data.", Toast.LENGTH_SHORT).show();
        }

    }
    private void deleteData() {
        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(editTask.getObjectID(), Task.class));

        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        finish();
    }
    private void updateTask(){  //this should hopefully work when get really data to get

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(editTask);

        try {
            TimeUnit.SECONDS.sleep(3);
        }catch (InterruptedException e) {
            e.printStackTrace();
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
