package com.geotask.myapplication.TestStory;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.TestServerAddress;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class TestStory6TaskAssignmentInteractions {

    @BeforeClass
    public static void oneTimeSetUp() {
        MasterController.verifySettings(InstrumentationRegistry.getContext());
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //6.a
    //part 5
    @Test
    public void testViewMyAcceptedTasksAsProvider() {

    }

    //6.b
    //part 5
    @Test
    public void testViewMyAssignedTasksAsRequesters() {

    }
}
