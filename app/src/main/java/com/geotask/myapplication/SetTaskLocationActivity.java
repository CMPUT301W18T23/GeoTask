/*
    SetTaskLocation Activity
        Displays a map to click any position which will set the task location to the point clicked.
 */
//https://developer.android.com/training/basics/intents/result.html
//https://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android

package com.geotask.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by James on 2018-04-08.
 */

public class SetTaskLocationActivity extends AbstractGeoTaskActivity implements OnMapReadyCallback {
    private String manLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Choose Task Location"); //set the

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.e("testing","Map is ready");

        //set the OnMapClickListener
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.e("testing", "registered user mapclick");
                //add a marker at the position of the user's click
                googleMap.addMarker(new MarkerOptions().position(latLng));

                //set manLocation to be the string representation of it's LatLng object
                StringBuilder SB = new StringBuilder();
                SB.append(Double.toString(latLng.latitude))
                        .append(",").append(Double.toString((latLng.longitude)));
                manLocation = SB.toString();

                //return the manually set location in a return intent
                Intent returnIntent = new Intent();
                returnIntent.putExtra("MAN_LOC", manLocation);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

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
    }
}
