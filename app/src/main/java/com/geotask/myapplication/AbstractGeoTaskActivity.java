package com.geotask.myapplication;

import android.accounts.Account;
import android.support.v7.app.AppCompatActivity;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGeoTaskActivity extends AppCompatActivity{

    private static User currentUser;
    private static Task currentTask;
    private static List<Bid> bidList;
    private static ArrayList<Task> taskList;
    private static Account account;
    private static int viewMode = R.integer.MODE_INT_ALL;
    private static String searchKeywords;
    private static double searchRange;


    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        AbstractGeoTaskActivity.currentUser = currentUser;
    }

    public static Task getCurrentTask() {
        return currentTask;
    }

    public static void setCurrentTask(Task currentTask) {
        AbstractGeoTaskActivity.currentTask = currentTask;
    }

    public static ArrayList<Task> getTaskList() {
        return taskList;
    }

    public static void setTaskList(ArrayList<Task> taskList) {
        AbstractGeoTaskActivity.taskList = taskList;
    }

    public static List<Bid> getBidList() {
        return bidList;
    }

    public static void setBidList(List<Bid> bidList) {
        AbstractGeoTaskActivity.bidList = bidList;
    }

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account account) {
        AbstractGeoTaskActivity.account = account;
    }

    public static int getViewMode() {
        return viewMode;
    }

    public static void setViewMode(int viewMode) {
        AbstractGeoTaskActivity.viewMode = viewMode;
    }

    public static String getSearchKeywords() {
        return searchKeywords;
    }

    public static void setSearchKeywords(String searchKeywords) {
        AbstractGeoTaskActivity.searchKeywords = searchKeywords;
    }

    public static double getSearchRange() {
        return searchRange;
    }

    public static void setSearchRange(double searchRange) {
        AbstractGeoTaskActivity.searchRange = searchRange;
    }
}
