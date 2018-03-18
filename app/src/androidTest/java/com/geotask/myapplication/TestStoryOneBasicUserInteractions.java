package com.geotask.myapplication;

import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class TestStoryOneBasicUserInteractions {

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

    //1.a
    @Test
    public void testLogin() {

    }

    //1.b
    @Test
    public void testRequestNewTask() {

    }

    //1.c
    @Test
    public void testViewMyTasksAsRequester() {

    }

    //1.d
    @Test
    public void testDeleteTask() {

    }

    //1.e
    @Test
    public void testLogout() {

    }

    //1.f
    @Test
    public void testViewAllAvailableTasks() {

    }

    //1.g
    @Test
    public void testViewAllTasksOnMap() {

    }
}
