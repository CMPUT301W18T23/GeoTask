package com.geotask.myapplication.Controllers.Helpers;

import com.geotask.myapplication.DataClasses.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Kyle on 2018-04-07.
 */

public class GetKeywordMatches {
    public static Integer getMatches(String keywords, String description, String title) {
        Integer matches = 0;
        String target = description + " " + title;
        for (String keyword : keywords.split(" ")) {
            if (target.contains(keyword)) {
                matches++;
            }
        }
        return matches;
    }

    public static ArrayList<Task> getSortedResults(ArrayList<Task> unsorted, final String keywords) {
        Collections.sort(unsorted, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return getMatches(keywords, task2.getDescription(), task2.getName()) -
                        getMatches(keywords, task1.getDescription(), task1.getName());
            }
        });
        return unsorted;
    }
}
