package com.geotask.myapplication;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geotask.myapplication.Controllers.MasterController;

import org.junit.After;
import org.junit.Before;
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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by kehanwang on 2018/3/17.
 */
@RunWith(AndroidJUnit4.class)



public class TestRegister {
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

    @Rule
    public ActivityTestRule registerActivity = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void RegisterTest(){
        onView(withId(R.id.newName)).perform(clearText(),typeText("kehan123456"),closeSoftKeyboard());
        // .check(matches(withText("")));

        onView(withId(R.id.newEmail)).perform(clearText(),typeText("kehan1"),closeSoftKeyboard());
        // .check(matches(withText("")));

        onView(withId(R.id.newPhone)).perform(clearText(),typeText("780"),closeSoftKeyboard());
        // .check(matches(withText("")));

        onView(withId(R.id.newSave))
                .perform(click());

        onView(withId(R.id.loginButton)).check(matches(withText("Login")));

    }

    @After
    public void tearDown() {
        MasterController.shutDown();
    }

}
