package com.geotask.myapplication.Controllers.Helpers;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Kyle on 2018-03-18.
 */

public class GetLowestBidFromServer implements AsyncCallBackManager {
    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    public Double searchAndReturnLowest(Task task) {
        //make the query
        SuperBooleanBuilder builder = new SuperBooleanBuilder();
        builder.put("taskID", task.getObjectID());

        //perform the search
        MasterController.AsyncSearch asyncSearch =
                new MasterController.AsyncSearch(this);
        asyncSearch.execute(new AsyncArgumentWrapper(builder, Bid.class));

        List<Bid> result = null;
        ArrayList<Bid> bidList;
        Double lowest = -1.0;
        try {
            //get the result
            result = (List<Bid>) asyncSearch.get();
            bidList = new ArrayList<Bid>(result);

        /*
            set the lowestBid TextView
         */

            if (bidList.size() == 0) {
                task.setStatusRequested();                                  //change the status
                MasterController.AsyncUpdateDocument asyncUpdateDocument =  //update the status
                        new MasterController.AsyncUpdateDocument();
                asyncUpdateDocument.execute(task);

            } else if (bidList.size() == 1) {
                lowest = bidList.get(0).getValue();
            } else {
                lowest = bidList.get(0).getValue();
                for (Bid bid : bidList) {                                     //iterate to find lowest
                    if (bid.getValue() < lowest) {
                        lowest = bid.getValue();
                    }
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lowest;
    }

    @Override
    public void onPostExecute(GTData data) {
        this.data = data;
    }

    @Override
    public void onPostExecute(List<? extends GTData> dataList) {
        this.searchResult = dataList;
    }

}
