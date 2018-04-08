package com.geotask.myapplication;

import android.util.Log;

import com.geotask.myapplication.Controllers.Helpers.EmailConverter;
import com.geotask.myapplication.Controllers.Helpers.GetKeywordMatches;
import com.geotask.myapplication.DataClasses.Task;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;

import static com.geotask.myapplication.Controllers.Helpers.GetKeywordMatches.getSortedResults;

/**
 * Created by Kyle on 2018-04-07.
 */

@RunWith(JUnit4.class)
public class TestGetKeywordMatches {

    @Test
    public void testGetMatches(){
        Assert.assertTrue(GetKeywordMatches.getMatches("a b", "a", "b c") == 2);

        System.out.println(GetKeywordMatches.getMatches("a b", "a", "b c").toString());
    }

    @Test
    public void testGetSortedResults(){
        Task task1 = new Task("123", "a", "b");
        Task task2 = new Task("123", "c d", "e");
        Task task3 = new Task("123", "f", "g");
        ArrayList<Task> taskList = new ArrayList<>();
        taskList.add(task3);
        taskList.add(task2);
        taskList.add(task1);

        taskList = getSortedResults(taskList, "a c d");

        Assert.assertTrue(taskList.get(0).equals(task2));
        Assert.assertTrue(taskList.get(1).equals(task1));
        Assert.assertTrue(taskList.get(2).equals(task3));
        System.out.println(taskList.get(0).getName());
        System.out.println(taskList.get(1).getName());
        System.out.println(taskList.get(2).getName());
    }
}
