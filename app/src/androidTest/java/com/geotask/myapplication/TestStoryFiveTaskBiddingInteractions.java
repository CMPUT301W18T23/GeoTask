package com.geotask.myapplication;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestStoryFiveTaskBiddingInteractions {

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

    //5.a
    @Test
    public void testPlaceBid() {

    }

    //5.b
    @Test
    public void testDeleteBid() {

    }

    //5.c
    @Test
    public void testViewMyBiddedTasks() {

    }

    //5.d
    @Test
    public void testViewMyTasksWhichHaveBids() {

    }

    //5.e
    @Test
    public void testViewBidsOnTask() {

    }

    //5.f
    @Test
    public void testAcceptBid() {

    }

    //5.g
    @Test
    public void testDeclineBid() {

    }
}
