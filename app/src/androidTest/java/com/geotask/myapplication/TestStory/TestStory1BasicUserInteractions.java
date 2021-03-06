package com.geotask.myapplication.TestStory;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.geotask.myapplication.AddTaskActivity;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.LoginActivity;
import com.geotask.myapplication.MenuActivity;
import com.geotask.myapplication.R;
import com.geotask.myapplication.TestServerAddress;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class TestStory1BasicUserInteractions {

    @Rule
    public ActivityTestRule<LoginActivity> testLogin = new ActivityTestRule<LoginActivity>(LoginActivity.class, true , false);
    @Rule
    public ActivityTestRule<AddTaskActivity> testaddTask = new ActivityTestRule<AddTaskActivity>(AddTaskActivity.class, true , false);
    @Rule
    public ActivityTestRule<MenuActivity> testMenu = new ActivityTestRule<MenuActivity>(MenuActivity.class, true, false);

    LocalDataBase database;

    @BeforeClass
    public static void oneTimeSetUp() {
        MasterController.verifySettings(InstrumentationRegistry.getTargetContext());
        MasterController.setTestSettings(TestServerAddress.getTestAddress());
        try {
            MasterController.deleteIndex();
            MasterController.createIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] img = new byte[0];
        User user = new User(img,"KehanWang", "kehan1@ualberta.ca", "7808858151");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(user);
    }

    @Before
    public void setUp() {
        database = LocalDataBase.getDatabase(InstrumentationRegistry.getTargetContext());
        database.taskDAO().delete();
        database.bidDAO().delete();
        database.userDAO().delete();
    }

    @After
    public void tearDown() {
        //database.close();
    }

    //1.a
    @Test
    public void testLogin() {
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, LoginActivity.class);
        testLogin.launchActivity(result);

        onView(ViewMatchers.withId(R.id.emailText))
                .perform(clearText(),typeText("kehan1@ualberta.ca"),closeSoftKeyboard());

        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withText("UPDATE"));

    }

    //1.b
    @Test
    public void testRequestNewTask() throws InterruptedException {
        byte[] image = new byte[0];
        User user = new User(image,"KehanWang", "kehan1@ualberta.ca", "7808858151");
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, MenuActivity.class);
        testaddTask.launchActivity(result);
        testaddTask.getActivity().setCurrentUser(user);

        onView((withId(R.id.fab)))
                .perform(click());

        onView(withId(R.id.TaskTitle))
                .perform(clearText(),typeText("cmput301 project."),closeSoftKeyboard());
        onView(withId(R.id.TaskDescription))
                .perform(clearText(),typeText("Project 4 user case test bugs."),closeSoftKeyboard());
        onView(withId(R.id.TaskSave))
                .perform(click());

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_all));

        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());

    }

    //1.c
    @Test
    public void testViewMyTasksAsRequester() throws InterruptedException {
        String userId = "requester";
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        user.setObjectID(userId);
        Task task = new Task (userId,"Cmput301", "TestStory1 bugs");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(user, task);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, MenuActivity.class);
        testMenu.launchActivity(result);
        testMenu.getActivity().setCurrentTask(task);
        testMenu.getActivity().setCurrentUser(user);

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_requester));
        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        pressBack();
    }

    //1.d
    @Test
    public void testDeleteTask() throws InterruptedException {
        String userId = "requester";
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        user.setObjectID(userId);
        Task task = new Task (userId,"Cmput301", "TestStory1 bugs");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(user, task);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, MenuActivity.class);
        testMenu.getActivity().setCurrentUser(user);
        testMenu.getActivity().setCurrentTask(task);
        testMenu.launchActivity(result);

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_requester));

        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        Thread.sleep(1000);

        //onView(withId(R.id.editTaskButton)).perform(click());
        onView(withId(R.id.action_delete)).perform(click());
    }

    //1.e
    @Test
    public void testLogout() throws InterruptedException {
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, MenuActivity.class);
        testMenu.launchActivity(result);
        testMenu.getActivity().setCurrentUser(user);

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_logout));
        onView(withId(R.id.emailText)).perform(click());
    }

    //1.f
    @Test
    public void testViewAllAvailableTasks() throws InterruptedException {
        String userId = "requester";
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        user.setObjectID(userId);
        Task task = new Task (userId,"Cmput301", "TestStory1 bugs");
        Task task2 = new Task ("provider", "Cmput3012", "test view all availble tasks");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(user, task,task2);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, MenuActivity.class);
        testMenu.launchActivity(result);
        testMenu.getActivity().setCurrentUser(user);
        testMenu.getActivity().setCurrentTask(task2);


        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_all));

        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        pressBack();
        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(1).perform(click());

    }

    //1.g
    @Test
    public void testViewAllTasksOnMap() {

    }
}
