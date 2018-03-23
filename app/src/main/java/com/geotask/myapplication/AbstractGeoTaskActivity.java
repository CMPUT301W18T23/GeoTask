package com.geotask.myapplication;

import android.accounts.Account;
import android.support.v7.app.AppCompatActivity;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;

import java.util.List;

public abstract class AbstractGeoTaskActivity extends AppCompatActivity{

    private static User currentUser;
    private static Task currentTask;
    private static List<Bid> bidList;
    private static Account account;
    private static int viewMode = 0;
    private static String searchKeywords;

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
}
