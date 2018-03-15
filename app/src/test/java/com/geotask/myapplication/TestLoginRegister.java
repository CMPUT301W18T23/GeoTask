package com.geotask.myapplication;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;
import com.geotask.myapplication.RandomStringUtility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.robotium.solo.Solo;



@RunWith(JUnit4.class)

public class TestLoginRegister extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo solo;



    public TestLoginRegister() {
        super(LoginActivity.class);
    }

    @Override
    public void setUp() throws Exception {

    }





    @Test
    public void testLoginAndRegister() {



        RandomStringUtility random = new RandomStringUtility();

        String testEmail = RandomStringUtility.generateString();





    }

    @Override
    public void tearDown() throws Exception {

    }


}
