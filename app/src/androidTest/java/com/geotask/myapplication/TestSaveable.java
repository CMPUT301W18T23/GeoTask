package com.geotask.myapplication;

import android.test.ActivityInstrumentationTestCase2;

import com.geotask.myapplication.DataClasses.Bid;
import com.robotium.solo.Solo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class TestSaveable extends ActivityInstrumentationTestCase2<MenuActivity> {

    private Solo solo;

    public TestSaveable(String pkg, Class<MenuActivity> activityClass) {
        super(pkg, activityClass);
    }

    @Test
    public void TestWriteFile() {
        Bid bid = new Bid("write test", 22.0, "write test");

        bid.writeFile(getActivity().getBaseContext());
    }
}
