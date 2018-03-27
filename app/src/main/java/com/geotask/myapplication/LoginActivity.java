package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;


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

        MasterController.verifySettings();

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

        if(!MasterController.existsProfile(email)){
            Toast.makeText(this,
                    R.string.FAILED_LOGIN_EMAIL_NOT_REGISTERED,
                    Toast.LENGTH_SHORT)
                    .show();
        } else {
            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            builder.put("email", email);

            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder, User.class));

            List<User> result;
            try {
                result = (List<User>) asyncSearch.get();

                /*
                    Setting the globals
                 */
                setCurrentUser(result.get(0));
                setHistoryHash();
                setStarHash();

                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onPostExecute(GTData data) {
    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {
    }
}
