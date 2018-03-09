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
public class RegisterActivity extends AppCompatActivity {

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
        newEmail = (EditText) findViewById(R.id.newEmail);
        System.out.print("register_check");

        SaveUserButton = (Button) findViewById(R.id.newSave);
        SaveUserButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                register_check();
            }
        });

    }

    public void register_check(){
        System.out.print("register_check");

        if (isValid()){
            String ID;
            String userName = newName.getText().toString().trim();
            String userPhone = newPhone.getText().toString().trim();
            String userEmail = newEmail.getText().toString().trim();

            this.newElasticSearch = new ElasticsearchController();
            newElasticSearch.verifySettings();
            System.out.print("elasticsearch good");
            if(newElasticSearch.existsProfile(userEmail)){
                Toast.makeText(this, "the name has been used", Toast.LENGTH_SHORT).show();
            }
            else{
                User newUser = new User(userName, userEmail, userPhone);
                try {
                    ID = newElasticSearch.createNewDocument(newUser);
                    newUser.setObjectID(ID);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                    intent.putExtra("EXTRA_SESSION_ID", 123);
                    intent.putExtra("MyClass", newUser);
                    startActivity(intent);




// To retrieve object in second Activity


                } catch  (java.io.IOException e) {
                    System.out.print("IO error, please wait and try again");
                }
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
