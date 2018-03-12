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
        solo = new Solo(getInstrumentation(), getActivity());
    }





    @Test
    public void testLoginAndRegister() {



        RandomStringUtility random = new RandomStringUtility();

        String testEmail = RandomStringUtility.generateString();

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        /*solo.clickOnButton("Register");

        solo.assertCurrentActivity("Wrong Activity", RegisterActivity.class);
        solo.enterText((EditText) solo.getView(R.id.newName), "TestName");
        solo.enterText((EditText) solo.getView(R.id.newEmail), testEmail);
        solo.enterText((EditText) solo.getView(R.id.newPhone), "111-222-3333");
        solo.clickOnButton("Save");

        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);
        solo.enterText((EditText) solo.getView(R.id.emailText), testEmail);
        solo.clickOnButton("Login");

        solo.assertCurrentActivity("Wrong Activity", MenuActivity.class);

*/
        
        //check that user was created on server
        //check that user was passed correctly



    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }


}
