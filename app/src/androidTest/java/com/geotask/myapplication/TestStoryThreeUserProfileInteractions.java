package com.geotask.myapplication;

import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class TestStoryThreeUserProfileInteractions {

    @BeforeClass
    public static void oneTimeSetUp() {
        MasterController.verifySettings();
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //3.a
    @Test
    public void testProfileRegistration() {

    }

    //3.b
    @Test
    public void testEditProfile() {

    }

    //3.c
    @Test
    public void testViewAnotherProfile() {

    }
}
