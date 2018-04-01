package com.geotask.myapplication.Controllers.SyncServices;


import com.geotask.myapplication.DataClasses.Task;

public class TaskMerger {

    public static Task merge(Task oldTask, Task newTask) {
        for(String BidId : oldTask.getBidList()){
            if(!newTask.getBidList().contains(BidId)){
                newTask.getBidList().add(BidId);
            }
        }
        return newTask;
    }
}
