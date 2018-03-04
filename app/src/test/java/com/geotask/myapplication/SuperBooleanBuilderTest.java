package com.geotask.myapplication;

import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import org.junit.Test;

public class SuperBooleanBuilderTest {

    @Test
    public void builderTest(){
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("ProviderID", "task1");
        System.out.println(builder.toString());
    }
}
