package com.geotask.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Michael on 3/9/2018.
 */

class AddTaskActivity extends AppCompatActivity {
    private EditText TaskTitle;
    private EditText TaskDescription;
    private TextView TaskStatus;
    private Button TaskPicture;
    private Button TaskMap;
    private Button SaveTask;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_add_task);

    }

}
