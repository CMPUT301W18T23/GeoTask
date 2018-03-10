package com.geotask.myapplication;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;

import com.robotium.solo.Solo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.time.temporal.JulianFields;

@RunWith(JUnit4.class)
public class TestTaskAdapter extends ActivityInstrumentationTestCase2<MenuActivity> {

    private Solo solo;

    public TestTaskAdapter(String pkg, Class<MenuActivity> activityClass) {
        super(pkg, activityClass);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        //ToDo: add stuff to list so there's something to click on
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    @Test
    public void testRegisterButtonClickShouldStartMenuActivity() {
    }

    @Test
    public void testAddItemToList() {
    }

    @Test
    public void testListItemClickShouldStartSingleTaskViewActivity() {
        solo.assertCurrentActivity("Error: not in MenuActivity", MenuActivity.class);
        solo.clickInList(0);
        solo.assertCurrentActivity("Error: not in TaskViewActivity", TaskViewActivity.class);
    }

    /*
    @Test
    public void testAddTaskUsingFloatingActionButton() {
        solo.assertCurrentActivity("not MenuActivity", MenuActivity.class);

        View floatingActionButton = getActivity().findViewById(R.id.fab);
        solo.clickOnView(floatingActionButton);

        solo.assertCurrentActivity("not AddTaskActivity", AddTaskActivity.class, true);

        EditText name = getActivity().findViewById(R.id.add_task_name);
        EditText description = getActivity().findViewById(R.id.add_task_description);
        solo.enterText(name, "test add");
        solo.enterText(description, "test description");
        solo.clickOnButton("Request");

        solo.assertCurrentActivity("not MenuActivity", MenuActivity.class);

        solo.clickInList(0);

        solo.assertCurrentActivity("not TaskViewActivity", TaskViewActivity.class);
    }
    */
}
