package com.geotask.myapplication.TestStory;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.R;
import com.geotask.myapplication.TestServerAddress;
import com.geotask.myapplication.ViewTaskActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;

@RunWith(AndroidJUnit4.class)
public class TestStory7TaskCompletionInteractions {

    LocalDataBase database;

    @BeforeClass
    public static void oneTimeSetUp() {
        MasterController.verifySettings(InstrumentationRegistry.getTargetContext());
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Rule
    public ActivityTestRule<ViewTaskActivity> taskViewActivityActivityTestRule =
            new ActivityTestRule<>(ViewTaskActivity.class, false, false);

    //7.a
    //part 5
    @Test
    public void testChangeStatusWhenComplete() {

    }

    //7.b
    @Test
    public void testChangeStatusWhenNotCompleted() throws InterruptedException, IOException {
        String requesterID = "testChangeStatusWhenNotCompleted_requester";
        String providerID = "testChangeStatusWhenNotCompleted_provider";

        User requester = new User("testChangeStatusWhenNotCompleted", "testChangeStatusWhenNotCompleted", "testChangeStatusWhenNotCompleted");
        requester.setObjectID(requesterID);
        User provider = new User("testChangeStatusWhenNotCompleted", "testChangeStatusWhenNotCompleted", "testChangeStatusWhenNotCompleted");
        provider.setObjectID(providerID);

        Task task = new Task(requesterID,
                "testChangeStatusWhenNotCompleted",
                "testChangeStatusWhenNotCompleted");
        task.setAcceptedProviderID(provider.getObjectID());
        task.setRequesterID(requesterID);
        Bid bid1 = new Bid(provider.getObjectID(), 11.0, task.getObjectID());
        task.setStatus("Accepted");
        task.setAcceptedBid(bid1.getValue());
//        task.setAccpeptedBidID(bid1.getObjectID());
        task.addBid(bid1);
        Bid bid2 = new Bid(provider.getObjectID(), 12.0, task.getObjectID());
        task.addBid(bid2);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(task, requester, bid1, bid2, provider);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ViewTaskActivity.class);
        taskViewActivityActivityTestRule.getActivity().setCurrentUser(requester);
        taskViewActivityActivityTestRule.getActivity().setCurrentTask(task);
//        intent.putExtra("currentUser", requester);
//        intent.putExtra("task", task);
        taskViewActivityActivityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.status_header))
                .check(matches(withText(containsString("Accepted"))));

//        onView(withId(R.id.bidsButton)).perform(click());
//        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(0).perform(click());
//        onView(withId(R.id.btn_delete_my_bid)).perform(click());
////        Espresso.pressBack();
////        pressBack();
//        onView(withId(R.id.status_header))
//                .check(matches(withText(startsWith("Bidded"))));
        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(0).perform(click());
        onView(withId(R.id.btn_delete_my_bid)).perform(click());
//        pressBack();

        onView(withId(R.id.status_header))
                .check(matches(withText(containsString("Requested"))));}
}