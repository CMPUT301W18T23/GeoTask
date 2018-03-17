package com.geotask.myapplication.Controllers.LocalFilesOps;

import android.content.Context;
import android.os.AsyncTask;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.DataClasses.Bid;

import java.util.List;

/**
 * controls all local data changes for Bid. Do not call explicitly, use MasterController
 */
public class BidDatabaseController {
    private static LocalDataBase database;
    private static Context context;


    /**
     * AsyncTask for inserting into table
     */
    public static class insertBid extends AsyncTask<Bid, Void, Void> {

        @Override
        protected Void doInBackground(Bid... bids) {
            //database = LocalDataBase.getDatabase();

            for(Bid bid : bids) {
                database.bidDAO().insert(bid);
            }
            return null;
        }
    }

    public static class updateBid extends AsyncTask<Bid, Void, Void> {

        @Override
        protected Void doInBackground(Bid... bids) {
            database = LocalDataBase.getDatabase(context);

            for(Bid bid : bids) {
                database.bidDAO().update(bid);
            }
            return null;
        }
    }

    public static class selectBidByProviderID extends AsyncTask<String, Void, List<Bid>> {
        private AsyncCallBackManager callBack = null;

        public selectBidByProviderID(AsyncCallBackManager callback){
            this.callBack = callback;
        }

        @Override
        protected List<Bid> doInBackground(String... providerIDLIst) {
            database = LocalDataBase.getDatabase(context);

            List<Bid> bid = null;
            for(String providerID : providerIDLIst){
                bid = database.bidDAO().selectByProvider(providerID);
            }
            return bid;
        }

        @Override
        protected void onPostExecute(List<Bid> bid) {
            if(callBack != null) {
                callBack.onPostExecute(bid);
            }
        }
    }

    public static class selectBidByTaskID extends AsyncTask<String, Void, List<Bid>> {
        private AsyncCallBackManager callback = null;

        @Override
        protected List<Bid> doInBackground(String... taskIDList) {
            database = LocalDataBase.getDatabase(context);

            List<Bid> bid = null;
            for(String taskID : taskIDList) {
                bid = database.bidDAO().selectByTask(taskID);
            }
            return bid;
        }

        @Override
        protected void onPostExecute(List<Bid> bids) {
            if(callback != null) {
                callback.onPostExecute(bids);
            }
        }
    }
}