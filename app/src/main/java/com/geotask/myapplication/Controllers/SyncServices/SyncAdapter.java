package com.geotask.myapplication.Controllers.SyncServices;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.geotask.myapplication.Controllers.ElasticsearchController;
import com.geotask.myapplication.Controllers.LocalFilesOps.LocalDataBase;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

import java.io.IOException;
import java.util.ArrayList;

import io.searchbox.client.JestResult;

//https://developer.android.com/training/sync-adapters/creating-sync-adapter.html


public class SyncAdapter extends AbstractThreadedSyncAdapter {
    private static ElasticsearchController controller;
    private LocalDataBase database;
    private ArrayList<Task> remoteTaskList;
    private ArrayList<Bid> remoteBidList;
    private ArrayList<Task> localTaskList;
    private ArrayList<Bid> localBidList;

    public SyncAdapter(Context context, boolean autoInitiate) {
        super(context, autoInitiate);
        controller = new ElasticsearchController();
        controller.verifySettings();
        //controller.setTestSettings("cmput301w18t23test");
        if(database == null) {
            Log.d("geotasksync", "open database");
            database = LocalDataBase.getDatabase(context);
        }
    }

    /**
     * compatibility for android 3.0+
     * @param context
     * @param autoInitiate
     * @param allowParallelSyncs
     */
    public SyncAdapter(Context context, boolean autoInitiate, boolean allowParallelSyncs) {
        super(context, autoInitiate, allowParallelSyncs);
        controller = new ElasticsearchController();
        controller.verifySettings();
        if(database == null) {
            Log.d("geotasksync", "open database");
            database = LocalDataBase.getDatabase(context);
        }
    }


    /**
     *
     * @param account for account services, not used, use CreateSyncAccount(this) for dummy account
     * @param extras extra information sent by event
     * @param authority authority of content provider
     * @param provider
     * @param syncResult sends information back to syncAdapter framework
     */
    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {

        // select all locally edited or new queries
        SQLQueryBuilder query = new SQLQueryBuilder(Task.class);
        query.addColumns(new String[] {"flag"});
        query.addParameters(new Boolean[] {true});
        localTaskList = (ArrayList<Task>) database.taskDAO().searchTasksByQuery(query.build());

        // get all tasks on server
        try {
            remoteTaskList = (ArrayList<Task>) controller.search("", Task.class);
            Log.d("geotasksync_remoteTAskList_size", String.valueOf(remoteTaskList.size()));
        } catch (IOException e) {e.printStackTrace();}

        // compare data and sync
        JestResult result;
        for (Task localTask : localTaskList) {
            try {
                if(remoteTaskList.contains(localTask)) {
                    result = controller.updateDocument(localTask, localTask.getVersion());
                    if(result.getResponseCode() == 409 && localTask.isClientOriginalFlag()) {//if document is edited locally
                            merge(localTask, (Task) controller.getDocument(localTask.getObjectID(), Task.class));
                            controller.updateDocument(localTask, localTask.getVersion());
                    }
                } else {
                    result = controller.createNewDocument(localTask);
                    localTask.setVersion((Double) result.getValue("_version"));
                    database.taskDAO().update(localTask);
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        query = new SQLQueryBuilder(Bid.class);
        query.addColumns(new String[] {"flag"});
        query.addParameters(new Boolean[] {true});
        localBidList = (ArrayList<Bid>) database.bidDAO().searchBidsByQuery(query.build());

        for(Bid localBid : localBidList) {
            try {
                result = controller.createNewDocument(localBid);
            } catch (Exception e) {e.printStackTrace();}
        }

        try {
            remoteTaskList = (ArrayList<Task>) controller.search("", Task.class);
            remoteBidList = (ArrayList<Bid>) controller.search("", Bid.class);
            Log.d("geotasksync", "task: " + remoteTaskList.size() + " bid: " + remoteBidList.size());

            database.taskDAO().delete();
            for(Task task : remoteTaskList){
                task.setVersion(controller.getDocumentVersion(task.getObjectID()));
                database.taskDAO().insert(task);
            }

            database.bidDAO().delete();
            for(Bid bid : remoteBidList){
                database.bidDAO().insert(bid);
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    private void merge(Task oldTask, Task newTask) {

    }
}


