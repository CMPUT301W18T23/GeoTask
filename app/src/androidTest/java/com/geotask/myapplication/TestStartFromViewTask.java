package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

//https://developer.android.com/training/testing/espresso/recipes.html
@RunWith(AndroidJUnit4.class)
public class TestStartFromViewTask {

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

    @Rule
    public ActivityTestRule<ViewTaskActivity> activityRule
            = new ActivityTestRule<>(ViewTaskActivity.class, true, false);

    @Test
    public void testEditTask() throws InterruptedException {
        String targetUserId = "testEditTask_id";

        Task task = new Task(targetUserId, "TestTest", "TestTest", "2234");
        User user = new User("testtestUser", "testtestUser", "testtestUser");
        user.setObjectID(targetUserId);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, ViewTaskActivity.class);

        result.putExtra("task", task);
        result.putExtra("currentUser", user);

        activityRule.launchActivity(result);

        onView(withId(R.id.editTaskButton)).perform(click());

        onView(withId(R.id.editTitle)).perform(replaceText("updated_string"));
        onView(withId(R.id.editButton)).perform(click());

        onView(withId(R.id.textViewTitle)).check(matches(withText(containsString("updated_string"))));
    }

    @Test
    public void testAddBidShouldLetYouAddBidIfProviderModeAndNotYourOwnTask() {



        String targetUserId = "testAddBidShouldLetYouAddBidIfProviderModeAndNotYourOwnTask_id";
        String requesterId = "testAddBidShouldLetYouAddBidIfProviderModeAndNotYourOwnTask_id2";
        Task task = new Task(targetUserId, "TestTest", "TestTest", "2234");
        User user = new User("testtestUser", "testtestUser", "testtestUser");
        user.setObjectID(targetUserId);
        task.setRequesterID(requesterId);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, ViewTaskActivity.class);

        result.putExtra("task", task);
        result.putExtra("currentUser", user);

        activityRule.launchActivity(result);



        onView(withId(R.id.addBidButton))
                .perform(click());

        onView(withId(R.id.editTextAmmount))
                .perform(clearText(),typeText("123456"),closeSoftKeyboard());
        onView(withId(R.id.btn_accept_bid))
                .perform(click());
        onView(withId(R.id.bidsButton))
                .perform(click());
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