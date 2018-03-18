package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class TestStorySevenTaskCompletionInteractions {

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
    public ActivityTestRule<TaskViewActivity> taskViewActivityActivityTestRule =
            new ActivityTestRule<>(TaskViewActivity.class, false, false);

    //7.a
    //part 5
    @Test
    public void testChangeStatusWhenComplete() {

    }

    //7.b
    @Test
    public void testChangeStatusWhenNotCompleted() throws InterruptedException {
        String requesterID = "requester";
        String providerID = "provider";

        User requester = new User("Michael", "mtang", "12838202");
        requester.setObjectID("requester");
        User provider = new User("Mike", "mtangg", "39484202");
        provider.setObjectID(providerID);

        Task task = new Task(requesterID,
                "testChangeStatusWhenNotCompleted",
                "testChangeStatusWhenNotCompleted");
        task.setAcceptedProviderID(provider.getObjectID());

        Bid bid = new Bid(provider.getObjectID(), 11.0, task.getObjectID());
        task.setAcceptedBid(bid.getValue());
        task.addBid(bid);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task, requester, bid, provider);

        Thread.sleep(2000);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, TaskViewActivity.class);
        intent.putExtra("currentUser", requester);
        intent.putExtra("task", task);
        taskViewActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(0).perform(click());
        onView(withId(R.id.btn_delete)).perform(click());
        pressBack();

        Thread.sleep(1000);

        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).check(matches(isDisplayed()));


        Thread.sleep(1000);

    }
}