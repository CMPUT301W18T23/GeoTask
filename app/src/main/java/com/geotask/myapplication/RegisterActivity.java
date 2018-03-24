package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.User;


//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
public class RegisterActivity extends AbstractGeoTaskActivity{

    private EditText newName;
    private EditText newEmail;
    private EditText newPhone;
    private User newUser;


    /**
     * Iinitiate variables, set on click listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reigister);

        newName = findViewById(R.id.newName);
        newPhone = findViewById(R.id.newPhone);
        newEmail = findViewById(R.id.newEmail);

        Button saveUserButton = findViewById(R.id.newSave);
        saveUserButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                register_check();
            }
        });
    }

    /**
     * If entered registration information is valid, create
     * a new user object and push it to the server, return
     * to the login activity
     */
    protected void register_check() {
        if (isValid()) {
            String userName = newName.getText().toString().trim();
            String userPhone = newPhone.getText().toString().trim();
            String userEmail = newEmail.getText().toString().trim();
            if(MasterController.existsProfile(userEmail)){
                Toast.makeText(this,
                        R.string.EMAIL_ALREADY_IN_USE_WHEN_REGISTERING_AND_EDITING,
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                newUser = new User(userName, userEmail, userPhone);

                MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                        = new MasterController.AsyncCreateNewDocument();
                asyncCreateNewDocument.execute(newUser);

                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * This method is responsible for checking if entered
     * registration information is valid
     */
    private Boolean isValid() {
        if (newName.getText().toString().trim().equals("") ) {
            Toast.makeText(this,
                    R.string.NAME_IS_EMPTY_WHEN_REGISTER_OR_EDITING_USER,
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }else if(newName.length() > 30){
            Toast.makeText(this,
                    R.string.NAME_EXCEEDS_30_CHARACTER_WHEN_REGISTER_AND_EDIT_USER,
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (newName.getText().toString().trim().equals("") && newName.length() < 8 && newName.length() > 30) {
            Toast.makeText(this,
                    R.string.NAME_IS_EMPTY_WHEN_REGISTER_OR_EDITING_USER,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (newEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this,
                    R.string.EMAIL_IS_EMPTY_WHEN_REGISTER_OR_EDITING_USER,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}