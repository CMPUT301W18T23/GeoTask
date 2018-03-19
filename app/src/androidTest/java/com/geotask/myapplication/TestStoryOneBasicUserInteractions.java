package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.User;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class TestStoryOneBasicUserInteractions {

    @Rule
    public ActivityTestRule<LoginActivity> testLogin = new ActivityTestRule<LoginActivity>(LoginActivity.class, true , false);
    @Rule
    public ActivityTestRule<AddTaskActivity> testaddTask = new ActivityTestRule<AddTaskActivity>(AddTaskActivity.class, true , false);
    @Rule
    public ActivityTestRule<MenuActivity> testMenu = new ActivityTestRule<MenuActivity>(MenuActivity.class, true, false);


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
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        MasterController.AsyncCreateNewDocument asyncCreateNewDocument
                = new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(user);

    }

    //1.a
    @Test
    public void testLogin() {
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, LoginActivity.class);
        testLogin.launchActivity(result);

        onView(withId(R.id.emailText))
                .perform(clearText(),typeText("kehan1@ualberta.ca"),closeSoftKeyboard());

        onView(withId(R.id.loginButton))
                .perform(click());

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

    }

    //1.b
    @Test
    public void testRequestNewTask() {
        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, AddTaskActivity.class);
        result.putExtra("currentUser", user);
        testaddTask.launchActivity(result);

        onView(withId(R.id.TaskTitle))
                .perform(clearText(),typeText("cmput301 project."),closeSoftKeyboard());
        onView(withId(R.id.TaskDescription))
                .perform(clearText(),typeText("Project 4 user case test bugs."),closeSoftKeyboard());

        onView(withId(R.id.TaskSave))
                .perform(click());

    }

    //1.c
    @Test
    public void testViewMyTasksAsRequester() throws InterruptedException {

        User user = new User("KehanWang", "kehan1@ualberta.ca", "7808858151");
        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent result = new Intent(targetContext, MenuActivity.class);
        result.putExtra("currentUser", user);
        testMenu.launchActivity(result);








    }

    //1.d
    @Test
    public void testDeleteTask() {




    }

    //1.e
    @Test
    public void testLogout() {

    }

    //1.f
    @Test
    public void testViewAllAvailableTasks() {

    }

    //1.g
    @Test
    public void testViewAllTasksOnMap() {

    }
}
