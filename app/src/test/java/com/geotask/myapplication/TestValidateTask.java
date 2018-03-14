package com.geotask.myapplication;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
/**
 * Created by james on 2018-03-14.
 */

public class TestValidateTask {
    String name = "small name";
    String description = "small description";
    ValidateTask over = new ValidateTask();

    @Test
    public void testGood(){ //test if normal data is good
        assertTrue(over.checkText(name, description));
    }


    @Test
    public void testBig(){


        String bigName = "0123456789012345678901234567891"; //over the limit by 1 char

        String bigDescription = "01234567890123456789012345678901234567890123456789" +
                "0123456789012345678901234567890123456789012345678901234567890123456789" +
                "0123456789012345678901234567890123456789012345678901234567890123456789" +
                "0123456789012345678901234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678901234567891"; // 301 chars (over limit
        assertFalse(over.checkText(bigName, description)); //test only name
        assertFalse(over.checkText(name, bigDescription)); //test only description to big
        assertFalse(over.checkText(bigName, bigDescription)); //test both at same time

    }
    @Test
    public void testNone(){
        assertFalse(over.checkText("", description)); //empty name
        assertFalse(over.checkText(name, "")); //empty description
        assertFalse(over.checkText("", "")); //empty both


    }

}
