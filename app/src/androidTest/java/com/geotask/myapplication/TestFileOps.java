package com.geotask.myapplication;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.geotask.myapplication.Controllers.LocalFilesOps.BidDAO_Impl;
import com.geotask.myapplication.Controllers.LocalFilesOps.BidDatabaseController;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Bid;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestFileOps {

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
        dataBase.close();
    }

    @Test
    public void testWriteBid() {
        String targetProvider = "database_test";
        Bid bid = new Bid(targetProvider, 1.1, "database test");
        dataBase.bidDAO().insert(bid);

        Log.i("dat result", bid.toString());

        List<Bid> resultList = dataBase.bidDAO().selectByProvider(targetProvider);

        Log.i("dat result2", resultList.toString());
        assertEquals(bid.getProviderID(), resultList.get(0).getProviderID());
    }

    @Test
    public void testReadBid() {

    }

    @Test
    public void testWriteFileWithNoID() {

    }
}
