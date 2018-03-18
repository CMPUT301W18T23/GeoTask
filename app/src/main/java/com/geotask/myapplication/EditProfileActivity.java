package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.User;

/**
 * allows user to edit their profile
 * their profile data includes:
 * name, phone and Number
 */
public class EditProfileActivity extends AppCompatActivity {

    private EditText userName;
    private EditText userPhone;
    private EditText userEmail;
    private Button saveEdit;
    private User currentUser;

    /**
     *init buttons edit text etc
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        currentUser = (User) getIntent().getSerializableExtra(getString(R.string.CURRENT_USER));

        userName = findViewById(R.id.UserName);
        userName.setText(currentUser.getName());
        userPhone = findViewById(R.id.UserPhone);
        userPhone.setText(currentUser.getPhonenum());
        userEmail = findViewById(R.id.UserEmail);
        userEmail.setText(currentUser.getEmail());
        saveEdit = findViewById(R.id.SaveEdit);
        saveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit_Check();
            }
        });



    }

    /**
     *gets text
     * checks if valid
     * then updates new data
     */
    private void Edit_Check(){
        if(isValid()){
            String userNameString = userName.getText().toString().trim();
            String userPhoneString = userPhone.getText().toString().trim();
            String userEmailString = userEmail.getText().toString().trim();

            if(!MasterController.existsProfile(userEmailString)){
                Toast.makeText(this, getString(R.string.EMAIL_ALREADY_IN_USE_WHEN_REGISTERING_AND_EDITING), Toast.LENGTH_SHORT).show();
            } else {
                currentUser.setName(userNameString);
                currentUser.setPhonenum(userPhoneString);
                currentUser.setEmail(userEmailString);

                MasterController.AsyncUpdateDocument asyncUpdateDocument
                        = new MasterController.AsyncUpdateDocument();
                asyncUpdateDocument.execute(currentUser);

                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                intent.putExtra(getString(R.string.CURRENT_USER), currentUser);
                startActivity(intent);
            }
        }


    }

    /**
     *mkaes sure that the data is valid
     * checks if empty
     * checks char limit
     */
    private Boolean isValid() {
        if (userName.getText().toString().trim().equals("") ) {
            Toast.makeText(this,
                    R.string.NAME_IS_EMPTY_WHEN_REGISTER_OR_EDITING_USER,
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        }else if(userName.length() > 30){
            Toast.makeText(this,
                    R.string.NAME_EXCEEDS_30_CHARACTER_WHEN_REGISTER_AND_EDIT_USER,
                    Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (userName.getText().toString().trim().equals("") && userName.length() < 8 && userName.length() > 30) {
            Toast.makeText(this,
                    R.string.NAME_IS_EMPTY_WHEN_REGISTER_OR_EDITING_USER,
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (userName.getText().toString().trim().equals("")) {
            Toast.makeText(this,
                    R.string.EMAIL_IS_EMPTY_WHEN_REGISTER_OR_EDITING_USER,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
