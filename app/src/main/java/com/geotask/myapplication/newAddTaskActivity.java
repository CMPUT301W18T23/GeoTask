//https://developer.android.com/training/location/retrieve-current.html#permissions
//https://developer.android.com/training/permissions/requesting.html

package com.geotask.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class newAddTaskActivity extends AppCompatActivity implements AsyncCallBackManager {


    private EditText Title;
    private EditText Description;
    private Button Picture;
    private Button Map;
    private Button Save;
    private User currentUser;

    private Task newTask;

    private FusedLocationProviderClient mFusedLocationClient; //for location grabbing
    private String coordString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //set location client

        setContentView(R.layout.activity_new_add_task);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");

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

        //check if location permission is given, if not just make location = ""
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed; request the permission
                /*ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);*/

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
        }

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

        ValidateTask check = new ValidateTask();
        if(check.checkText(titleString, descriptionString)){
            newTask = new Task(currentUser.getObjectID(), titleString, descriptionString, coordString);
            MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                    = new MasterController.AsyncCreateNewDocument();
            asyncCreateNewDocument.execute(newTask);

            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            intent.putExtra("currentUser", currentUser);
            startActivity(intent);

        }else{
            Toast.makeText(this,"Please enter valid task data.", Toast.LENGTH_SHORT).show();
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
