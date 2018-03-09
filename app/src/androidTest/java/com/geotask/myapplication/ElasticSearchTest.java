package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.BidList;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

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


@RunWith(JUnit4.class)
public class ElasticSearchTest {

    static ElasticsearchController controller;

    @BeforeClass
    public static void oneTimeSetUp() {
        controller = new ElasticsearchController();
        controller.verifySettings();
        controller.setTestSettings(TestServerAddress.getTestAddress());
        try {
            controller.deleteIndex();
            controller.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testCreateAndGetBid() {
        Bid bid = new Bid("test ProviderID", 1.1, "test TaskID");
        String ID;
        Bid remote = null;
        
        try {
            ID = controller.createNewDocument(bid);
            bid.setObjectID(ID);
            remote = (Bid) controller.getDocument(ID, "bid");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(bid.getProviderID(), remote.getProviderID());
        assertEquals(bid.getValue(), remote.getValue());
        assertEquals(bid.getTaskID(), remote.getTaskID());
    }

    @Test
    public void testCreateAndGetTask() {
        Task task = new Task("test Task", "test Description");
        String ID;
        Task remote = null;

        try {
            ID = controller.createNewDocument(task);
            task.setObjectID(ID);
            remote = (Task) controller.getDocument(ID, "task");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(task.getName(), remote.getName());
        assertEquals(task.getDescription(), remote.getDescription());
    }

    @Test
    public void testCreateAndGetBidList() {
        BidList bidList = new BidList("test taskID");
        bidList.addBid("test ID1");
        bidList.addBid("test ID2");
        String ID;
        BidList remote = null;

        try {
            ID = controller.createNewDocument(bidList);
            bidList.setObjectID(ID);
            remote = (BidList) controller.getDocument(ID, "bidList");
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(bidList.getNumBids(), remote.getNumBids());
    }

    @Test public void testCreateAndGetUser() {
        User user = new User("test user 1", "test_email@gmail.com", "555-555-5555");
        String ID;
        User remote = null;

        try {
            ID = controller.createNewDocument(user);
            user.setObjectID(ID);
            remote = (User) controller.getDocument(ID, "user");
        } catch (IOException e) {
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
            ID = controller.createNewDocument(bid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            code = controller.deleteDocument(ID, "bid");
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
            controller.createNewDocument(bid1);
            controller.createNewDocument(bid2);
            controller.createNewDocument(bid3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(3);

        List<Bid> searchResultList1 = null;

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("taskID", "atask");
        try {
            searchResultList1 = (List<Bid>) controller.search(builder1.toString(), "bid");
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
            searchResultList1 = (List<Bid>) controller.search(builder2.toString(), "bid");
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
            controller.createNewDocument(task1);
            controller.createNewDocument(task2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(3);

        List<Task> searchResultList = null;

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("description", "a");
        try {
            searchResultList = (List<Task>) controller.search(builder1.toString(), "task");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2, searchResultList.size());
    }

    @Test
    public void testSearchUsers() throws InterruptedException {
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User("user", "1@yahoo.ca", "888");

        try {
            controller.createNewDocument(user1);
            controller.createNewDocument(user2);
            controller.createNewDocument(user3);
            controller.createNewDocument(user4);
            controller.createNewDocument(user5);
        } catch (IOException e) {
            e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(5);

        List<User> searchResultList = null;
    }

    @Test
    public void testMultiSearch() {

    }

    @Test
    public void testExistsProfile() {

    }

    @AfterClass
    public static void oneTimeTearDown() {
        controller.shutDown();
    }
}
