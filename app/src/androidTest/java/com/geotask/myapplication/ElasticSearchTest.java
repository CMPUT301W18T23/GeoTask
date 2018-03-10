package com.geotask.myapplication;

import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.EmailConverter;
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
public class ElasticSearchTest implements AsyncCallBackManager {

    private GTData data = null;
    private List<? extends GTData> searchResult = null;

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
        Task task = new Task("test Task", "test Description");

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

    @Test public void testCreateAndGetUser() throws InterruptedException {
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

        bid.setProviderID("updated");
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

        assertEquals(bid.getProviderID(), remote.getProviderID());
    }

    @Test
    public void testUpdateTask() throws InterruptedException {
        Task task = new Task("test update task", "test update description");

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument();
        asyncUpdateDocument.execute(task);

        TimeUnit.SECONDS.sleep(3);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
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
                new MasterController.AsyncGetDocument(this);
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
                new MasterController.AsyncSearch(this);
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
                new MasterController.AsyncSearch(this);
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
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder1.toString(), User.class));
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
    }

    @Test
    public void testEmailConversion(){
        String email = "kyleg@email.com";
        String convertedEmail = EmailConverter.convertEmailForElasticSearch(email);
        String revertedEmail = EmailConverter.revertEmailFromElasticSearch(convertedEmail);
        assert(convertedEmail.compareTo("107c121c108c101c103c64c101c109c97c105c108c46c99c111c109c") == 0);
        assert(email.compareTo(revertedEmail) == 0);
    }

    @After
    public void tearDown() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
        MasterController.shutDown();
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
