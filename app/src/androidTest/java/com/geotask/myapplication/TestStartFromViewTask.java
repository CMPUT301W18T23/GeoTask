package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//https://developer.android.com/training/testing/espresso/recipes.html
@RunWith(AndroidJUnit4.class)
public class TestStartFromViewTask {

    @Rule
    public ActivityTestRule<TaskViewActivity> activityRule
            = new ActivityTestRule<>(TaskViewActivity.class, true, false);

    @Test
    public void testViewBidShouldDisplayListOfBidsOnTask() throws InterruptedException {
        String targetUserId = "testViewBidShouldDisplayListOfBidsOnTask_id";

        Task task = new Task(targetUserId, "TestTest", "TestTest", "2234");
        User user = new User("testtestUser", "testtestUser", "testtestUser");
        user.setObjectID(targetUserId);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, TaskViewActivity.class);

        result.putExtra("task", task);
        result.putExtra("currentUser", user);

        activityRule.launchActivity(result);

        Thread.sleep(1000);

        onView(withId(R.id.editTaskButton)).perform(click());
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