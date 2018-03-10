package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.ArgumentWrappers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class ElasticSearchTest {


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

    @Before
    public void setUp() {
        MasterController.verifySettings();
    }

    @Test
    public void testAsyncCreateAndGetBid() throws InterruptedException {
        Bid bid = new Bid("test async ProviderID", 1.2, "test async taskID");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testAsyncCreateAndGetTask() throws InterruptedException {
        Task task = new Task("test Task", "test Description");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));
    }

    @Test public void testCreateAndGetUser() throws InterruptedException {
        User user = new User("test user 1", "test_email@gmail.com", "555-555-5555");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));
    }

    @Test
    public void testDeleteDocument() throws InterruptedException {
        Bid bid = new Bid("test delete document", 2.1, "test delete document");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testUpdateBid() throws InterruptedException {
        Bid bid = new Bid("test update document", 8.1, "test update document");

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testUpdateTask() throws InterruptedException {
        Task task = new Task("test update task", "test update description");

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));
    }

    @Test
    public void testUpdateUser() throws InterruptedException {
        User user = new User("test update user 1", "test_email@gmail.com", "555-555-5555");

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));
    }

    @Test
    public void testSearchBid() throws InterruptedException {
        Bid bid1 = new Bid("aprovider", 3.1, "atask");
        Bid bid2 = new Bid("bprovider", 3.2, "atask");
        Bid bid3 = new Bid("cprovider", 3.3, "atask");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid1, bid2, bid3);

        TimeUnit.SECONDS.sleep(5);

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("taskID", "atask");
        SuperBooleanBuilder builder2 = new SuperBooleanBuilder();
        builder2.put("providerID", "aprovider");

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch();
        asyncSearch.execute(new AsyncArgumentWrapper(builder1.toString(), Bid.class),
                            new AsyncArgumentWrapper(builder2.toString(), Bid.class));
    }

    @Test
    public void testSearchTasks() throws InterruptedException {
        Task task1 = new Task("task", "a");
        task1.setAcceptedBid(1.1);
        Task task2 = new Task("task", "b");
        task2.setAcceptedBid(1.2);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task1, task2);

        TimeUnit.SECONDS.sleep(5);


        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("description", "a");

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch();
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));
    }

    @Test
    public void testSearchUsers() throws InterruptedException {
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User("user", "1@yahoo.ca", "888");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user1, user2, user3, user4, user5);

        TimeUnit.SECONDS.sleep(5);


        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("name", "user");

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch();
        asyncSearch.execute(new AsyncArgumentWrapper(builder1.toString(), User.class));
    }

    @Test
    public void testMultiSearch() {

    }

    @Test
    public void testExistsProfile() {

    }

    @After
    public void tearDown() {
        MasterController.shutDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        MasterController.shutDown();
    }
}
