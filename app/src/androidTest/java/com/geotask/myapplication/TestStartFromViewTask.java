package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestStartFromViewTask {

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {

    }
    @Rule
    public ActivityTestRule<TaskViewActivity> taskViewActivity
            = new ActivityTestRule<TaskViewActivity>(TaskViewActivity.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext =
                            InstrumentationRegistry.getInstrumentation().getTargetContext();
                    Intent result = new Intent(targetContext, TaskViewActivity.class);
                    result.putExtra("task",
                            new Task("TestTest", "TestTest", "TestTest"));
                    result.putExtra("Id",
                            new User("TestUSer", "TestUser", "TestUser"));
                    result.putExtra("currentUser",
                            new User("testtestUser", "testtestUser", "testtestUser"));

                    return result;
                }
            };

    @Test
    public void testViewBidShouldDisplayListOfBidsOnTask() {

    }

    @Test
    public void testAddBidShouldLetYouAddBidIfProviderModeAndNotYourOwnTask() {
    }

    @Test
    public void testAddBidButtonShouldNotShowIfYourOwnTask() {

    }

    @Test
    public void testAcceptBid() {

    }

    @Test
    public void testDeclineBid() {

    }

    @Test
    public void testClickOnUserNameShouldStartViewUserActivity() {

    }
}