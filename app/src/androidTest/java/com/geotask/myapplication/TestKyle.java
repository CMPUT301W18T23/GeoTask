package com.geotask.myapplication;

import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.geotask.myapplication.AbstractGeoTaskActivity.setCurrentUser;


@RunWith(AndroidJUnit4.class)
public class TestKyle implements AsyncCallBackManager {
    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    @Before
    public void setUp() {
        User user = new User("kylerequest", "kyle.google@org", "9111234");
        setCurrentUser(user);
    }
    @Test
    public void addData(){

        User user = new User("kylerequest", "kyle.google@org", "9111234");
        setCurrentUser(user);

        User provider1 = new User("kyleprovide1", "kyle.yahoo@org", "1231233");
        User provider2  = new User("kyleprovide2", "kyle.weehaw@org", "11912312");
        Task task = new Task("randomid","kylestast", "do it for me", "12");
        Bid bid1;
        Bid bid2;

        bid1 = new Bid(provider1.getObjectID(), 99.96, task.getObjectID());
        bid2 = new Bid(provider2.getObjectID(), 4.9, task.getObjectID());

        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user,provider1,provider2,task,bid1,bid2);
//        asyncCreateNewDocument.execute(provider1);
//        asyncCreateNewDocument.execute(provider2);
//        asyncCreateNewDocument.execute(task);

//        bid1 = new Bid(provider1.getObjectID(), 99.96, task.getObjectID());
//        bid2 = new Bid(provider2.getObjectID(), 4.9, task.getObjectID());

//        asyncCreateNewDocument.execute(bid1);
//        asyncCreateNewDocument.execute(bid2);



        assert(bid1.getDate() > 0);

    }

    @After
    public void tearDown() {
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
