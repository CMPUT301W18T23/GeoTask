package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;


//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
public class RegisterActivity extends AppCompatActivity implements AsyncCallBackManager{

    private EditText newName;
    private EditText newEmail;
    private EditText newPhone;

    private Button SaveUserButton;

    private User newUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reigister);

        newName = (EditText) findViewById(R.id.newName);
        newPhone = (EditText) findViewById(R.id.newPhone);
        newEmail = (EditText) findViewById(R.id.newEmail);

        SaveUserButton = (Button) findViewById(R.id.newSave);
        SaveUserButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                register_check();
            }
        });

    }

    /**
     *
     */
    public void register_check() {
        if (isValid()) {
            String userName = newName.getText().toString().trim();
            String userPhone = newPhone.getText().toString().trim();
            String userEmail = newEmail.getText().toString().trim();

            if(!MasterController.existsProfile(userEmail)){
                Toast.makeText(this, "the name has been used", Toast.LENGTH_SHORT).show();
            } else {
                newUser = new User(userName, userEmail, userPhone);

                MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                        = new MasterController.AsyncCreateNewDocument();
                asyncCreateNewDocument.execute(newUser);

                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                intent.putExtra("currentUser", newUser);
                startActivity(intent);
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

    @Override
    public void onPostExecute(GTData data) {
        newUser = (User) data;
    }
}