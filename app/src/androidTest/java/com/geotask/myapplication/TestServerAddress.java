package com.geotask.myapplication;


/**
 * global static reference to test server address
 */

class TestServerAddress {
    private static final TestServerAddress ourInstance = new TestServerAddress();
    private static final String testAddress = "cmput301w18t23test";

    private TestServerAddress() {
    }

    static String getTestAddress() {
        return testAddress;
    }
}
