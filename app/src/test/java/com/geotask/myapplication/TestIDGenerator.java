package com.geotask.myapplication;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TestIDGenerator {

    String source = "abcdefghijklmnopqrstuvwxyz1234567890";

    @Before
    public void setUp() {
    }

    public boolean validateID(String ID) {
        boolean result = true;

        for(int j = 0; j < ID.length(); j++) {
            if(!source.contains(ID.substring(j, j+1))){
                result = false;
                break;
            }
        }
        return result;
    }

    @Test
    public void TestIDGenerator() {
        String ID = UniqueIDGenerator.generate();

        assertEquals(29, ID.length());

        System.out.println(ID);

        assertTrue(validateID(ID));
    }

    @Test
    public void TestGTDataConstruction() {
        GTData bid = new Bid("id test", 1.1, "id test");
        System.out.println(bid.getObjectID());
        assertTrue(validateID(bid.getObjectID()));
    }
}
