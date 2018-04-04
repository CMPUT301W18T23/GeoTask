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

        // select all locally edited or new tasks
        SQLQueryBuilder query = new SQLQueryBuilder(Task.class);
        query.addColumns(new String[] {"flag"});
        query.addParameters(new Boolean[] {true});
        localTaskList = (ArrayList<Task>) database.taskDAO().searchTasksByQuery(query.build());
        Log.d("geotasksync", "localTaskList_size = " + localTaskList.size());

        query = new SQLQueryBuilder(Bid.class);
        query.addColumns(new String[] {"flag"});
        query.addParameters(new Boolean[] {true});
        localBidList = (ArrayList<Bid>) database.bidDAO().searchBidsByQuery(query.build());
        Log.d("geotasksync", "localBidList_size = " + localBidList.size());

        // get all tasks on server
        try {
            remoteTaskList = (ArrayList<Task>) controller.search("", Task.class);
            Log.d("geotasksync", "remoteTAskList_size = " + remoteTaskList.size());
        } catch (IOException e) {e.printStackTrace();}

        // compare data and sync
        JestResult result;
        for (Task localTask : localTaskList) {
            try {
                //if local task was edited
                if(remoteTaskList.contains(localTask)) {
                    Log.d("geotasksync", "remote contains local");
                    result = controller.updateDocument(localTask, localTask.getVersion());
                    if(result.getResponseCode() == 409 && localTask.isClientOriginalFlag()) {
                        Log.d("geotasksync", "local push rejected");
                        merge(localBidList, localTask,
                                (Task) controller.getDocument(localTask.getObjectID(), Task.class));
                        Log.d("geotasksync", "merged result = " + localTask.getBidList().size());
                        result = controller.updateDocument(localTask, controller.getDocumentVersion(localTask.getObjectID()));
                        Log.d("geotasksync", "409 update result = " + result.getJsonString());
                    }
                //if local task was new
                } else {
                    Log.d("geotasksync", "remote does not contain local");
                    result = controller.createNewDocument(localTask);
                    localTask.setVersion((Double) result.getValue("_version"));
                    database.taskDAO().update(localTask);
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        Log.d("geotasksync", "localbidlist size is still " + localBidList.size());
        for(Bid localBid : localBidList) {
            try {
                if(controller.getDocument(localBid.getTaskID(), Bid.class) != null) {
                    result = controller.createNewDocument(localBid);
                    Log.d("geotasksync", result.getJsonString());
                }
            } catch (Exception e) {e.printStackTrace();}
        }

        try {
            controller.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            remoteTaskList = (ArrayList<Task>) controller.search("", Task.class);
            remoteBidList = (ArrayList<Bid>) controller.search("", Bid.class);
            Log.d("geotasksync", "remote updated task: " + remoteTaskList.size() +
                    ". remote updated bid: " + remoteBidList.size());

            //database.taskDAO().delete();
            Log.d("geotasksync", "task rows deleted = " + database.taskDAO().delete());
            for(Task task : remoteTaskList){
                task.setVersion(controller.getDocumentVersion(task.getObjectID()));
                database.taskDAO().insert(task);
            }
            Log.d("geotasksync", "task size after pull = " + database.taskDAO().selectAll().size());

            //database.bidDAO().delete();
            Log.d("geotasksync", "bid rows deleted = " + database.bidDAO().delete());
            for(Bid bid : remoteBidList){
                database.bidDAO().insert(bid);
            }
            Log.d("geotasksync", "bid size after pull = " + database.bidDAO().selectAll().size());
        } catch (IOException e) {e.printStackTrace();}
    }

    //if local is edited and remote is edited by someone else
    private void merge(ArrayList<Bid> localBidList, Task local, Task remote) {
        Log.d("geotasksync_merge", "merging");
        Log.d("geotasksync_merge", localBidList.toString());
        //merge bidlist, add bids that weren't synced from server
        for(Bid bid : localBidList){
            Log.d("geotasksync_merge", bid.getTaskID() + " " + local.getObjectID());
            Log.d("geotasksync_merge", String.valueOf(bid.getTaskID().equals(local.getObjectID())));
            if(bid.getTaskID().equals(local.getObjectID())) {
                remote.addBid(bid);
                Log.d("geotasksync_merge", remote.getBidList().toString());
            }
        }
        Log.d("geotasksync_merge", "merged bid list = " + remote.getBidList().toString());

        if(local.isClientOriginalFlag()){ //local edited
            Log.d("geotasksync_merge", "local edited");
            local.setBidList(remote.getBidList()); //remote bidlist + local edits
        } else { //local bidded on
            Log.d("geotasksync_merge", "local not edited");
            local = remote; //remote bidlist + no local edits
        }
        Log.d("geotasksync_merge", "merged result = " + local.getBidList().size());
    }
}


