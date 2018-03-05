package com.geotask.myapplication;

import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import org.junit.Test;

import java.util.ArrayList;

public class SuperBooleanBuilderTest {

    @Test
    public void builderTest(){
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("ProviderID", "task1");
        System.out.println(builder.toString());
    }

    @Test
    public void multiBuilterTest() {
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        ArrayList<String> list = new ArrayList<>();
        list.add("providerID");
        list.add("test1");
        list.add("taskID");
        list.add("test");

        builder.put(list);
        System.out.println(builder.toString());
    }
}
