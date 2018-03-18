package com.geotask.myapplication;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.view.Gravity;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;

/**
 * Created by kehanwang on 2018/3/17.
 */

public class TestEditProfile {

    @Rule
    public ActivityTestRule<EditProfileActivity> EditProfileActivity = new ActivityTestRule<>(EditProfileActivity.class,true, false);



    @Test
    public void RegisterTest(){
        User user = new User("kehan123456", "kehan1", "780");
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation().getTargetContext(), EditProfileActivity.class);
        intent.putExtra("currentUser",user);
        EditProfileActivity.launchActivity(intent);


        onView(withId(R.id.UserName)).perform(clearText(),typeText("kehan1234561"),closeSoftKeyboard())
                .check(matches(withText(containsString("kehan123456"))));

        onView(withId(R.id.UserEmail)).perform(clearText(),typeText("kehan11"),closeSoftKeyboard())
                .check(matches(withText(containsString("kehan1"))));

        onView(withId(R.id.UserPhone)).perform(clearText(),typeText("7801"),closeSoftKeyboard())
                .check(matches(withText(containsString("780"))));

        onView(withId(R.id.SaveEdit))
                .perform(click());

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_profile));}
}
