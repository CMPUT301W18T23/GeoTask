/*
    Map Activity
        This file contains the logic for the map activity
        It will load and display a google map with a marker at the university of alberta
    Outstanding Issues
        -will not draw the tasks because tasks are not added with a location
        -task markers are not clickable (click task to go to task view)
        -the filtered tasklist is not passed in from the main activity
 */

//https://developers.google.com/maps/documentation/android-api/map-with-marker
//https://developer.android.com/training/maps/index.html

package com.geotask.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.geotask.myapplication.AbstractGeoTaskActivity.getTaskList;

/**
 * Created by James on 2018-03-17.
 */

public class MapActivity extends AbstractGeoTaskActivity implements OnMapReadyCallback, AsyncCallBackManager{
    private ArrayList<Task> taskList = getTaskList();
    private FusedLocationProviderClient mFusedLocationClient; //for location
    private String locationString;
    private Double user_locationX;
    private Double user_locationY;
    private LatLng user_location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Map of GeoTasks"); //set the title

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("testing","Map is ready");

        //###############set the marker click listener
        //googleMap.setOnMarkerClickListener(this);

        locationString = retrieveLocation(this); //get user location, input as floats to the LatLng function

        Log.e("testing", "drawing You Are Here marker");
        //add a "You are here" marker at and move camera to user location
        if(locationString == null){ }
        else if(locationString.equals("null")){ }
        else{
            user_locationX = Double.parseDouble((locationString.split("[,]")[0]));
            user_locationY = Double.parseDouble((locationString.split("[,]")[1]));
            user_location = new LatLng(user_locationX, user_locationY);
            googleMap.addMarker(new MarkerOptions().position(user_location)
                    .title("You are here."));
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));
        }

        //add a marker for each task with a location
        Log.e("testing","right before adding tasks");
        for (int i = 0; i < taskList.size(); i++) {
            Log.e("testing","adding task...");
            if(taskList.get(i).getLocation() == null) {}
            else if(taskList.get(i).getLocation().equals("null")){
                Log.e("testing","location was null, continuing");
            }
            else {
                //add custom marker for this task
                Task tempTask = taskList.get(i);            //get current task

                //get user that requested the task
                MasterController.AsyncGetDocument asyncGetDocument =
                        new MasterController.AsyncGetDocument(this, this);
                asyncGetDocument.execute(new AsyncArgumentWrapper(tempTask.getRequesterID(), Task.class));
                User tempUser = null;
                try {
                tempUser = (User) asyncGetDocument.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                LatLng taskLocation = new LatLng(tempTask.getLocationX(), tempTask.getLocationY());
                googleMap.addMarker(new MarkerOptions()             //add the marker to the map
                        .position(taskLocation)                     //set position of marker
                        .title(tempTask.getName())                  //set name of marker
                        //.icon(tempUser.getUserPhoto())              //set the icon to be the profile picture of the task requester
                );
            }
        }
    }

    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }
}

