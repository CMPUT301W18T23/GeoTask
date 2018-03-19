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
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class TestStoryTwoTaskInteractions {

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

        String newemail = "testEmail";
        String newname = "testUserName";
        String newphone = "123456789";


        User user = new User(newname, newemail, newphone);
        Task task = new Task(user.getObjectID(),
                "testViewTask",
                "testViewTaskDesc");


        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task, user);


    }

    private ActivityTestRule<LoginActivity> LoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class, false, false);

    //2.a
    @Test
    public void testViewTaskDetail() throws InterruptedException {

        String newemail = "testEmail";
        String newname = "testUserName";
        String title = "testViewTask";
        String desc = "testViewTaskDesc";


        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);
        onView(withId(R.id.emailText)).perform(clearText(),typeText(newemail),closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());




        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        onView(withId(R.id.textViewTitle)).check(matches(withText(startsWith(title))));
        onView(withId(R.id.textViewName)).check(matches(withText(startsWith(newname))));
        onView(withId(R.id.textViewDescription)).check(matches(withText(startsWith(desc))));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //2.b
    @Test
    public void testEditTaskDetail() throws InterruptedException {


        String newemail = "testEmail";
        String newname = "testUserName";
        String title = "testViewTask";
        String desc = "testViewTaskDesc";




        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);


        onView(withId(R.id.emailText)).perform(clearText(),typeText(newemail),closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());






        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.editTaskButton)).perform(click());

        onView(withId(R.id.editTitle)).perform(replaceText(title));
        onView(withId(R.id.editDescription)).perform(replaceText(desc));

        onView(withId(R.id.editButton)).perform(click());

        onView(withId(R.id.editTaskButton)).perform(click());

        onView(withId(R.id.editTitle)).check(matches(withText(startsWith(title))));
        onView(withId(R.id.editDescription)).check(matches(withText(startsWith(desc))));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
