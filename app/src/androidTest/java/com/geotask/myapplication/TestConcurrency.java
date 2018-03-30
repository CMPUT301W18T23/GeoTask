package com.geotask.myapplication;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class TestConcurrency implements AsyncCallBackManager{
    static ElasticsearchController controller;

    @BeforeClass
    public static void oneTimeSetUp() {
        controller = new ElasticsearchController();
        controller.verifySettings();
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
    public void testEqualityBetweenLocalTaskAndRemoteTask() throws Exception {
        Task taskLocal = new Task("testMapping", "testMapping", "testMapping");
        controller.createNewDocument(taskLocal);

        Task taskRemote = (Task) controller.getDocument(taskLocal.getObjectID(), Task.class);

        taskRemote.setDescription("asdgasdgasdg");
        taskRemote.setAcceptedProviderID("234234");

        assertEquals(taskLocal, taskRemote);
        assertFalse(taskLocal == taskRemote);

        taskRemote.setObjectID("asdgasdgasdg");
        assertThat(taskLocal, not(taskRemote));
    }

    @Test
    public void testListMembershipRemoteListShouldExistInLocalList() throws IOException, InterruptedException {
        Task task1 = new Task("testListMembership1", "testListMembership", "testListMembership1");
        Task task2 = new Task("testListMembership2", "testListMembership", "testListMembership2");
        Task task3 = new Task("testListMembership3", "testListMembership1", "testListMembership3");

        List<Task> taskList = new ArrayList();
        taskList.add(task1);
        taskList.add(task2);
        taskList.add(task3);
        assertEquals(3, taskList.size());

        controller.createNewDocument(task1);
        controller.createNewDocument(task2);
        controller.createNewDocument(task3);

        Thread.sleep(1000);

        List<Task> remoteTask = (List<Task>) controller.search("", Task.class);

        assertNotNull(remoteTask);
        assertEquals(3, remoteTask.size());
        assertTrue(taskList.contains(remoteTask.get(0)));
    }

    @Test
    public void testIndexingMultipleTimesShouldUpVersionNumber() {
        Task task = new Task("testIndexing", "testIndexing", "testIndexing");

        double code = 0;
        try {
            code = controller.createNewDocument(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(1.0, code);

        try {
            code = controller.createNewDocument(task);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(2.0, code);
    }

    @Override
    public void onPostExecute(GTData data) {

    }

    @Override
    public void onPostExecute(List<? extends GTData> searchResult) {

    }
}
