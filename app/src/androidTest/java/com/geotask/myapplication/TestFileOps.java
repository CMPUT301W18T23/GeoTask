package com.geotask.myapplication;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestFileOps implements AsyncCallBackManager{

    private LocalDataBase dataBase;

    @Before
    public void setUp() {
        dataBase = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        dataBase.bidDAO().delete();
        dataBase.taskDAO().delete();
        dataBase.userDAO().delete();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testWriteAndReadBid() {
        Bid bid = new Bid("testWriteAndReadBid", 1.1, "testWriteAndReadBid");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(bid);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        Bid result = null;
        try {
            result = (Bid) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(bid.getObjectID(), result.getObjectID());
    }

    @Test
    public void testWriteAndReadTask() throws InterruptedException {
        Bid bid = new Bid("testWriteAndReadTask", 1.1, "testWriteAndReadTask");

        Task task = new Task("testWriteAndReadTask",
                "testWriteAndReadTask",
                "testWriteAndReadTask");
        task.addBid(bid);

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(task);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        Task result = null;
        try {
            result = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(task.getObjectID(), result.getObjectID());
    }

    @Test
    public void testWriteAndReadUser() {
        User user = new User("testWriteAndReadUser", "eaa@gmail.com", "2342342");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(user);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(user.getObjectID(), User.class));

        User result = null;
        try {
            result = (User) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(user.getObjectID(), result.getObjectID());
    }

    @Test
    public void testPrimaryKeyViolationAndViolationShouldReplaceOldRowWithNew() throws InterruptedException {
        User user1 = new User("primary key test", "eaa@gmail.com", "2342342");
        String target = "violator";
        User user2 = new User(target, "adsga@gmail.com", "3498723");
        user2.setObjectID(user1.getObjectID());

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(user1, user2);

        Thread.sleep(1000);

        List<User> resultList = dataBase.userDAO().selectAll();

        assertEquals(1, resultList.size());
        assertEquals(target, resultList.get(0).getName());
    }

    @Test
    public void testUpdate() {
        User user1 = new User("update test", "update@gmail.com", "2342342");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(user1);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(user1.getObjectID(), User.class));

        User result = null;
        try {
            result = (User) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(user1.getName(), result.getName());

        String target = "update";
        result.setName(target);
        user1.setName(target);

        MasterController.AsyncUpdateDocument asyncUpdateDocument =
                new MasterController.AsyncUpdateDocument(InstrumentationRegistry.getContext());
        asyncUpdateDocument.execute(result);

        MasterController.AsyncGetDocument asyncGetDocument2 =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument2.execute(new AsyncArgumentWrapper(user1.getObjectID(), User.class));

        User result2 =null;
        try {
            result2 = (User) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(user1.getObjectID(), result2.getObjectID());
        assertEquals(target, result2.getName());
    }

    @Test
    public void testDeleteSingleRow() {
        Bid bid = new Bid("delete single test", 1.1, "delete single");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(bid);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        Bid result = null;
        try {
            result = (Bid) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(bid.getObjectID(), result.getObjectID());

        MasterController.AsyncDeleteDocument asyncDeleteDocument =
                new MasterController.AsyncDeleteDocument(InstrumentationRegistry.getContext());
        asyncDeleteDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument2 =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument2.execute(bid);

        try {
            assertEquals(null, asyncCreateNewDocument2.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInsertMultiple() {
        Bid bid1 = new Bid("multi test", 6.1, "multi 1");
        Bid bid2 = new Bid("multi test", 6.2, "multi 2");
        Bid bid3 = new Bid("multi test", 6.3, "multi 3");

        dataBase.bidDAO().insertMultiple(bid1, bid2, bid3);

        List<Bid> resultList = dataBase.bidDAO().selectByProvider("multi test");

        assertEquals(3, resultList.size());
    }

    @Test
    public void testInsertMultipleWithPrimaryKeyUniquenessViolation() {
        Bid bid1 = new Bid("multi test", 6.1, "multi 1");
        Bid bid2 = new Bid("multi test", 6.2, "multi 2");
        String target = "multi 3";
        Bid bid3 = new Bid("multi test", 6.3, target);
        bid3.setObjectID(bid2.getObjectID());

        dataBase.bidDAO().insertMultiple(bid1, bid2, bid3);

        List<Bid> resultList = dataBase.bidDAO().selectAll();
        assertEquals(2, resultList.size());

        resultList = dataBase.bidDAO().selectByTask(target);
        assertEquals(1, resultList.size());
        assertEquals(target, resultList.get(0).getTaskID());
    }

    @Test
    public void testSearchQuery() {
        Bid bid = new Bid("testSearchQuery", 3.2, "testSearchQuery");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(bid);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(bid.getObjectID(), Bid.class));

        Bid result = null;
        try {
            result = (Bid) asyncGetDocument.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(bid.getObjectID(), result.getObjectID());

        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[]{"object_id"});

        String[] object = new String[]{bid.getObjectID()};
        builder.addParameters(object);

        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this, InstrumentationRegistry.getContext());
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> resultList = null;
        try {
            resultList = (List<Bid>) asyncSearch.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(1, resultList.size());
        assertEquals(bid.getObjectID(), resultList.get(0).getObjectID());
    }

    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }
}
