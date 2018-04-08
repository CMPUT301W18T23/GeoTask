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
import com.geotask.myapplication.DataClasses.Photo;
import com.geotask.myapplication.DataClasses.Task;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private List<byte[]> photoList;
    private Photo photo;


    private Button testbutton;

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
        Save.setEnabled(true);
        photoList = new ArrayList<>();

        Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTaskActivity.this, SelectPhotoActivity.class);
                intent.putExtra("type","add");
                intent.putExtra(getString(R.string.PHOTO_LIST_SIZE), photoList.size());
                System.out.println("1234567890"+photoList.size());
                for (int i = 0; i < photoList.size(); i++) {
                    intent.putExtra("list" + i, photoList.get(i));
                }
                //}
                startActivityForResult(intent,1);
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


        testbutton = findViewById(R.id.button2);
        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddTaskActivity.this, SelectPhotoActivity.class);
                intent.putExtra("type","view");
                intent.putExtra(getString(R.string.PHOTO_LIST_SIZE), photoList.size());
                for (int i = 0; i < photoList.size(); i++) {
                    intent.putExtra("list" + i, photoList.get(i));
                }
                startActivity(intent);

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
        Save.setEnabled(false);
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

        if(titleString.length() > 30) {
            Toast.makeText(this,
                    getString(R.string.TASK_TITLE_TOO_LONG),
                    Toast.LENGTH_LONG)
                    .show();
        } else if(titleString.length() <= 0) {
            Toast.makeText(this,
                    getString(R.string.TASK_TITLE_EMPTY),
                    Toast.LENGTH_SHORT)
                    .show();
        } else if(descriptionString.length() > 300) {
            Toast.makeText(this,
                    getString(R.string.TASK_DESCRIPTION_TOO_LONG),
                    Toast.LENGTH_LONG)
                    .show();
        } else if(descriptionString.length() <= 0) {
            Toast.makeText(this,
                    getString(R.string.TASK_DESCRIPTION_EMPTY),
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            newTask = new Task(getCurrentUser().getObjectID(), titleString, descriptionString);



            MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                    = new MasterController.AsyncCreateNewDocument(this);
            asyncCreateNewDocument.execute(newTask);

            photo = new Photo(newTask.getObjectID(),photoList);

            MasterController.AsyncCreateNewDocument asyncCreateNewDocument1
                    = new MasterController.AsyncCreateNewDocument(this);
            asyncCreateNewDocument1.execute(photo);

            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            startActivity(intent);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Intent intent = data;


            int size = 0;
            if(data != null){
                size = Integer.parseInt(intent.getExtras().get(getString(R.string.PHOTO_LIST_SIZE)).toString());
                photoList.clear();}
            for (int i = 0; i < size; i++) {
                photoList.add(intent.getByteArrayExtra("list" + i));
            }


            System.out.println("123321123321123321123231123123123131121hg"+photoList.size());
        }
    }
}
