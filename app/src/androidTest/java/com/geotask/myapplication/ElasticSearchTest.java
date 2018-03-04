package com.geotask.myapplication;

import android.util.Log;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

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
    public void TestSearch() {
  /*      Bid bid1 = new Bid("test1",77.8, "test");
        Bid bid2 = new Bid("test2",67.8, "test");
        Bid bid3 = new Bid("test3",57.8, "test");
        Bid bid4 = new Bid("test4",47.8, "test");
        Bid bid5 = new Bid("test5",37.8, "test");
        Bid bid6 = new Bid("test6",27.8, "test");
        Bid bid7 = new Bid("test7",17.8, "test");

        controller.createNewDocument(bid1);
        controller.createNewDocument(bid2);
        controller.createNewDocument(bid3);
        controller.createNewDocument(bid4);
        controller.createNewDocument(bid5);
        controller.createNewDocument(bid6);
        controller.createNewDocument(bid7);

        TimeUnit.SECONDS.sleep(3);
*/
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("providerID", "test1");

        //System.out.println(builder.toString());
        Integer string = controller.search("bid", builder.toString());
        Log.i("&&&&&&&&&&&&&&&&&235135&&&&&&&&&", string.toString());
        System.out.println(string);
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
