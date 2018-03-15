package com.geotask.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class newAddTaskActivity extends AppCompatActivity {


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
