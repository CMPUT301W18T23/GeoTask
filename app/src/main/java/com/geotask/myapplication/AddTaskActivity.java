//https://developer.android.com/training/location/retrieve-current.html#permissions
//https://developer.android.com/training/permissions/requesting.html

package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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

    private FusedLocationProviderClient mFusedLocationClient; //for location grabbing
    private String coordString;

    /**
     *sets up buttons
     * @param savedInstanceState
     * nothing is returned
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //set location client

        setContentView(R.layout.activity_new_add_task);

        Title = findViewById(R.id.TaskTitle);
        Description = findViewById(R.id.TaskDescription);
        //String Status = "requested";
        Picture = findViewById(R.id.TaskPictures);
        Map = findViewById(R.id.TaskMap);
        Save = findViewById(R.id.TaskSave);

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

    /**
     *gets text for adding task,
     * checks if it is valid,
     * then adds a new task through master controller
     * if text is not valid it notifies the user
     * no paramaters or return values
     * uses UserEntryStringValidator to validate if the text is correct
     * @see UserEntryStringValidator
     */
    private void addTask(){
        String titleString = Title.getText().toString().trim();
        String descriptionString = Description.getText().toString().trim();

        //check if location permission is given, if not just make location = ""
        /*if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                //give server the correct location, formatted
                                coordString = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
                            }
                            else { coordString = ""; }
                        }
                    });
        } else {
            //give server no location
            coordString = "";
        }*/

        //ValidateTask check = new ValidateTask();

        UserEntryStringValidator check = new UserEntryStringValidator();
        if(check.checkText(titleString, descriptionString)){
            newTask = new Task(getCurrentUser().getObjectID(), titleString, descriptionString);

            MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                    = new MasterController.AsyncCreateNewDocument();
            asyncCreateNewDocument.execute(newTask);

            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this,
                    getString(R.string.INVALID_TASK_DATA_WHEN_CREATING_NEW_TASK),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
