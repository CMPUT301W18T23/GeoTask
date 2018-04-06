package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;


/**
 * MUST CHANGE SERVER ADDRESS MANUALLY since android service doesn't change the address
 * MUST run tests individually.
 * MUST run tests individually.
 * not guarranteed to pass entire test suite when ran together due to racing conditions caused by
 * multithreading and networking
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSync implements AsyncCallBackManager {

    private LocalDataBase database;
    private ElasticsearchController controller = new ElasticsearchController();

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule =
            new ActivityTestRule<>(MenuActivity.class, false, false);

    @Before
    public void setUp() throws InterruptedException {
        controller.verifySettings();
        database = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        MasterController.verifySettings(InstrumentationRegistry.getTargetContext());
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        database = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        database.bidDAO().delete();
        database.taskDAO().delete();
        database.userDAO().delete();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testSyncDown() throws InterruptedException, IOException {
        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());
        assertNotNull(controller);
        assertNotNull(database);

        String test = "testDown";
        User user = new User(test, test, test);
        user.setObjectID(test);
        Task task = new Task(test, test, test);

        controller.createNewDocument(user);
        controller.createNewDocument(task);

        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());
        try {
            assertNotNull(controller.getDocument(test, Task.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);

        Task local = database.taskDAO().selectByID(task.getObjectID());
        assertNotNull(local);
        assertEquals(local, task);
    }

    @Test
    public void testSyncUp() throws InterruptedException {
        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());
        assertNotNull(controller);
        assertNotNull(database);

        Task task = new Task("testSyncUp", "testSyncUp", "testSyncUp");

        database.taskDAO().insert(task);

        assertEquals(1, database.taskDAO().selectAll().size());

        String userId = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(userId);
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);

        Task remote = null;
        try {
            remote = (Task) controller.getDocument(task.getObjectID(), Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(remote);
        assertEquals(remote, task);
    }

    @Test
    public void testSyncUpAndDown() throws Exception {
        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());
        assertNotNull(controller);
        assertNotNull(database);

        String test = "testSyncUpAndDown";
        Task local = new Task(test,test,test);
        Task remote = new Task(test,test,test);

        database.taskDAO().insert(local);
        try {
            controller.createNewDocument(remote);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(controller.getDocument(remote.getObjectID(), Task.class));

        User user = new User(test,test,test);
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);

        List<Task> localResult = null;
        List<Task> remoteResult = null;

        localResult = database.taskDAO().selectAll();
        try {
            remoteResult = (List<Task>) controller.search("", Task.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertNotNull(localResult);
        assertNotNull(remoteResult);
        assertEquals(2, localResult.size());
        assertEquals(2, remoteResult.size());
    }

    @Test
    public void testConflictTaskEditedByOfflineUserThenBiddedOnByOnlineUser() throws InterruptedException {
        String test = "testConflictTaskEditedByOfflineUserThenBiddedOnByOnlineUser";
        User user = new User(test, test, test);
        user.setObjectID(test);

        Task task = new Task(test, test, test);
        task.setObjectID(test);

        Bid bid = new Bid(test, 123.0, task.getObjectID());
        task.addBid(bid);

        //server on version 2, client on version 1
        database.taskDAO().insert(task);
        try {
            controller.createNewDocument(bid);
            controller.createNewDocument(task);
            controller.updateDocument(task, task.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assertEquals(2.0, controller.getDocumentVersion(task.getObjectID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1.0, database.taskDAO().selectAll().get(0).getVersion());

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);
        Task updated = database.taskDAO().selectByID(test);

        assertEquals(2.0, updated.getVersion());
        assertEquals(1, updated.getBidList().size());
        assertEquals(1, database.bidDAO().selectAll().size());
    }

    @Test
    public void testConflictTaskBiddedOnByOfflineUserShouldNotPushIfTaskWasEditedByFirstUser() throws InterruptedException {
        String test = "testConflictTaskBiddedOnByOfflineUserShouldNotPushIfTaskWasEditedByFirstUser";
        User user = new User(test,test,test);
        user.setObjectID(test);

        Task task = new Task(test,test,test);
        task.setObjectID(test);
        try {
            controller.createNewDocument(task);
            controller.updateDocument(task, task.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bid bid = new Bid(test,22.22,test);
        task.addBid(bid);
        database.taskDAO().insert(task);
        database.bidDAO().insert(bid);

        try {
            assertEquals(task, controller.getDocument(task.getObjectID(), Task.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals(1, database.bidDAO().selectAll().size());
        assertEquals(1, database.taskDAO().selectAll().size());

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);
        Task remote = null;
        try {
            remote = (Task) controller.getDocument(task.getObjectID(), Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(remote);
        assertEquals(0, remote.getBidList().size());
    }

    @Test
    public void testBidLocalShouldBeAddedToServer() throws InterruptedException {
        String test = "testBidLocalShouldBeAddedToServer";
        User user = new User(test,test,test);
        user.setObjectID(test);

        Task task = new Task(test,test,test);
        task.setObjectID(test);

        try {
            controller.createNewDocument(task);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bid bid = new Bid(test, 22.0, test);
        task.addBid(bid);
        database.taskDAO().insert(task);
        database.bidDAO().insert(bid);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);

        assertEquals(1, database.taskDAO().selectAll().get(0).getBidList().size());
        assertEquals(1, database.bidDAO().selectAll().size());
        List<Bid> remote = null;
        try {
            remote = (List<Bid>) controller.search("", Bid.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(remote);
        assertEquals(bid, remote.get(0));
    }

    @Test
    public void testBidLocalShouldNotBeAddedToServerIfTaskNoLongerExistsOnServer() throws InterruptedException {
        String test = "testBidLocalShouldNotBeAddedToServerIfTaskNoLongerExistsOnServer";
        User user = new User(test,test,test);
        user.setObjectID(test);

        Task task = new Task(test,test,test);
        task.setObjectID(test);
        task.setClientOriginalFlag(false);

        Bid bid1 = new Bid(test, 22.0, test);
        task.addBid(bid1);
        Bid bid2 = new Bid(test, 23.0, test);
        bid2.setClientOriginalFlag(false);
        task.addBid(bid2);

        database.taskDAO().insert(task);
        database.bidDAO().insert(bid1);
        database.bidDAO().insert(bid2);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.getActivity().setHistoryHash();
        menuActivityRule.getActivity().setStarHash();
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);

        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0,database.bidDAO().selectAll().size());
        Bid remote = null;
        try {
            remote = (Bid) controller.search("", Bid.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNull(remote);
    }

    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }
}
