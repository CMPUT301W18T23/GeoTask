package com.geotask.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;

public class newAddTaskActivity extends AppCompatActivity implements AsyncCallBackManager {


    private EditText Title;
    private EditText Description;
    private Button Picture;
    private Button Map;
    private Button Save;

    private Task newTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_task);

        Title = (EditText) findViewById(R.id.TaskTitle);
        Description = (EditText) findViewById(R.id.TaskDescription);
        //String Status = "requested";
        Picture = (Button) findViewById(R.id.TaskPictures);
        Map = (Button) findViewById(R.id.TaskMap);
        Save = (Button) findViewById(R.id.TaskSave);

        Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTask();
            }
        });

    }

    private void addTask(){
        String titleString = Title.getText().toString().trim();
        String descriptionString = Description.getText().toString().trim();
        ValidateTask check = new ValidateTask();
        if(check.checkText(titleString, descriptionString)){
            newTask = new Task(titleString, descriptionString);

            MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                    = new MasterController.AsyncCreateNewDocument();
            asyncCreateNewDocument.execute(newTask);

            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            startActivity(intent);

        }else{
            Toast.makeText(this,"please enter valid data", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onPostExecute(GTData data) {
        newTask = (Task) data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }

}
