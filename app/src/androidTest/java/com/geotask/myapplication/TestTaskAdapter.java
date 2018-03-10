package com.geotask.myapplication;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import org.junit.Test;

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
}
