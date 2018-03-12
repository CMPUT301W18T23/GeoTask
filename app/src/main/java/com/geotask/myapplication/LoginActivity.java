package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.List;
import java.util.concurrent.ExecutionException;


//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
public class LoginActivity extends AppCompatActivity implements AsyncCallBackManager {

    private EditText emailText;


    private Button loginButton;
    private Button registerButton;
    private ElasticsearchController newElasticSearch;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText = (EditText) findViewById(R.id.emailText);
        System.out.print("register_check");

        loginButton = (Button) findViewById(R.id.loginButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        //Check UserName entered in edit text field and log the user in if valid
        //Sends the user to MainActivity
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

    public void login_check() {
        String email = emailText.getText().toString().trim();

        if(!MasterController.existsProfile(email)){
            Toast.makeText(this, "This email has not been registered.", Toast.LENGTH_SHORT).show();
        } else {


            SuperBooleanBuilder builder = new SuperBooleanBuilder();
            builder.put("email", email);

            MasterController.AsyncSearch asyncSearch =
                    new MasterController.AsyncSearch(this);
            asyncSearch.execute(new AsyncArgumentWrapper(builder, User.class));

            List<User> result = null;

            try {
                result = (List<User>) asyncSearch.get();
                user = result.get(0);
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                intent.putExtra("currentUser", user);
                startActivity(intent);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException i) {
                i.printStackTrace();
            }

        }
    }


    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {
        user = (User) searchResult.get(0);

    }
}
