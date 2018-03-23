package com.geotask.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.geotask.myapplication.DataClasses.User;

/**
 * simple class to view user data
 */
public class ViewProfileActivity extends AbstractGeoTaskActivity {

    private TextView name;
    private TextView phone;
    private TextView email;
    private User viewUser;

    /**
     * sets up vars from intent to view user data
     * can be called from viewBidsActivity
     * can be called from TaskViewActivity
     *
     * @see ViewBidsActivity
     * @see TaskViewActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        Intent intent = getIntent();
        this.viewUser = (User) intent.getSerializableExtra(getString(R.string.VIEW_USER));
        this.name = findViewById(R.id.profileName);
        this.phone = findViewById(R.id.profilePhone);
        this.email = findViewById(R.id.profileEmail);
        this.name.setText(viewUser.getName());
        this.phone.setText(viewUser.getPhonenum());
        this.email.setText(viewUser.getEmail());
    }
}