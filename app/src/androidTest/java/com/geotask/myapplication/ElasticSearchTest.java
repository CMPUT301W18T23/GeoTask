package com.geotask.myapplication;

import android.util.Log;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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

}
