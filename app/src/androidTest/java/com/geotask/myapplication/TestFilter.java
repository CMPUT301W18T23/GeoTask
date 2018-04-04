package com.geotask.myapplication;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.DataClasses.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;

import static com.geotask.myapplication.AbstractGeoTaskActivity.setCurrentUser;

@RunWith(AndroidJUnit4.class)
public class TestFilter {

    @Rule
    public ActivityTestRule<MenuActivity> MenuActivity = new ActivityTestRule<>(MenuActivity.class);

    @Before
    public void setUp(){
        User user = new User("kehan123456", "kehan1@gmail.com", "7801111111");
        setCurrentUser(user);
    }
    @Test
    public void Filter() {


    }
}