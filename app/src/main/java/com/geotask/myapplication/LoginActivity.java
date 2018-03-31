package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;


//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
public class LoginActivity extends AbstractGeoTaskActivity implements AsyncCallBackManager {

    private EditText emailText;

    /**
     * Initiate variables, set on click listeners for buttons
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MasterController.verifySettings(this);

        emailText = findViewById(R.id.emailText);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        //Check UserName entered in edit text field and log the user in if valid
        //Sends the user to MenuActivity
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                login_check();
            }
        });

        //Send the user to a new RegisterActivity
        registerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Check if the entered email for login is valid,
     * and log the user in with elastic search
     */
    private void login_check() {
        String email = emailText.getText().toString().trim().toLowerCase();

        User user = MasterController.existsProfile(email);
        if(user == null){
            Toast.makeText(this,
                    R.string.FAILED_LOGIN_EMAIL_NOT_REGISTERED,
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            setCurrentUser(user);
            Intent intent = new Intent(getBaseContext(), MenuActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public void onPostExecute(GTData data) {
    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {
    }
}
