package com.geotask.myapplication.Controllers.Helpers;

import com.geotask.myapplication.Controllers.AsyncCallBackManager;
import com.geotask.myapplication.Controllers.MasterController;
import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.QueryBuilder.SQLQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kyle on 2018-03-18.
 */
public class GetLowestBidFromServer implements AsyncCallBackManager {
    private GTData data = null;
    private List<? extends GTData> searchResult = null;

    public ArrayList<Double> searchAndReturnLowest(Task task) {


        //make the query
        SQLQueryBuilder builder = new SQLQueryBuilder(Bid.class);
        builder.addColumns(new String[] {"taskID"});
        builder.addParameters(new String[]{task.getObjectID()});

        List<Bid> result = null;
        ArrayList<Bid> bidList;
        Double lowest = -1.0;

        result = (List<Bid>) MasterController.slowSearch(new AsyncArgumentWrapper(builder, Bid.class));
        bidList = new ArrayList<Bid>(result);

        ArrayList<Double> returnList = new ArrayList<Double>();

        if (bidList.size() > 0) {
            lowest = bidList.get(0).getValue();
            for (Bid bid : bidList) {                                     //iterate to find lowest
                if (bid.getValue() < lowest) {
                    lowest = bid.getValue();
                }
            }
        }

        returnList.add(lowest);
        returnList.add(0.0 + bidList.size());
        return returnList;

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
