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
import com.geotask.myapplication.LoginActivity;
import com.geotask.myapplication.R;
import com.geotask.myapplication.TestServerAddress;

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
public class TestStory6TaskAssignmentInteractions {

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
    }
    @Rule
    public ActivityTestRule<LoginActivity> LoginActivityTestRule =
            new ActivityTestRule<>(LoginActivity.class, false, false);

    //6.a
    //part 5
    @Test
    public void testViewMyAcceptedTasksAsProvider() {

        String name1 = "kehan1";
        String name2 = "kehan2";

        String email1= "kehan1@ualberta.ca";
        String email2 = "kehan2@ualberta.ca";

        String phone1 = "7808858151";
        String phone2 = "7808828152";

        //byte[] photo = "photo";

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.newName))
                .perform(clearText(),typeText(name1),closeSoftKeyboard());
        onView(withId(R.id.newPhone))
                .perform(clearText(),typeText(phone1),closeSoftKeyboard());
        onView(withId(R.id.newEmail))
                .perform(clearText(),typeText(email1),closeSoftKeyboard());
        onView(withId(R.id.newSave))
                .perform(click());

        onView(withId(R.id.emailText))
                .perform(clearText(),typeText(email1),closeSoftKeyboard());

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));

    }

    //6.b
    //part 5
    @Test
    public void testViewMyAssignedTasksAsRequesters() {

        String name1 = "kehan1";
        String name2 = "kehan2";

        String email1= "kehan1@ualberta.ca";
        String email2 = "kehan2@ualberta.ca";

        String phone1 = "7808858151";
        String phone2 = "7808828152";

        //byte[] photo = "photo";

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.newName))
                .perform(clearText(),typeText(name1),closeSoftKeyboard());
        onView(withId(R.id.newPhone))
                .perform(clearText(),typeText(phone1),closeSoftKeyboard());
        onView(withId(R.id.newEmail))
                .perform(clearText(),typeText(email1),closeSoftKeyboard());
        onView(withId(R.id.newSave))
                .perform(click());

        onView(withId(R.id.emailText))
                .perform(clearText(),typeText(email1),closeSoftKeyboard());

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));



    }
}
