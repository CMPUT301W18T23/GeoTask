package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.DataClasses.User;


//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
public class LoginActivity extends AppCompatActivity {

    private EditText userNameText;


    private Button loginButton;
    private Button registerButton;
    private ElasticsearchController newElasticSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userNameText = (EditText) findViewById(R.id.userNameText);
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

    public void login_check(){
        System.out.print("login_check");

        String ID;
        String userName = userNameText.getText().toString().trim();

        this.newElasticSearch = new ElasticsearchController();
        newElasticSearch.verifySettings();
        System.out.print("elasticsearch good");
        if(!(newElasticSearch.existsProfile(userName))){
            Toast.makeText(this, "This user name doesn't exist.", Toast.LENGTH_SHORT).show();
        }
        else{
            //User newUser = new User(userName, userEmail, userPhone);
           // try {
                //ID = newElasticSearch.createNewDocument(newUser);
                //newUser.setObjectID(ID);
                //Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                    intent.putExtra("EXTRA_SESSION_ID", 123);
                //intent.putExtra("MyClass", newUser);
                //startActivity(intent);


           // } catch  (java.io.IOException e) {
                //System.out.print("IO error, please wait and try again.");
            //}
        }
    }


}
