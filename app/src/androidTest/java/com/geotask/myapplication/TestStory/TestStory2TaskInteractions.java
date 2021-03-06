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

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.LoginActivity;
import com.geotask.myapplication.R;
import com.geotask.myapplication.TestServerAddress;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
public class TestStory2TaskInteractions {

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
        String newemail = "kehan1@ualberta.ca";
        String newname = "KehanWang";
        String newphone = "7808858151";


        User user = new User(newname, newemail, newphone);
        Task task = new Task(user.getObjectID(),
                "testViewTask",
                "testViewTaskDesc");


        MasterController.AsyncCreateNewDocument asyncCreateNewDocument =
                new MasterController.AsyncCreateNewDocument(InstrumentationRegistry.getTargetContext());
        asyncCreateNewDocument.execute(task, user);

    }


    private ActivityTestRule<LoginActivity> LoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class, false, false);

    //2.a
    @Test
    public void testViewTaskDetail() throws InterruptedException {

        String newemail = "kehan1@ualberta.ca";
        String newname = "KehanWang";
        String title = "testViewTask";
        String desc = "testViewTaskDesc";


        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);
        onView(ViewMatchers.withId(R.id.emailText)).perform(clearText(),typeText(newemail),closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_requester));

        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());
        onView(withId(R.id.textViewTitle)).check(matches(withText(title)));
        onView(withId(R.id.textViewName)).check(matches(withText(containsString(newname))));
        onView(withId(R.id.textViewDescription)).check(matches(withText(startsWith(desc))));
    }

    //2.b
    @Test
    public void testEditTaskDetail() throws InterruptedException {

        String newemail = "kehan1@ualberta.ca";
        String newname = "KehanWang";
        String title = "testViewTask";
        String desc = "testViewTaskDesc";

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);


        onView(withId(R.id.emailText)).perform(clearText(),typeText(newemail),closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_requester));
        onData(anything()).inAdapterView(withId(R.id.taskListView)).atPosition(0).perform(click());

        onView(withId(R.id.action_edit)).perform(click());

        onView(withId(R.id.editTitle)).perform(replaceText(title));
        onView(withId(R.id.editDescription)).perform(replaceText(desc));

        onView(withId(R.id.editButton)).perform(click());

        //onView(withId(R.id.editTaskButton)).perform(click());

        onView(withId(R.id.textViewTitle)).check(matches(withText(startsWith(title))));
        onView(withId(R.id.textViewDescription)).check(matches(withText(startsWith(desc))));
        Thread.sleep(5000);

        pressBack();

//        onView(withId(R.id.drawer_layout))
//                .check(matches(isClosed(Gravity.LEFT)))
//                .perform(DrawerActions.open());
//        onView(withId(R.id.nav_view))
//                .perform(NavigationViewActions.navigateTo(R.id.nav_logout));
//        onView(withId(R.id.emailText)).perform(click());
    }
}

