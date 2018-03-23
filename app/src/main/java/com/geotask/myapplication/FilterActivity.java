package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FilterActivity extends AbstractGeoTaskActivity {

    /**
     * Initiate variables and grab current user,
     * as well as setting on click listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        Button applyButton = (Button) findViewById(R.id.buttonApply);
        Button clearButton = (Button) findViewById(R.id.buttonClear);
        final EditText keywordsText = (EditText) findViewById(R.id.textKeywords);


        //On-Click listener for Clear button to clear all fields
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                keywordsText.setText("");
            }
        });

        //On-Click listener for Apply button to apply filters and return to the list view
        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                //make builder and put it into intent
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                setSearchKeywords(keywordsText.getText().toString());
                startActivity(intent);
            }
        });

    }
}
