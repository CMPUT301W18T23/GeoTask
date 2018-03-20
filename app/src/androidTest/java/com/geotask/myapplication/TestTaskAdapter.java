package com.geotask.myapplication;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class TestTaskAdapter{

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule = new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void testDrawer() throws InterruptedException {
        /**
         * onView(item).perform(action)
         */
        Thread.sleep(2000);

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());
        Thread.sleep(1000);

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_provider));
        Thread.sleep(1000);
    }
}
