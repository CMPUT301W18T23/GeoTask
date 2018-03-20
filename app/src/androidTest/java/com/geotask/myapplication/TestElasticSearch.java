package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class TestElasticSearch implements AsyncCallBackManager {

    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @Before
    public void setUp() {
        MasterController.verifySettings();
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAsyncCreateAndGetBid() throws InterruptedException {
        Bid bid = new Bid("test async ProviderID", 1.2, "test async taskID");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        Bid remote = null;
        try {
            remote = (Bid) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(bid.getProviderID(), remote.getProviderID());
    }

    @Test
    public void testAsyncCreateAndGetTask() throws InterruptedException {
        Task task = new Task("randomid","test Task", "test Description");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        Task remote = null;
        try {
            remote = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(task.getDate(), remote.getDate());
    }

    @Test
    public void testCreateAndGetUser() throws InterruptedException {
        User user = new User("test user 1", "test_email@gmail.com", "555-555-5555");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));

        User remote = null;
        try {
            remote = (User) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(user.getName(), remote.getName());
    }

    @Test
    public void testDeleteDocument() throws InterruptedException {
        Bid bid = new Bid("test delete document", 2.1, "test delete document");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocumentWhenDocumentExist =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocumentWhenDocumentExist.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        Bid remote = null;
        try {
            remote = (Bid) asyncGetDocumentWhenDocumentExist.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(bid.getProviderID(), remote.getProviderID());


        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument();
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocumentWhenDocumentShouldNotExist =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocumentWhenDocumentShouldNotExist.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        try {
            remote = (Bid) asyncGetDocumentWhenDocumentShouldNotExist.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertNull(remote);
    }

    @Test
    public void testUpdateBid() throws InterruptedException {
        Bid bid = new Bid("test update document", 8.1, "test update document");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        Bid remote = null;
        try {
            remote = (Bid) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(bid.getProviderID(), remote.getProviderID());

        String updateString = "updated";
        bid.setProviderID(updateString);
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocumentAfterUpdate =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocumentAfterUpdate.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        try {
            remote = (Bid) asyncGetDocumentAfterUpdate.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(updateString, remote.getProviderID());
    }

    @Test
    public void testUpdateTask() throws InterruptedException {
        Task task = new Task("randomid","test update task", "test update description");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        Task remote = null;
        try {
            remote = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(task.getName(), remote.getName());

        String updateString = "updated";
        task.setName(updateString);
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocumentAfterUpdate =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocumentAfterUpdate.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        try {
            remote = (Task) asyncGetDocumentAfterUpdate.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(updateString, remote.getName());
    }

    @Test
    public void testUpdateUser() throws InterruptedException {
        User user = new User("test update user 1", "test_email@gmail.com", "555-555-5555");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));

        User remote = null;
        try {
            remote = (User) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        assertEquals(user.getName(), remote.getName());

        String updateString = "updated";
        user.setName(updateString);
        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(user);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocumentAfterUpdate =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocumentAfterUpdate.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));

        try {
            remote = (User) asyncGetDocumentAfterUpdate.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(updateString, remote.getName());
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
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Bid.class));
        MasterController.AsyncSearch asyncSearch1 =
                new MasterController.AsyncSearch(this);
        asyncSearch1.execute(new AsyncArgumentWrapper(builder2, Bid.class));

        List<Bid> result1 = null;
        List<Bid> result2 = null;

        try {
            result1 = (List<Bid>) asyncSearch.get();
            result2 = (List<Bid>) asyncSearch1.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(3, result1.size());
        assertEquals(1, result2.size());
    }

    @Test
    public void testSearchTasks() throws InterruptedException {
        Task task1 = new Task("random id","test search task", "test search task");
        task1.setAcceptedBid(1.1);
        String target = "targettargettarget";
        String targetName = "task2";
        Task task2 = new Task("random ID", targetName, target);
        task2.setAcceptedBid(1.2);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task1, task2);

        TimeUnit.SECONDS.sleep(5);


        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("description", target);

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1, Task.class));

        List<Task> result = null;

        try {
            result = (List<Task>) asyncSearch.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(1, result.size());
        assertEquals(targetName, result.get(0).getName());
    }

    @Test
    public void testSearchUsers() throws InterruptedException {
        String targetUser = "target";
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User(targetUser, "1@yahoo.ca", "888");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user1, user2, user3, user4, user5);

        TimeUnit.SECONDS.sleep(5);

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("phonenum", "888");
        SuperBooleanBuilder builder2 = new SuperBooleanBuilder();
        builder2.put("email", "1@yahoo.ca");

        MasterController.AsyncSearch asyncSearch1 =
                new MasterController.AsyncSearch(this);
        asyncSearch1.execute(new AsyncArgumentWrapper(builder1, User.class));
        MasterController.AsyncSearch asyncSearch2 =
                new MasterController.AsyncSearch(this);
        asyncSearch2.execute(new AsyncArgumentWrapper(builder2, User.class));

        List<User> result1 = null;
        List<User> result2 = null;

        try {
            result1 = (List<User>) asyncSearch1.get();
            result2 = (List<User>) asyncSearch2.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(2, result1.size());
        assertEquals(1, result2.size());
        assertEquals(targetUser, result2.get(0).getName());
    }

    @Test
    public void testSearchReturnMoreThanTenItems() throws InterruptedException {
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User("user", "1@yahoo.ca", "888");
        User user6 = new User("user", "1@gmail.com", "555");
        User user7 = new User("user", "2@gmail.com", "666");
        User user8 = new User("user", "1@gmail.com", "777");
        User user9 = new User("user", "1@yahoo.com", "888");
        User user10 = new User("user", "1@yahoo.ca", "888");
        User user11 = new User("user", "1@gmail.com", "555");
        User user12 = new User("user", "2@gmail.com", "666");
        User user13 = new User("user", "1@gmail.com", "777");
        User user14 = new User("user", "1@yahoo.com", "888");
        User user15 = new User("user", "1@yahoo.ca", "888");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user1, user2, user3, user4, user5,
                                        user6, user7, user8, user9, user10,
                                        user11, user12, user13, user14, user15);

        TimeUnit.SECONDS.sleep(10);

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("name", "user");

        MasterController.AsyncSearch asyncSearch1 =
                new MasterController.AsyncSearch(this);
        asyncSearch1.execute(new AsyncArgumentWrapper(builder1, User.class));

        List<User> result1 = null;

        try {
            result1 = (List<User>) asyncSearch1.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(15, result1.size());
    }

    @Test
    public void testMultiSearch() {

    }

    @Test
    public void testExistsProfile() throws InterruptedException {
        assertFalse(MasterController.existsProfile("kyleg@email.com"));

        User user1 = new User("Kyle1", "kyleg@email.com", "555");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user1);

        TimeUnit.SECONDS.sleep(2);

        assertTrue(MasterController.existsProfile("kyleg@email.com"));
    }

    @After
    public void tearDown() {
        MasterController.shutDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    @Override
    public void onPostExecute(GTData data) {
        this.data = data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        this.searchResult = dataList;
    }
}
