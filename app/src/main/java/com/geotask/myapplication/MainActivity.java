package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ListView taskList;

    private ArrayList<Task> taskArray;  //Array of Task Objects
    private ArrayAdapter<Task> adapter; //Adapter for listview
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button allButton = (Button) findViewById(R.id.buttonAll);
        Button requestButton = (Button) findViewById(R.id.buttonRequest);
        Button providerButton = (Button) findViewById(R.id.buttonProvider);
        Button filterButton = (Button) findViewById(R.id.buttonFilter);
        Button profileButton = (Button) findViewById(R.id.buttonProfile);
        Button mapButton = (Button) findViewById(R.id.buttonMap);
        Button logoutButton = (Button) findViewById(R.id.buttonLogout);
        taskList = (ListView) findViewById(R.id.taskListView);
        taskList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);


        Intent i = getIntent();
        this.user = (User)i.getSerializableExtra("MyClass");
        user.getName();

        //Click listener for listview, will eventually bring the user to the Task screen for selected task
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setResult(RESULT_OK);
                //Requires implementation of task activity
                //Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                //intent.putExtra("arrayPos", i); //Pass the position of the selected task as an int
                //startActivity(intent);
            }
        });

        //On-Click listener for All button to show all tasks
        allButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        //On-Click listener for Request button to show tasks that you have requested
        requestButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        //On-Click listener for Provider button to show tasks with status requested or bidded
        providerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        //On-Click listener for Filter button to take the user to an activity where they can apply filters
        filterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        //On-Click listener for Profile button to take the user to their own profile activity
        profileButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        //On-Click listener for Map button to the the user to a Map activity showing tasks in the current task list
        mapButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });

        //On-Click listener for Logout button to log the user out and return them to the login activity
        logoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
            }
        });


    }


    @Override
    //Setup the adapter for our task list, do elastic search things
    protected void onStart() {
        super.onStart();
		/*Here is where the elastic search needs to populate our array before making the adapter*/
		
        //adapter = new ArrayAdapter<Task>(this, R.layout.list_item, taskArray);
        //taskList.setAdapter(adapter);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(intent);

    }


}
