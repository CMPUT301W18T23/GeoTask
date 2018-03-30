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
import io.searchbox.params.Parameters;

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
            Log.d("syncadapter", "open database");
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
            Log.d("syncadapter", "open database");
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
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("SYNCADAPTERER", "WORKING");
        try {
            remoteTaskList = (ArrayList<Task>) controller.search("", Task.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("SYNCADAPTERER", String.valueOf(remoteTaskList.size()));
        localTaskList = (ArrayList<Task>) database.taskDAO().selectAll();

        JestResult result = null;
        double version;
        for (Task localTask : localTaskList) {
            if(remoteTaskList.contains(localTask)) {
                try {
                    result = controller.updateDocument(localTask, localTask.getVersion());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(result != null && result.getResponseCode() == 200) {
                    localTask.setVersion((int) result.getValue(Parameters.VERSION));
                    database.taskDAO().update(localTask);

                    SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
                    builder.addColumns(new String[]{"taskID"});
                    builder.addParameters(new String[]{localTask.getObjectID()});

                    localBidList = (ArrayList<Bid>) database.bidDAO().searchBidsByQuery(builder.build());
                    for(Bid bid : localBidList) {
                        try {
                            controller.createNewDocument(bid);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else if(result.getResponseCode() == 409) {
                    //rejected
                }
            } else {
                try {
                    version = controller.createNewDocument(localTask);
                    localTask.setVersion((int)version);
                    database.taskDAO().update(localTask);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            remoteTaskList = (ArrayList<Task>) controller.search("", Task.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(Task task : remoteTaskList){
            database.taskDAO().insert(task);
        }
    }
}

