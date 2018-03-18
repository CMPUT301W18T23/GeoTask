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
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by James on 2018-03-17.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, AsyncCallBackManager {
    private User currentUser;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        //get the tasks form the server
        /*SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));*/


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
        LatLng user_location = new LatLng( 53.523, -113.526);//currentUser.getLocationX(), currentUser.getLocationY()); //get user location, input as floats to the LatLng function
        googleMap.addMarker(new MarkerOptions().position(user_location)
                .title("You are here."));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(user_location));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        /*for (int i = 0; i < taskList.size(); i++) {
            if(taskList.get(i).getLocation() == ""){ continue; }
            LatLng taskLocation = new LatLng(taskList.get(i).getLocationX(), taskList.get(i).getLocationY());
            googleMap.addMarker(new MarkerOptions().position(taskLocation).title(taskList.get(i).getName()));
        }*/

    }

    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {
        //add the rest of the markers by iterating over the list of tasks and getting location
        taskList = (List<Task>) searchResult;
    }
}
