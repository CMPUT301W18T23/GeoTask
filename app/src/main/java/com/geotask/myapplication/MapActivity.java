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

import static com.geotask.myapplication.AbstractGeoTaskActivity.getTaskList;

/**
 * Created by James on 2018-03-17.
 */

public class MapActivity extends AbstractGeoTaskActivity implements OnMapReadyCallback {
    private ArrayList<Task> taskList = getTaskList();
    private FusedLocationProviderClient mFusedLocationClient; //for location grabbing
    private Double user_locationX;
    private Double user_locationY;
    private LatLng user_location;
    private Boolean haveLocaiton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Map of GeoTasks"); //set the title

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this); //set location client

        //get the user location
        haveLocaiton = Boolean.FALSE;
        retrieveUserLocation();     //get user location, input as floats to the LatLng function
        while(!haveLocaiton);   //wait for location data

        try {
            user_location = new LatLng(user_locationX, user_locationY);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }

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
        // add a "You are here" marker at and move camera to user location
        googleMap.addMarker(new MarkerOptions().position(user_location)
                .title("You are here."));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));

        //add a marker for each task with a location
        Log.e("testing","right before adding tasks");
        for (int i = 0; i < taskList.size(); i++) {
            Log.e("testing","adding task...");
            if(taskList.get(i).getLocation() == ""){ continue; }
            //add custom marker for this task
            LatLng taskLocation = new LatLng(taskList.get(i).getLocationX(), taskList.get(i).getLocationY());
            googleMap.addMarker(new MarkerOptions()         //add the marker to the map
                .position(taskLocation)                     //set position of marker
                .title(taskList.get(i).getName())           //set name of marker
                );                                          //set the icon to be the profile picture of the task requester
        }

    }

    public void retrieveUserLocation() {
        Log.e("testing", "retrieving user location");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e("testing","permission was granted!");
            //permission already granted, get the last location
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            Log.e("testing", "onSuccess");
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.e("testing", "loc is not null");

                                haveLocaiton = Boolean.TRUE;

                                //set coordString to the correct location, formatted
                                user_locationX = location.getLatitude();
                                user_locationY = location.getLongitude();
                            } else {
                                Log.e("testing", "location was null, setting user_location to \"\"");
                                user_locationX = 0.0;
                                user_locationY = 0.0;
                            }
                        }
                    });
        } else {
            //permission not granted, ask for permission
            Log.e("testing","permission not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);         //defining 1 to be the requestCode for accessing fine location

        }
        //end of permission check/request
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrieveUserLocation();

                } else {
                    //permission was denied
                    Log.e("testing", "permission explicitly denied, setting user_loc to 0,0");
                    user_locationX = 0.0;
                    user_locationY = 0.0;
                }
                return;
            }
        }
    }
}

