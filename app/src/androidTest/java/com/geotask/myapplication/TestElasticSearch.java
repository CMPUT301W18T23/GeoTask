package com.geotask.myapplication;

import android.support.test.InstrumentationRegistry;
import android.util.Log;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.ElasticsearchController;
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

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class TestElasticSearch implements AsyncCallBackManager {

    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    ElasticsearchController controller = new ElasticsearchController();

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @Before
    public void setUp() {
        MasterController.verifySettings(InstrumentationRegistry.getTargetContext());
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAsyncCreateAndGetBid() throws Exception {
        String test = "testAsyncCreateAndGetBid";
        Bid bid = new Bid(test,1.1,test);
        controller.createNewDocument(bid);

        Bid remote = (Bid) controller.getDocument(bid.getObjectID(), Bid.class);

        assertEquals(bid.getProviderID(), remote.getProviderID());
    }

    @Test
    public void testAsyncCreateAndGetTask() throws Exception {
        String test = "testAsyncCreateAndGetTask";
        Task task = new Task(test,test,test);
        controller.createNewDocument(task);

        Task remote = (Task) controller.getDocument(task.getObjectID(), Task.class);

        assertEquals(task.getDate(), remote.getDate());
    }

    @Test
    public void testCreateAndGetUser() throws Exception {
        String test = "testCreateAndGetUser";
        User user = new User(test,test,test);
        controller.createNewDocument(user);

        User remote = (User) controller.getDocument(user.getObjectID(), User.class);

        assertEquals(user.getName(), remote.getName());
    }

    @Test
    public void testDeleteDocument() throws Exception {
        String test = "testDeleteDocument";
        Bid bid = new Bid(test, 1.2, test);
        controller.createNewDocument(bid);

        Bid remote = (Bid) controller.getDocument(bid.getObjectID(), Bid.class);
        assertEquals(bid.getProviderID(), remote.getProviderID());

        controller.deleteDocument(bid.getObjectID(), Bid.class);

        remote = (Bid) controller.getDocument(bid.getObjectID(), Bid.class);
        assertNull(remote);
    }

    @Test
    public void testUpdateBid() throws Exception {
        String test = "testUpdateBid";
        Bid bid = new Bid(test, 2.1, test);

        controller.createNewDocument(bid);

        Bid remote = (Bid) controller.getDocument(bid.getObjectID(), Bid.class);
        assertEquals(bid.getProviderID(), remote.getProviderID());

        String updateString = "updated";
        bid.setProviderID(updateString);
        controller.updateDocument(bid);

        remote = (Bid) controller.getDocument(bid.getObjectID(), Bid.class);
        assertEquals(updateString, remote.getProviderID());
    }

    @Test
    public void testUpdateTask() throws Exception {
        String test = "testUpdateTask";
        Task task = new Task(test,test,test);
        controller.createNewDocument(task);

        Task remote = (Task) controller.getDocument(task.getObjectID(), Task.class);
        assertEquals(task.getName(), remote.getName());

        String updateString = "updated";
        task.setName(updateString);
        controller.updateDocument(task);

        remote = (Task) controller.getDocument(task.getObjectID(), Task.class);

        assertEquals(updateString, remote.getName());
    }

    @Test
    public void testUpdateUser() throws Exception {
        String test = "testUpdateUser";
        User user = new User(test,test,test);
        controller.createNewDocument(user);

        User remote = (User) controller.getDocument(user.getObjectID(), User.class);
        assertEquals(user.getName(), remote.getName());

        String updateString = "updated";
        user.setName(updateString);
        controller.updateDocument(user);

        remote = (User) controller.getDocument(user.getObjectID(), User.class);

        assertEquals(updateString, remote.getName());
    }

    @Test
    public void testSearchBid() throws InterruptedException, IOException {
        String targetProvider = "targetProvider";
        String targetTask = "targetTask";
        Bid bid1 = new Bid(targetProvider, 3.1, "test");
        Bid bid2 = new Bid(targetProvider, 3.2, targetTask);
        Bid bid3 = new Bid(targetProvider, 3.3, targetTask);

        controller.createNewDocument(bid1);
        controller.createNewDocument(bid2);
        controller.createNewDocument(bid3);
        controller.refresh();

        SuperBooleanBuilder searchTask = new SuperBooleanBuilder();
        searchTask.put("taskID", targetTask.toLowerCase());
        SuperBooleanBuilder searchProvider = new SuperBooleanBuilder();
        searchProvider.put("providerID", targetProvider.toLowerCase());

        List<Bid> resultTask = (List<Bid>) controller.search(searchTask.toString(), Bid.class);
        List<Bid> resultProvider = (List<Bid>) controller.search(searchProvider.toString(), Bid.class);
        Log.d("123123123", searchTask.toString() + "%%%" + searchProvider.toString());

        assertEquals(3, resultProvider.size());
        assertEquals(2, resultTask.size());
    }

    @Test
    public void testSearchTasks() throws InterruptedException, IOException {
        Task task1 = new Task("random id","test search task", "test search task");
        task1.setAcceptedBid(1.1);
        String target = "targettargettarget";
        String targetName = "task2";
        Task task2 = new Task("random ID", targetName, target);
        task2.setAcceptedBid(1.2);

        controller.createNewDocument(task1);
        controller.createNewDocument(task2);
        controller.refresh();

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("description", target);
        Log.d("123123123", builder1.toString());

        List<Task> result = (List<Task>) controller.search(builder1.toString(), Task.class);

        assertEquals(1, result.size());
        assertEquals(targetName, result.get(0).getName());
    }

    @Test
    public void testSearchUsers() throws InterruptedException, IOException {
        String targetUser = "target";
        User user1 = new User("user", "1@gmail.com", "555");
        User user2 = new User("user", "2@gmail.com", "666");
        User user3 = new User("user", "1@gmail.com", "777");
        User user4 = new User("user", "1@yahoo.com", "888");
        User user5 = new User(targetUser, "1@yahoo.ca", "888");

        controller.createNewDocument(user1);
        controller.createNewDocument(user2);
        controller.createNewDocument(user3);
        controller.createNewDocument(user4);
        controller.createNewDocument(user5);
        controller.refresh();

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("phonenum", "888");
        SuperBooleanBuilder builder2 = new SuperBooleanBuilder();
        builder2.put("email", "1@yahoo.ca");

        List<User> result1 = (List<User>) controller.search(builder1.toString(), User.class);
        List<User> result2 = (List<User>) controller.search(builder2.toString(), User.class);

        assertEquals(2, result1.size());
        assertEquals(1, result2.size());
        assertEquals(targetUser, result2.get(0).getName());
    }

    @Test
    public void testSearchReturnMoreThanTenItems() throws InterruptedException, IOException {
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

        controller.createNewDocument(user1);
        controller.createNewDocument(user2);
        controller.createNewDocument(user3);
        controller.createNewDocument(user4);
        controller.createNewDocument(user5);
        controller.createNewDocument(user6);
        controller.createNewDocument(user7);
        controller.createNewDocument(user8);
        controller.createNewDocument(user9);
        controller.createNewDocument(user10);
        controller.createNewDocument(user11);
        controller.createNewDocument(user12);
        controller.createNewDocument(user13);
        controller.createNewDocument(user14);
        controller.createNewDocument(user15);
        controller.refresh();

        SuperBooleanBuilder builder1 = new SuperBooleanBuilder();
        builder1.put("name", "user");

        List<User> result1 = (List<User>) controller.search(builder1.toString(), User.class);

        assertEquals(15, result1.size());
    }

    @Test
    public void testExistsProfile() throws InterruptedException, IOException {
        assertTrue(MasterController.existsProfile("kyleg@email.com") == null);

        User user1 = new User("Kyle1", "kyleg@email.com", "555");
        controller.createNewDocument(user1);

        assertFalse(controller.existsProfile("kyleg@email.com") == null);
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
