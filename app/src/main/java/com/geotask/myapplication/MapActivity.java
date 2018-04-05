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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static com.geotask.myapplication.AbstractGeoTaskActivity.getTaskList;

/**
 * Created by James on 2018-03-17.
 */

public class MapActivity extends AbstractGeoTaskActivity implements OnMapReadyCallback {
    private ArrayList<Task> taskList = getTaskList();

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
        // add a default marker at and move camera to user location
        LatLng user_location = new LatLng( 53.523, -113.526);//getCurrentUser().getLocationX(), getCurrentUser().getLocationY()); //get user location, input as floats to the LatLng function
        googleMap.addMarker(new MarkerOptions().position(user_location)
                .title("You are here."));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));

        //add a marker for each task with a location
        for (int i = 0; i < taskList.size(); i++) {
            if(taskList.get(i).getLocation() == ""){ continue; }
            //add custom marker for this task
            LatLng taskLocation = new LatLng(taskList.get(i).getLocationX(), taskList.get(i).getLocationY());
            //LatLng taskLocation = new LatLng(54, -114);//#####del me
            googleMap.addMarker(new MarkerOptions()         //add the marker to the map
                .position(taskLocation)                     //set position of marker
                .title(taskList.get(i).getName())           //set name of marker
                );                                          //set the icon to be the profile picture of the task requester
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(taskLocation));
        }

    }
}
