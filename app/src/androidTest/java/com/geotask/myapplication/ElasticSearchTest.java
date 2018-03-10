package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.ArgumentWrappers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.AsyncController;
import com.geotask.myapplication.Controllers.AsyncController;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class ElasticSearchTest {


    @BeforeClass
    public static void oneTimeSetUp() {
        AsyncController.verifySettings();
        AsyncController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            AsyncController.deleteIndex();
            AsyncController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        AsyncController.verifySettings();
    }

    @Test
    public void testAsyncCreateAndGetBid() throws InterruptedException {
        Bid bid = new Bid("test async ProviderID", 1.2, "test async taskID");

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncGetDocument asyncGetDocument =
                new AsyncController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testAsyncCreateAndGetTask() throws InterruptedException {
        Task task = new Task("test Task", "test Description");

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncGetDocument asyncGetDocument =
                new AsyncController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));
    }

    @Test public void testCreateAndGetUser() throws InterruptedException {
        User user = new User("test user 1", "test_email@gmail.com", "555-555-5555");

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncGetDocument asyncGetDocument =
                new AsyncController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));
    }

    @Test
    public void testDeleteDocument() throws InterruptedException {
        Bid bid = new Bid("test delete document", 2.1, "test delete document");

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncDeleteDocument asyncDeleteDocument =
                new AsyncController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testUpdateBid() throws InterruptedException {
        Bid bid = new Bid("test update document", 8.1, "test update document");

        AsyncController.AsyncUpdateDocument asyncUpdateDocument =
                new AsyncController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncGetDocument asyncGetDocument =
                new AsyncController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testUpdateTask() throws InterruptedException {
        Task task = new Task("test update task", "test update description");

        AsyncController.AsyncUpdateDocument asyncUpdateDocument =
                new AsyncController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncGetDocument asyncGetDocument =
                new AsyncController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));
    }

    @Test
    public void testUpdateUser() throws InterruptedException {
        User user = new User("test update user 1", "test_email@gmail.com", "555-555-5555");

        AsyncController.AsyncUpdateDocument asyncUpdateDocument =
                new AsyncController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        AsyncController.AsyncGetDocument asyncGetDocument =
                new AsyncController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));
    }

    @Test
    public void testSearchBid() throws InterruptedException {
        Bid bid1 = new Bid("aprovider", 3.1, "atask");
        Bid bid2 = new Bid("bprovider", 3.2, "atask");
        Bid bid3 = new Bid("cprovider", 3.3, "atask");

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid1, bid2, bid3);

        TimeUnit.SECONDS.sleep(5);

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("taskID", "atask");
        SuperBooleanBuilder builder2 = new SuperBooleanBuilder();
        builder2.put("providerID", "aprovider");

        AsyncController.AsyncSearch asyncSearch =
                new AsyncController.AsyncSearch();
        asyncSearch.execute(new AsyncArgumentWrapper(builder1.toString(), Bid.class),
                            new AsyncArgumentWrapper(builder2.toString(), Bid.class));
    }

    @Test
    public void testSearchTasks() throws InterruptedException {
        Task task1 = new Task("task", "a");
        task1.setAcceptedBid(1.1);
        Task task2 = new Task("task", "b");
        task2.setAcceptedBid(1.2);

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task1, task2);

        TimeUnit.SECONDS.sleep(5);


        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("description", "a");

        AsyncController.AsyncSearch asyncSearch =
                new AsyncController.AsyncSearch();
        asyncSearch.execute(new AsyncArgumentWrapper(builder1.toString(), Task.class));
    }

    @Test
    public void testSearchUsers() throws InterruptedException {
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User("user", "1@yahoo.ca", "888");

        AsyncController.AsyncCreateNewDocument asyncCreateNewDocument =
                new AsyncController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user1, user2, user3, user4, user5);

        TimeUnit.SECONDS.sleep(5);


        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("name", "user");

        AsyncController.AsyncSearch asyncSearch =
                new AsyncController.AsyncSearch();
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
        AsyncController.shutDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        AsyncController.shutDown();
    }
}
