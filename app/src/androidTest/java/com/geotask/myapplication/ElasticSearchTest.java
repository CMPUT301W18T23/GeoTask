package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.ArgumentWrappers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.ElasticsearchController;
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
        ElasticsearchController.verifySettings();
        ElasticsearchController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            ElasticsearchController.deleteIndex();
            ElasticsearchController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
        ElasticsearchController.verifySettings();
    }

    @Test
    public void testCreateAndGetBid() {
        Bid bid = new Bid("test ProviderID", 1.1, "test TaskID");
        String ID;
        Bid remote = null;

        try {
            ElasticsearchController.createNewDocument(bid);
            remote = (Bid) ElasticsearchController.getDocument(bid.getObjectID(), Bid.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(bid.getProviderID(), remote.getProviderID());
        assertEquals(bid.getValue(), remote.getValue());
        assertEquals(bid.getTaskID(), remote.getTaskID());
    }

    @Test
    public void testAsyncCreateAndGetBid() throws InterruptedException {
        Bid bid = new Bid("test async ProviderID", 1.2, "test async taskID");

        ElasticsearchController.AsyncCreateNewDocument asyncCreateNewDocument =
                new ElasticsearchController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(bid);

        TimeUnit.SECONDS.sleep(3);

        ElasticsearchController.AsyncGetDocument asyncGetDocument =
                new ElasticsearchController.AsyncGetDocument();
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));
    }

    @Test
    public void testCreateAndGetTask() {
        Task task = new Task("test Task", "test Description");
        String ID;
        Task remote = null;

        try {
            ElasticsearchController.createNewDocument(task);
            remote = (Task) ElasticsearchController.getDocument(task.getObjectID(), Task.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(task.getName(), remote.getName());
        assertEquals(task.getDescription(), remote.getDescription());
    }

    @Test public void testCreateAndGetUser() {
        User user = new User("test user 1", "test_email@gmail.com", "555-555-5555");
        String ID;
        User remote = null;

        try {
            ElasticsearchController.createNewDocument(user);
            remote = (User) ElasticsearchController.getDocument(user.getObjectID(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertEquals(user.getEmail(), remote.getEmail());
        assertEquals(user.getName(), remote.getName());
        assertEquals(user.getPhonenum(), user.getPhonenum());
    }

    @Test
    public void testDeleteDocument() {
        Bid bid = new Bid("test delete document", 2.1, "test delete document");
        String ID = null;
        int code = -1;

        try {
            ElasticsearchController.createNewDocument(bid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            code = ElasticsearchController.deleteDocument(bid.getObjectID(), Bid.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(200, code);
    }

    @Test
    public void testSearchBid() throws InterruptedException {
        Bid bid1 = new Bid("aprovider", 3.1, "atask");
        Bid bid2 = new Bid("bprovider", 3.2, "atask");
        Bid bid3 = new Bid("cprovider", 3.3, "atask");

        try {
            ElasticsearchController.createNewDocument(bid1);
            ElasticsearchController.createNewDocument(bid2);
            ElasticsearchController.createNewDocument(bid3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(3);

        List<Bid> searchResultList1 = null;

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("taskID", "atask");
        try {
            searchResultList1 = (List<Bid>) ElasticsearchController.search(builder1.toString(), Bid.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(3, searchResultList1.size());
        assertEquals(bid1.getValue(), searchResultList1.get(0).getValue());
        assertEquals(bid2.getValue(), searchResultList1.get(1).getValue());
        assertEquals(bid3.getValue(), searchResultList1.get(2).getValue());

        SuperBooleanBuilder builder2 = new SuperBooleanBuilder();
        builder2.put("providerID", "aprovider");
        try {
            searchResultList1 = (List<Bid>) ElasticsearchController.search(builder2.toString(), Bid.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, searchResultList1.size());
        assertEquals(bid1.getValue(), searchResultList1.get(0).getValue());
    }

    @Test
    public void testSearchTasks() throws InterruptedException {
        Task task1 = new Task("task", "a");
        task1.setAcceptedBid(1.1);
        Task task2 = new Task("task", "b");
        task2.setAcceptedBid(1.2);

        try {
            ElasticsearchController.createNewDocument(task1);
            ElasticsearchController.createNewDocument(task2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(3);

        List<Task> searchResultList = null;

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("description", "a");
        try {
            searchResultList = (List<Task>) ElasticsearchController.search(builder1.toString(), Task.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1, searchResultList.size());
    }

    @Test
    public void testSearchUsers() throws InterruptedException {
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User("user", "1@yahoo.ca", "888");

        try {
            ElasticsearchController.createNewDocument(user1);
            ElasticsearchController.createNewDocument(user2);
            ElasticsearchController.createNewDocument(user3);
            ElasticsearchController.createNewDocument(user4);
            ElasticsearchController.createNewDocument(user5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(5);

        List<User> searchResultList = null;

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("name", "user");
        try {
            searchResultList = (List<User>) ElasticsearchController.search(builder1.toString(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(5, searchResultList.size());
    }

    @Test
    public void testMultiSearch() {

    }

    @Test
    public void testExistsProfile() {
        User user1 = new User("uniqueness test", "123@gmail.com", "1234566");
        try {
            ElasticsearchController.createNewDocument(user1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<User> searchResultList = null;

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("email", "123@gmail.com");
        try {
            searchResultList = (List<User>) ElasticsearchController.search(builder1.toString(), User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(searchResultList.size() == 0);
    }

    @After
    public void tearDown() {
        ElasticsearchController.shutDown();
    }

    @AfterClass
    public static void oneTimeTearDown() {
        ElasticsearchController.shutDown();
    }
}
