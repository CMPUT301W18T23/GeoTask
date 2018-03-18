package com.geotask.myapplication;

import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class TestStoryTwoTaskInteractions {

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

    //2.a
    @Test
    public void testViewTaskDetail() {

    }

    //2.b
    @Test
    public void testEditTaskDetail() {

    }
}
