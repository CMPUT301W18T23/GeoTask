package com.geotask.myapplication;


/**
 * global static reference to test server address
 */

public class TestServerAddress {
    private static final TestServerAddress ourInstance = new TestServerAddress();
    private static final String testAddress = "cmput301w18t23test";

    private TestServerAddress() {
    }

    public static String getTestAddress() {
        return testAddress;
    }
}
