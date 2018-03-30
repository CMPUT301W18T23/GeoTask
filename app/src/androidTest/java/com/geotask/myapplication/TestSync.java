package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSync implements AsyncCallBackManager {

    private LocalDataBase database;
    private ElasticsearchController controller = new ElasticsearchController();

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule =
            new ActivityTestRule<>(MenuActivity.class, false, false);

    @Before
    public void setUp() {
        controller.verifySettings();
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

    @Test
    public void testSyncDown() throws InterruptedException, IOException {
        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());

        String userId = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(userId);
        Task task = new Task("testSync","testSync", "testSync");

        controller.createNewDocument(user);
        controller.createNewDocument(task);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        TimeUnit.SECONDS.sleep(10);

        Task local = database.taskDAO().selectByID(task.getObjectID());
        assertNotNull(local);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getTargetContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        Task result = null;
        try {
            result = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(task.getObjectID(), result.getObjectID());
    }

    @Test
    public void testSyncUp() throws InterruptedException {
        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());

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
        menuActivityRule.launchActivity(intent);

        TimeUnit.SECONDS.sleep(10);

        Task local = database.taskDAO().selectByID(task.getObjectID());
        assertNotNull(local);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getTargetContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        Task result = null;
        try {
            result = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(task.getObjectID(), result.getObjectID());
    }

    @Test
    public void testConflictTaskEditedByFirstUserThenBiddedOnBySecondUser() throws InterruptedException {
        String userId = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(userId);

        Task task = new Task("beforeEdit", "beforeEdit", "beforeEdit");
        task.setObjectID("testSync");
        database.taskDAO().insert(task);

        //server on version 2, client on version 1
        try {
            controller.createNewDocument(task);
            controller.updateDocument(task, task.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Bid bid = new Bid(userId, 123.0, task.getObjectID());
        database.bidDAO().insert(bid);
        task.addBid(bid);
        assertEquals(1, database.bidDAO().selectAll().size());
        assertEquals(1.0, database.taskDAO().selectAll().get(0).getVersion());

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        Thread.sleep(2000);
        Task updated = database.taskDAO().selectByID("testSync");

        assertEquals(2.0, updated.getVersion());
        assertEquals(0, updated.getBidList().size());
        assertEquals(0, database.bidDAO().selectAll().size());
    }

    @Test
    public void testConflictAcceptingBidThatWasAlreadyDeletedFromServer() throws InterruptedException {
        String Id = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(Id);

        Task task = new Task("beforeEdit", "beforeEdit", "beforeEdit");
        task.setObjectID(Id);

        try {
            controller.createNewDocument(task);
            controller.updateDocument(task, task.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }


        Bid bid = new Bid(Id, 22.0, Id);
        task.addBid(bid);

        database.bidDAO().insert(bid);
        database.taskDAO().insert(task);
        assertEquals(1, database.taskDAO().selectAll().get(0).getBidList().size());


        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        Thread.sleep(3000);

        assertEquals(0, database.bidDAO().selectAll().size());
        assertEquals(0, database.taskDAO().selectAll().get(0).getBidList().size());
    }

    @Test
    public void testConflictTaskWasBiddedOnBySecondUserDuringEditingTaskByFirstUser() throws InterruptedException {
        String Id = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(Id);

        Task task = new Task("beforeEdit", "beforeEdit", "beforeEdit");
        task.setObjectID(Id);

        Bid bid = new Bid(Id, 22.0, Id);
        task.addBid(bid);

        try {
            controller.createNewDocument(task);
            controller.createNewDocument(bid);
            controller.updateDocument(task, task.getVersion());
        } catch (IOException e) {
            e.printStackTrace();
        }


        task.setDescription("afterEdit");
        database.taskDAO().insert(task);


        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        Thread.sleep(3000);

        Task remote = null;
        try {
            remote = (Task) controller.getDocument(Id, Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertEquals("afterEdit", remote.getDescription());
        assertEquals("afterEdit", database.taskDAO().selectAll().get(0).getDescription());
        assertEquals(1, database.bidDAO().selectAll().size());
    }

    @Test
    public void testBidLocalShouldBeAddedToServer() throws InterruptedException {
        String Id = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(Id);

        Task task = new Task("beforeEdit", "beforeEdit", "beforeEdit");
        task.setObjectID(Id);

        Bid bid = new Bid(Id, 22.0, Id);
        task.addBid(bid);
        try {
            controller.createNewDocument(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
        database.taskDAO().insert(task);
        database.bidDAO().insert(bid);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        Thread.sleep(3000);

        assertEquals(1, database.taskDAO().selectAll().get(0).getBidList().size());
        assertEquals(1, database.bidDAO().selectAll().size());
        Bid remote = null;
        try {
            controller.getDocument(bid.getObjectID(), Bid.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(remote);
    }

    @Test
    public void testBidLocalShouldNotBeAddedToServerIfTaskNoLongerExistsOnServer() throws InterruptedException {
        String Id = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(Id);

        Task task = new Task("beforeEdit", "beforeEdit", "beforeEdit");
        task.setObjectID(Id);

        Bid bid = new Bid(Id, 22.0, Id);
        task.addBid(bid);

        database.taskDAO().insert(task);
        database.bidDAO().insert(bid);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        Thread.sleep(3000);

        assertEquals(0, database.taskDAO().selectAll().get(0).getBidList().size());
        assertEquals(0, database.bidDAO().selectAll().size());
        Bid remote = null;
        try {
            controller.getDocument(bid.getObjectID(), Bid.class);
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
