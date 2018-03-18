package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
