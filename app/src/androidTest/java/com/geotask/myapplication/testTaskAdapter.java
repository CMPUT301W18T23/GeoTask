package com.geotask.myapplication;

import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

public class testTaskAdapter extends ActivityInstrumentationTestCase2<MenuActivity> {

    private Solo solo;

    public testTaskAdapter(String pkg, Class<MenuActivity> activityClass) {
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

    public void testListItemClickShouldStartSingleTaskViewActivity() throws Exception {
        solo.assertCurrentActivity("not seeing main list", MenuActivity.class);

        solo.clickInList(1);
        solo.assertCurrentActivity("not seeing single task", TaskViewActivity.class);
    }
}
