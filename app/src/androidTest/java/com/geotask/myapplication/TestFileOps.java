package com.geotask.myapplication;

import android.database.sqlite.SQLiteConstraintException;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.geotask.myapplication.Controllers.LocalFilesOps.BidDatabaseController;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class TestFileOps {

    private LocalDataBase dataBase;

    @Before
    public void setUp() {
        dataBase = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        dataBase.bidDAO().delete();
        Log.i("rows deleted from tasks", Integer.toString(dataBase.taskDAO().delete()));

        dataBase.userDAO().delete();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testWriteAndReadBid() {
        String targetProvider = "database_bid_test";
        Bid bid = new Bid(targetProvider, 1.1, "database test");
        bid.setObjectID("1");
        dataBase.bidDAO().insert(bid);

        Log.i("dat result2", bid.toString());

        List<Bid> resultList = dataBase.bidDAO().selectByProvider(targetProvider);

        Log.i("dat result22", resultList.toString());
        assertEquals(bid.getProviderID(), resultList.get(0).getProviderID());
    }

    @Test
    public void testWriteAndReadTask() {
        Bid bid = new Bid("testtest", 1.1, "database test");
        bid.setObjectID("bid1");

        String targetName = "database_task_test";
        Task task = new Task(targetName, "test description1");
        task.setObjectID("2");
        task.addBid(bid);
        dataBase.taskDAO().insert(task);

        Log.i("dat result212", task.toString());

        List<Task> resultList = dataBase.taskDAO().selectByName(targetName);

        Log.i("dat result2212", resultList.toString());
        assertEquals(task.getName(), resultList.get(0).getName());
    }

    @Test
    public void testWriteAndReadUser() {
        String targetName = "database_user_test";
        User user = new User(targetName, "eaa@gmail.com", "2342342");
        user.setObjectID("2");
        dataBase.userDAO().insert(user);

        List<User> resultList = dataBase.userDAO().selectByName(targetName);

        assertEquals(user.getName(), resultList.get(0).getName());
    }

    @Test
    public void primaryKeyViolationTestAndViolationShouldReplaceOldRowWithNew() {
        User user1 = new User("primary key test", "eaa@gmail.com", "2342342");
        user1.setObjectID("2");
        String target = "violator";
        User user2 = new User(target, "adsga@gmail.com", "3498723");
        user2.setObjectID("2");

        dataBase.userDAO().insert(user1);
        try {
            dataBase.userDAO().insert(user2);
        } catch(SQLiteConstraintException constraint) {
            Log.i("SQLiteConstraintException", constraint.toString());
        }

        List<User> resultList = dataBase.userDAO().selectAll();

        assertEquals(1, resultList.size());
        assertEquals(target, resultList.get(0).getName());
    }

    @Test
    public void testUpdate() {
        User user1 = new User("update test", "update@gmail.com", "2342342");
        user1.setObjectID("2");

        dataBase.userDAO().insert(user1);

        String target = "update";
        user1.setName(target);

        dataBase.userDAO().update(user1);

        List<User> resultList = dataBase.userDAO().selectAll();

        assertEquals(1, resultList.size());
        assertEquals(target, resultList.get(0).getName());
    }

    @Test
    public void testDeleteSingleRow() {
        Bid bid = new Bid("delete single test", 1.1, "delete single");

        dataBase.bidDAO().insert(bid);
        List<Bid> resultList = dataBase.bidDAO().selectAll();

        assertEquals(1, resultList.size());


        dataBase.bidDAO().delete(bid);
        resultList = dataBase.bidDAO().selectAll();

        assertEquals(0, resultList.size());
    }

    @Test
    public void testInsertMultiple() {
        Bid bid1 = new Bid("multi test", 6.1, "multi 1");
        bid1.setId(1);
        Bid bid2 = new Bid("multi test", 6.2, "multi 2");
        bid2.setId(2);
        Bid bid3 = new Bid("multi test", 6.3, "multi 3");
        bid3.setId(3);

        dataBase.bidDAO().insertMultiple(bid1, bid2, bid3);

        List<Bid> resultList = dataBase.bidDAO().selectByProvider("multi test");

        assertEquals(3, resultList.size());
    }

    @Test
    public void testInsertMultipleWithPrimaryKeyUniquenessViolation() {
        Bid bid1 = new Bid("multi test", 6.1, "multi 1");
        bid1.setId(1);
        Bid bid2 = new Bid("multi test", 6.2, "multi 2");
        bid2.setId(2);
        String target = "multi 3";
        Bid bid3 = new Bid("multi test", 6.3, target);
        bid3.setId(2);

        dataBase.bidDAO().insertMultiple(bid1, bid2, bid3);

        List<Bid> resultList = dataBase.bidDAO().selectByProvider("multi test");

        assertEquals(2, resultList.size());
        assertEquals(target, resultList.get(1).getTaskID());
    }
}
