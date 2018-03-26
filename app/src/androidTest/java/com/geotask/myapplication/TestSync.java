package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.ElasticsearchController;
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
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TestSync implements AsyncCallBackManager {

    private LocalDataBase database;
    private ElasticsearchController controller = new ElasticsearchController();

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule =
            new ActivityTestRule<>(MenuActivity.class, false, false);

    @Before
    public void setUp() {
        controller.verifySettings();
        MasterController.verifySettings(InstrumentationRegistry.getTargetContext());
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
    public void testSync() throws InterruptedException, IOException {
        assertEquals(0, database.taskDAO().selectAll().size());
        assertEquals(0, database.bidDAO().selectAll().size());

        String userId = "testSync";
        User user = new User("testSync", "testSync", "testSync");
        user.setObjectID(userId);
        Task task = new Task("testSync","testSync", "testSync");

        controller.createNewDocument(user);
        controller.createNewDocument(task);


        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, MenuActivity.class);
        menuActivityRule.getActivity().setCurrentUser(user);
        menuActivityRule.launchActivity(intent);

        TimeUnit.SECONDS.sleep(10);

        Task local = database.taskDAO().selectByID(task.getObjectID());
        Log.d("syncadapter", "selection");
        assertNotNull(local);

        MasterController.AsyncGetDocument asyncGetDocument =
                new MasterController.AsyncGetDocument(this, InstrumentationRegistry.getTargetContext());
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
