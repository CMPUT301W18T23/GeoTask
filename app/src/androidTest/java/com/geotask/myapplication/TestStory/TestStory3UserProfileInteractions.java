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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.startsWith;

@RunWith(AndroidJUnit4.class)
public class TestStory3UserProfileInteractions {

    @BeforeClass
    public static void oneTimeSetUp() {
        MasterController.verifySettings(InstrumentationRegistry.getContext());
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

    //3.a
    @Test
    public void testProfileRegistration() {
        String newname = "testUserName";
        String newphone = "123456789";
        String newemail = "testEmail";

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);

        onView(ViewMatchers.withId(R.id.registerButton)).perform(click());
        onView(withId(R.id.newName)).perform(replaceText(newname));
        onView(withId(R.id.newPhone)).perform(replaceText(newphone));
        onView(withId(R.id.newEmail)).perform(replaceText(newemail));
        onView(withId(R.id.newSave)).perform(click());

        onView(withId(R.id.emailText)).perform(replaceText(newemail));
        onView(withId(R.id.loginButton)).perform(click());
    }

    //3.b
    @Test
    public void testEditProfile() {
        String newemail = "testEmail";

        Context targetContext =
                InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intent intent = new Intent(targetContext, LoginActivity.class);
        LoginActivityTestRule.launchActivity(intent);

        onView(withId(R.id.emailText)).perform(replaceText(newemail));
        onView(withId(R.id.loginButton)).perform(click());
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));

        onView(withId(R.id.UserName)).perform(replaceText("name2"));
        onView(withId(R.id.UserEmail)).perform(replaceText("email2"));
        onView(withId(R.id.UserPhone)).perform(replaceText("phone2"));

        onView(withId(R.id.SaveEdit)).perform(click());

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_profile));
        onView(withId(R.id.UserName)).check(matches(withText(startsWith("name2"))));
        onView(withId(R.id.UserEmail)).check(matches(withText(startsWith("email2"))));
        onView(withId(R.id.UserPhone)).check(matches(withText(startsWith("phone2"))));
    }

    //3.c
    //part 5
    @Test
    public void testViewAnotherProfile() {

    }
}
