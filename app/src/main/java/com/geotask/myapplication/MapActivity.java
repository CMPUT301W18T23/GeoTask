//https://developers.google.com/maps/documentation/android-api/map-with-marker
//https://developer.android.com/training/maps/index.html

package com.geotask.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by James on 2018-03-17.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // add marker at and move camera to user location
        /*LatLng user_location = new LatLng(-33.852, 151.211); //get user location, input as floats to the LatLng function
        googleMap.addMarker(new MarkerOptions().position(user_location)
                .title("You are here."));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));*/

        //add the rest of the markers by iterating over the list of tasks and getting location
        /*
        for each task{
            if task.location is within whatever specified range of user location{
                googleMap.addMarker(new MarkerOptions().position(task.location)
                    .title(task.name));
            }
        }
         */
    }
}
