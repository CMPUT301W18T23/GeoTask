package com.geotask.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MT on 2/27/2018.
 */
public class ElasticsearchBasicTest {

    @Test
    public void testCreateNewDocument() {
        Bid bid = new Bid("provider", 23.0, "Task");

        ElasticsearchController controller = new ElasticsearchController();
        controller.verifySettings();
        controller.createNewDocument(bid);
    }
}