package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends AbstractGeoTaskActivity implements AdapterView.OnItemSelectedListener {
    private String status;
    private Spinner spinner;

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
        final EditText rangeText = (EditText) findViewById(R.id.textRange);
        final FilterActivity context = this;

        getSupportActionBar().setTitle("Filter");

        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("All");
        categories.add("Requested");
        categories.add("Bidded");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        //On-Click listener for Clear button to clear all fields
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            setResult(RESULT_OK);
            keywordsText.setText("");
            rangeText.setText("");
            }
        });

        //On-Click listener for Apply button to apply filters and return to the list view
        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setResult(RESULT_OK);
                //set new filters and return to MenuActivity
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                try {
                    if(rangeText.getText().toString().compareTo("")  != 0) {
                        double newRange = Double.parseDouble(rangeText.getText().toString());
                        if(newRange > 0) {
                            setSearchRange(newRange);
                        }
                        else {
                            throw new NumberFormatException();
                        }
                    }
                    else {
                        //To represent no range set
                        setSearchRange(-1);
                    }
                    setSearchKeywords(keywordsText.getText().toString().replace("%", "")
                    .replace(";", "").replace("-", "").replace("\"", ""));
                    setSearchStatus(status);
                    startActivity(intent);
                } catch(NumberFormatException e) {
                    rangeText.setText("");
                    Toast.makeText(context, R.string.DECIMAL_INVALID, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();

        if(item.compareTo("All") == 0){
            status = "All";
        } else if (item.compareTo("Requested") == 0){
            status = "Requested";
        } else if (item.compareTo("Bidded") == 0) {
            status = "Bidded";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        status = "All";
    }
}
