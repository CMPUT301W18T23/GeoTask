package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.Helpers.AsyncArgumentWrapper;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.Controllers.MasterController;
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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class TestSync implements AsyncCallBackManager {

    private LocalDataBase database;

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule = new ActivityTestRule<>(MenuActivity.class);

    @Before
    public void setUp() {
        MasterController.verifySettings(InstrumentationRegistry.getContext());
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }

        database = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        database.bidDAO().delete();
        database.taskDAO().delete();
        database.userDAO().delete();
    }

    @Test
    public void testSync() throws InterruptedException {
        String userId = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(userId);
        Task task = new Task("testSync","testSync", "testSync");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getContext());
        asyncCreateNewDocument.execute(user, task);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        Thread.sleep(5000);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getContext());
        asyncGetDocument.execute(new AsyncArgumentWrapper(task.getObjectID(), Task.class));

        Task result = null;
        try {
            result = (Task) asyncGetDocument.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        assertNotNull(result);
        assertEquals(task.getObjectID(), result.getObjectID());
    }

    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }
}
