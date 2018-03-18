package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;

public class EditProfile extends AppCompatActivity implements AsyncCallBackManager {

    private EditText userName;
    private EditText userPhone;
    private EditText userEmail;

    private Button saveEdit;
    //private ElasticsearchController newElasticSearch;

    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

       currentUser = (User) getIntent().getSerializableExtra("currentUser");

        userName = (EditText) findViewById(R.id.UserName);
        userName.setText(currentUser.getName());
        userPhone = (EditText) findViewById(R.id.UserPhone);
        userPhone.setText(currentUser.getPhonenum());
        userEmail = (EditText) findViewById(R.id.UserEmail);
        userEmail.setText(currentUser.getEmail());

        saveEdit = (Button) findViewById(R.id.SaveEdit);

        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit_Check();
            }
        });



    }

    private void Edit_Check(){
        if(isValid()){
            String userNameString = userName.getText().toString().trim();
            String userPhoneString = userPhone.getText().toString().trim();
            String userEmailString = userEmail.getText().toString().trim();

            if(!MasterController.existsProfile(userEmailString)){
                Toast.makeText(this, "This email has been registered.", Toast.LENGTH_SHORT).show();
            } else {
                User currentUser = new User(userNameString, userEmailString, userPhoneString);
                MasterController.AsyncUpdateDocument asyncUpdateDocument
                        = new MasterController.AsyncUpdateDocument();
                asyncUpdateDocument.execute(currentUser);
                try {
                    asyncUpdateDocument.get();
                } catch (Exception e){
                    e.printStackTrace();
                }
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        }


    }

    public Boolean isValid() {
        if (userName.getText().toString().trim().equals("") ) {
            Toast.makeText(this, "Empty name.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(userName.length() < 8 || userName.length() > 30){
            Toast.makeText(this,"Name should be in 8 to 30 characters.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userName.getText().toString().trim().equals("") && userName.length() < 8 && userName.length() > 30) {
            Toast.makeText(this, "Empty name.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (userEmail.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Empty email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    @Override
    public void onPostExecute(GTData data) {
        currentUser = (User) data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }

}
