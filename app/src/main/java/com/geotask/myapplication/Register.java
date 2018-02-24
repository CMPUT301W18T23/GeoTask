package com.geotask.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {

    private EditText newName;
    private EditText newEmail;
    private EditText newPhone;

    private Button SaveUserButton;
    private ElasticsearchController newElasticSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reigister);

        newName = (EditText) findViewById(R.id.newName);
        newPhone = (EditText) findViewById(R.id.newPhone);
        newPhone = (EditText) findViewById(R.id.newPhone);

        SaveUserButton = (Button) findViewById(R.id.newSave);
        SaveUserButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                register_check();
            }
        });

    }

    public void register_check(){
        if (isValid()){
            String userName = newName.getText().toString().trim();
            String userPhone = newPhone.getText().toString().trim();
            String userEmail = newEmail.getText().toString().trim();

            newElasticSearch = new ElasticsearchController();
            if(newElasticSearch.existsProfile(userEmail)){
                Toast.makeText(this, "the name has been used", Toast.LENGTH_SHORT).show();
            }

            else{
                newElasticSearch.insertNewProfile(userName,userEmail,userPhone);
            }
        }
    }

    public Boolean isValid() {
        if (newName.getText().toString().trim().equals("") && newName.length() < 8 && newName.length() > 30) {
            Toast.makeText(this, "Empty name", Toast.LENGTH_SHORT).show();
            return false;


        } else if (newEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Empty password", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }
}
