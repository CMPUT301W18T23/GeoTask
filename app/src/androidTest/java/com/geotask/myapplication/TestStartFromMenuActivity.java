package com.geotask.myapplication;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Task;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertTrue;

//https://developer.android.com/training/testing/espresso/recipes.html
@RunWith(AndroidJUnit4.class)
public class TestStartFromMenuActivity {

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

    @Rule
    public ActivityTestRule<MenuActivity> menuActivityRule = new ActivityTestRule<>(MenuActivity.class);

    @Test
    public void testOnClickItemInListShouldOpenTaskDetail() {
        assertTrue(menuActivityRule.getActivity() != null);

        onData(instanceOf(Task.class))
                .atPosition(0)
                .perform(click());
        onView(withId(R.id.editTaskButton)).check(matches(withText("EditTask")));
    }

    @Test
    public void testAddNewTaskFromMenuShouldAddTaskToList() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT)))
                .perform(DrawerActions.open());

        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_provider));

        onView(withId(R.id.fab))
                .perform(click());
    }

    @Test
    public void testDeleteTaskFromListShouldRemoveIt() {

    }

    @Test
    public void testChangeToRequesterModeShouldSeeOnlyYourRequestedTasks() {

    }

    @Test
    public void testChangeToProviderModeShouldSeeOnlyYourProvicedTasks() {

    }

    @Test
    public void testChangeToAllShouldShowAllTask() {

    }


    @Test
    public void testLogOut() {

    }

    @Test
    public void testLogInThenLogOutThenLogInThenBackButtonShouldNotGoBackToFirstLogin() {

    }
}
