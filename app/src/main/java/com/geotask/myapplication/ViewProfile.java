package com.geotask.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
/**
 * simple class to view user data
 */
public class ViewProfile extends AppCompatActivity {
    private TextView name;
    private TextView phone;
    private TextView email;
    private User viewUser;

    /**
     * sets up vars from intent to view user data
     * can be called from viewBidsActivity
     * can be called from TaskViewActivity
     * @see ViewBidsActivity
     * @see TaskViewActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Intent intent = getIntent();
        this.viewUser = (User)intent.getSerializableExtra("user");
        this.name = (TextView)findViewById(R.id.profileName);
        this.phone = (TextView)findViewById(R.id.profilePhone);
        this.email = (TextView)findViewById(R.id.profileEmail);
        this.name.setText(viewUser.getName());
        this.phone.setText(viewUser.getPhonenum());
        this.email.setText(viewUser.getEmail());



    }
}
