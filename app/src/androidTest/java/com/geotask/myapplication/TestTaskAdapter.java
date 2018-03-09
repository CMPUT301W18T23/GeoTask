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
    }
}
