package com.geotask.myapplication.TestStory;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.R;
import com.geotask.myapplication.TestServerAddress;
import com.geotask.myapplication.ViewTaskActivity;

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
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestStory5TaskBiddingInteractions {


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
    public ActivityTestRule<ViewTaskActivity> testPlaceBidActivityRule =
            new ActivityTestRule<>(ViewTaskActivity.class, false, false);
    //5.a
//    @Test
//    public void testPlaceBid() throws InterruptedException {
//        String requesterID = "requester";
//        String providerID = "provider";
//
//        User requester = new User("testPlaceBid_1", "testPlaceBid_1", "testPlaceBid_1");
//        requester.setObjectID("requester");
//        User provider = new User("testPlaceBid_2", "testPlaceBid_2", "testPlaceBid_2");
//        provider.setObjectID(providerID);
//
//        Task task = new Task(requesterID,
//                "testPlaceBid",
//                "testPlaceBid");
//        task.setAcceptedProviderID(provider.getObjectID());
//        testPlaceBidActivityRule.getActivity().setCurrentUser(provider);
//        testPlaceBidActivityRule.getActivity().setCurrentTask(task);
//
//        Task T = testPlaceBidActivityRule.getActivity().getCurrentTask();
//        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
//                new MasterController.AsyncCreateNewDocument();
//        asyncCreateNewDocument.execute(task, requester, provider);
//
//        Thread.sleep(1000);
//
//        testPlaceBidActivityRule.getActivity();
//        Context targetContext =
//                InstrumentationRegistry.getInstrumentation().getTargetContext();
//        Intent intent = new Intent(targetContext, ViewTaskActivity.class);
//
////        intent.putExtra("currentUser", provider);
////        intent.putExtra("task", task);
//        testPlaceBidActivityRule.launchActivity(intent);
//
//
//        onView(ViewMatchers.withId(R.id.addBidButton)).perform(click());
//        onView(withId(R.id.editTextAmmount)).perform(clearText(),typeText("233"),closeSoftKeyboard());
//        onView(withId(R.id.btn_accept_bid)).perform(click());
//        onView(withId(R.id.bidsButton)).perform(click());
//
//        onData(anything())
//                .inAdapterView(withId(R.id.bidListView))
//                .atPosition(0)
//                .check(matches(isDisplayed()));
//    }

    //5.b
    @Test
    public void testDeleteBid() throws InterruptedException {
        String requesterID = "testDelete_Bidrequester";
        String providerID = "testDelete_Bidprovider";

        User requester = new User("testDeleteBid", "testDeleteBid", "testDeleteBid");
        requester.setObjectID(requesterID);
        User provider = new User("testDeleteBid", "testDeleteBid", "testDeleteBid");
        provider.setObjectID(providerID);

        Task task = new Task(requesterID,
                "testDeleteBid",
                "testDeleteBid");
        task.setAcceptedProviderID(provider.getObjectID());
        task.setRequesterID(requesterID);

        Bid bid = new Bid(provider.getObjectID(), 11.0, task.getObjectID());
        task.setAcceptedBid(bid.getValue());
        task.addBid(bid);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(task, requester, bid, provider);

        Thread.sleep(1000);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ViewTaskActivity.class);

        testPlaceBidActivityRule.getActivity().setCurrentUser(requester);
        testPlaceBidActivityRule.getActivity().setCurrentTask(task);
//        intent.putExtra("currentUser", requester);
//        intent.putExtra("task", task);
        taskViewActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(0).perform(click());
        onView(withId(R.id.btn_delete)).perform(click());
        pressBack();

        onView(withId(R.id.bidsButton)).perform(click());
        onView(withId(R.id.bidListView))
                .check(matches(not(hasDescendant(withText(containsString("tasks"))))));
    }

    //5.c
    //part 5
    @Test
    public void testViewMyBiddedTasks() {

    }

    //5.d
    //part 5
    @Test
    public void testViewMyTasksWhichHaveBids() {

    }

    @Rule
    public ActivityTestRule<ViewTaskActivity> taskViewActivityActivityTestRule =
            new ActivityTestRule<>(ViewTaskActivity.class, false, false);
    //5.e
    @Test
    public void testViewBidsOnTask() throws InterruptedException {
        String requesterID = "testViewBidsOnTask_requester";
        String providerID = "testViewBidsOnTask_provider";

        User requester = new User("testViewBidsOnTask", "testViewBidsOnTask", "testViewBidsOnTask");
        requester.setObjectID(requesterID);
        User provider = new User("testViewBidsOnTask", "testViewBidsOnTask", "testViewBidsOnTask");
        provider.setObjectID(providerID);

        Task task = new Task(requesterID,
                "testViewBidsOnTask",
                "testViewBidsOnTask");
        task.setRequesterID(requesterID);
        task.setAcceptedProviderID(provider.getObjectID());

        Bid bid1 = new Bid(provider.getObjectID(), 11.0, task.getObjectID());
        task.setAcceptedBid(bid1.getValue());
        task.addBid(bid1);
        Bid bid2 = new Bid(provider.getObjectID(), 12.0, task.getObjectID());
        task.addBid(bid2);
        Bid bid3 = new Bid(provider.getObjectID(), 13.0, task.getObjectID());
        task.addBid(bid3);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(task, requester, bid1, bid2, bid3, provider);

        Thread.sleep(1000);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ViewTaskActivity.class);
        testPlaceBidActivityRule.getActivity().setCurrentUser(requester);
        testPlaceBidActivityRule.getActivity().setCurrentTask(task);
//        intent.putExtra("currentUser", requester);
//        intent.putExtra("task", task);
        taskViewActivityActivityTestRule.launchActivity(intent);

        Thread.sleep(1000);

        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(2).perform(click());

        onView(withId(R.id.btn_accept)).check(matches(isDisplayed()));
    }

    //5.f
    @Test
    public void testAcceptBid() throws InterruptedException {
        String requesterID = "testAcceptBid_requester";
        String providerID = "testAcceptBid_provider";

        User requester = new User("testAcceptBid", "testAcceptBid", "testAcceptBid");
        requester.setObjectID(requesterID);
        User provider = new User("testAcceptBid", "testAcceptBid", "testAcceptBid");
        provider.setObjectID(providerID);

        Task task = new Task(requesterID,
                "testAcceptBid",
                "testAcceptBid");
        task.setRequesterID(requesterID);
        task.setAcceptedProviderID(provider.getObjectID());

        Bid bid1 = new Bid(provider.getObjectID(), 11.0, task.getObjectID());
        task.setAcceptedBid(bid1.getValue());
        task.addBid(bid1);
        Bid bid2 = new Bid(provider.getObjectID(), 12.0, task.getObjectID());
        task.addBid(bid2);
        Bid bid3 = new Bid(provider.getObjectID(), 13.0, task.getObjectID());
        task.addBid(bid3);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(task, requester, bid1, bid2, bid3, provider);

        Thread.sleep(2000);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ViewTaskActivity.class);
        taskViewActivityActivityTestRule.getActivity().setCurrentUser(requester);
        taskViewActivityActivityTestRule.getActivity().setCurrentTask(task);
//        intent.putExtra("currentUser", requester);
//        intent.putExtra("task", task);
        taskViewActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(2).perform(click());

        onView(withId(R.id.btn_accept)).perform(click());
        Task T =  taskViewActivityActivityTestRule.getActivity().getCurrentTask();
        assert(T.getStatus().equals("Accepted"));
//        onData(Matchers.anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
//        onView(withId(R.id.status_header)).check(matches(withText(startsWith("Accepted"))));
    }

    //5.g
    @Test
    public void testDeclineBid() throws InterruptedException {
        String requesterID = "testDeclineBid_requester";
        String providerID = "testDeclineBid_provider";

        User requester = new User("testDeclineBid", "testDeclineBid", "testDeclineBid");
        requester.setObjectID(requesterID);
        User provider = new User("testDeclineBid", "testDeclineBid", "testDeclineBid");
        provider.setObjectID(providerID);

        Task task = new Task(requesterID,
                "testDeclineBid",
                "testDeclineBid");
        task.setRequesterID(requesterID);
        task.setAcceptedProviderID(provider.getObjectID());

        Bid bid = new Bid(provider.getObjectID(), 11.0, task.getObjectID());
        task.setAcceptedBid(bid.getValue());
        task.addBid(bid);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(task, requester, bid, provider);

        Thread.sleep(1000);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, ViewTaskActivity.class);
        testPlaceBidActivityRule.getActivity().setCurrentUser(requester);
        testPlaceBidActivityRule.getActivity().setCurrentTask(task);
//        intent.putExtra("currentUser", requester);
//        intent.putExtra("task", task);
        taskViewActivityActivityTestRule.launchActivity(intent);

        onView(withId(R.id.bidsButton)).perform(click());
        onData(anything()).inAdapterView(withId(R.id.bidListView)).atPosition(0).perform(click());
        onView(withId(R.id.btn_delete)).perform(click());
        pressBack();

        onView(withId(R.id.bidsButton)).perform(click());
        onView(withId(R.id.bidListView))
                .check(matches(not(hasDescendant(withText(containsString("tasks"))))));
    }
}
