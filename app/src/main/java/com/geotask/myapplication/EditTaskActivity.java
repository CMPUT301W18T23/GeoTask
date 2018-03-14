package com.geotask.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

public class EditTaskActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editDescription;
    private Button editButton;
    private Button deleteButton;

    private Task editTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        Task testTask = new Task("need to test task", "i need a task to test so here is my task");
        testTask.setRequesterID("h55p98a2ac9ye1kf");    //user named tennis
        Intent intent = new Intent(EditTaskActivity.this, EditTaskActivity.class);
        intent.putExtra("user", testTask);
        this.editTask = (Task)intent.getSerializableExtra("user");

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
        ValidateTask check = new ValidateTask();
        if (check.checkText(name, description)){
            editTask.setName(name);
            editTask.setDescription(description);
            //possible wrong
            MasterController.AsyncUpdateDocument asyncUpdateDocument
                    = new MasterController.AsyncUpdateDocument();
            asyncUpdateDocument.execute(editTask);


        }else{
            Toast.makeText(this, "please enter valid data", Toast.LENGTH_SHORT).show();
        }

    }
    private void deleteData() {

    }
}
