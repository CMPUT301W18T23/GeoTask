package com.geotask.myapplication;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.espresso.proto.matcher13.HamcrestMatchersv13;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.Menu;

import com.geotask.myapplication.DataClasses.Task;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.CursorMatchers.withRowString;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


import static android.support.test.espresso.Espresso.onData;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class TestUiBasic {

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule = new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void testOnClickItemInListShouldOpenTaskDetail() {
        assertTrue(menuActivityRule.getActivity() instanceof MenuActivity);

        onData(instanceOf(Task.class))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.editTaskButton)).check(matches(withText("EditTask")));
    }

    @Test
    public void testAddNewTaskFromMenuAndDeleteTask() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_provider));

        onView(withId(R.id.fab))
                .perform(click());
    }
}
