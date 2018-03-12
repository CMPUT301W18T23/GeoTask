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
    private LocalDataBase database;
    private Context context;

    public BidDatabaseController(Context context) {
        this.context = context;
        this.database = LocalDataBase.getDatabase(context);
    }

    /**
     * AsyncTask for inserting into table
     */
    public class insertBid extends AsyncTask<Bid, Void, Void> {

        @Override
        protected Void doInBackground(Bid... bids) {
            database = LocalDataBase.getDatabase(context);

            for(Bid bid : bids) {
                database.bidDAO().insert(bid);
            }
            return null;
        }
    }

    public class selectBidByProviderID extends AsyncTask<String, Void, List<Bid>> {
        private AsyncCallBackManager callBack = null;

        public selectBidByProviderID(AsyncCallBackManager callback){
            this.callBack = callback;
        }

        @Override
        protected List<Bid> doInBackground(String... providerIDLIst) {
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
}
