package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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
    }

    private ActivityTestRule<LoginActivity> LoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class, false, false);

    //2.a
    @Test
    public void testViewTaskDetail() {
        String newname = "testUserName";
        String newphone = "123456789";
        String newemail = "testEmail";

        Task task = new Task(newemail,
                "testViewTask",
                "testViewTaskDesc");

        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument();
        asyncCreateNewDocument.execute(task);

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);

        onView(withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.newName)).perform(replaceText(newname));
        onView(withId(R.id.newPhone)).perform(replaceText(newphone));
        onView(withId(R.id.newEmail)).perform(replaceText(newemail));
        onView(withId(R.id.newSave)).perform(click());
        onView(withId(R.id.emailText)).perform(replaceText(newemail));
        onView(withId(R.id.loginButton)).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //2.b
    @Test
    public void testEditTaskDetail() {

    }
}
