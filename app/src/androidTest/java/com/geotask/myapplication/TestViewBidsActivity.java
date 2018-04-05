package com.geotask.myapplication;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class TestViewBidsActivity implements AsyncCallBackManager {

    private GTData data = null;
    private List<? extends GTData> searchResult = null;
    private User user = new User("kylerequest", "kyle.google@org", "911");
    private User provider1 = new User("kyleprovide1", "kyle.yahoo@org", "191");
    private User provider2  = new User("kyleprovide2", "kyle.weehaw@org", "119");
    private Task task = new Task(user.getObjectID(), "kylestast", "do it for me", "123142341243");
    private Bid bid1;
    private Bid bid2;


    @Rule
    public ActivityTestRule<ViewBidsActivity> viewBidsActivity = new ActivityTestRule<ViewBidsActivity>(ViewBidsActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(InstrumentationRegistry.getTargetContext(), ViewBidsActivity.class);
            intent.putExtra("currentUser", user);
            intent.putExtra("task", task);
            return intent;
        }
    };


    @Before
    public void setUp() throws InterruptedException {
        MasterController.verifySettings();
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);
        asyncCreateNewDocument.execute(provider1);
        asyncCreateNewDocument.execute(provider2);
        asyncCreateNewDocument.execute(task);

        bid1 = new Bid(provider1.getObjectID(), 99.96, task.getObjectID());
        bid2 = new Bid(provider2.getObjectID(), 4.9, task.getObjectID());

        asyncCreateNewDocument.execute(bid1);
        asyncCreateNewDocument.execute(bid2);

        viewBidsActivity.getActivity().getSupportFragmentManager().beginTransaction();
        //viewBidsActivity.getActivity().setForTest();
        TimeUnit.SECONDS.sleep(3);

    }

    @Test
    public void BasicTestConnection() throws InterruptedException {
        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this);
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));
        Thread.sleep(2000);
        Task remote = null;
        try {
            remote = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertEquals(task.getDate(), remote.getDate());
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