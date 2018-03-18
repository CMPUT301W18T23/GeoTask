package com.geotask.myapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestMap {

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule = new ActivityTestRule<>(MenuActivity.class);

}
