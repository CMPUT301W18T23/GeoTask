//https://developer.android.com/training/location/retrieve-current.html#permissions
//https://developer.android.com/training/permissions/requesting.html


package com.geotask.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * handles adding a task by the task requester
 * the user must be in requester mode for the button to show to go to this activity
 * @see MenuActivity
 */

public class AddTaskActivity extends AbstractGeoTaskActivity {


    private EditText Title;
    private EditText Description;
    private Button Picture;
    private Button Map;
    private Button Save;
    private Task newTask;

    /*
    private FusedLocationProviderClient mFusedLocationClient; //for location grabbing
    private String coordString;*/ //delete me

    /**
     * sets up buttons
     *
     * @param savedInstanceState nothing is returned
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_add_task);

        Title = findViewById(R.id.TaskTitle);
        Description = findViewById(R.id.TaskDescription);
        //String Status = "requested";
        Picture = findViewById(R.id.TaskPictures);
        Map = findViewById(R.id.TaskMap);
        Save = findViewById(R.id.TaskSave);
        Save.setEnabled(true);

        Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTaskActivity.this, SelectPhotoActivity.class);
                startActivity(intent);
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

    /**
     * gets text for adding task,
     * checks if it is valid,
     * then adds a new task through master controller
     * if text is not valid it notifies the user
     * no paramaters or return values
     * uses UserEntryStringValidator to validate if the text is correct
     *
     * @see UserEntryStringValidator
     */
    private void addTask() {
        Save.setEnabled(false);
        String titleString = Title.getText().toString().trim();
        String descriptionString = Description.getText().toString().trim();

        //ask for location
        String location = retrieveLocation(this);

        if (titleString.length() > 30) {
            Toast.makeText(this,
                    getString(R.string.TASK_TITLE_TOO_LONG),
                    Toast.LENGTH_LONG)
                    .show();
        } else if (titleString.length() <= 0) {
            Toast.makeText(this,
                    getString(R.string.TASK_TITLE_EMPTY),
                    Toast.LENGTH_SHORT)
                    .show();
        } else if (descriptionString.length() > 300) {
            Toast.makeText(this,
                    getString(R.string.TASK_DESCRIPTION_TOO_LONG),
                    Toast.LENGTH_LONG)
                    .show();
        } else if (descriptionString.length() <= 0) {
            Toast.makeText(this,
                    getString(R.string.TASK_DESCRIPTION_EMPTY),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            newTask = new Task(getCurrentUser().getObjectID(), titleString, descriptionString, location);

            MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                    = new MasterController.AsyncCreateNewDocument(this);
            asyncCreateNewDocument.execute(newTask);

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            startActivity(intent);
        }
    }


}
