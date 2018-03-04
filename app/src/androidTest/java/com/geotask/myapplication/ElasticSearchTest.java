package com.geotask.myapplication;

import android.util.Log;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

/**
 * Created by Kyle on 2018-03-04.
 */
@RunWith(JUnit4.class)
public class ElasticSearchTest {

    ElasticsearchController controller = new ElasticsearchController();

    @Before
    public void setup(){
        controller.verifySettings();

    }

    @Test
    public void TestCreateNewDocument(){
        Bid bid = new Bid("test",77.8, "test");
        Log.i("hi", controller.createNewDocument(bid));
        System.out.println(".");
    }

    @Test
    public void TestGetDocument(){
        //GTData bid2 = (Bid) controller.getDocument("AWHytEkr1Qdy-XuYsHZA", bid.getClass().toString());
        Log.i("hi", controller.getDocument("AWHytEkr1Qdy-XuYsHZA").toString());
    }

    @Test
    public void TestSearch(){
        ArrayList<ArrayList<String>> test = new ArrayList<ArrayList<String>>();
        controller.search("null", test);
    }

    @Test
    public void TestDeleteDocument(){
        //GTData bid2 = (Bid) controller.getDocument("AWHytEkr1Qdy-XuYsHZA", bid.getClass().toString());
        Bid bid = new Bid("test",77.8, "test");
        String test = controller.createNewDocument(bid);
        Log.i("hi", test);
        Assert.assertEquals(200, controller.deleteDocument(test));
    }

}
