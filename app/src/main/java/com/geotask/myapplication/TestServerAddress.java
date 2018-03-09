package com.geotask.myapplication;

/**
 * Created by Michael on 3/9/2018.
 */

class TestServerAddress {
    private static final TestServerAddress ourInstance = new TestServerAddress();
    private static final String testAddress = "cmput301w18t23";

    private TestServerAddress() {
    }

    static String getTestAddress() {
        return testAddress;
    }
}
