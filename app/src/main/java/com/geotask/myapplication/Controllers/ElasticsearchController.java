package com.geotask.myapplication.Controllers;

import android.util.Log;

import com.geotask.myapplication.DataClasses.Bid;
import com.geotask.myapplication.DataClasses.BidList;
import com.geotask.myapplication.DataClasses.GTData;
import com.geotask.myapplication.DataClasses.Task;
import com.geotask.myapplication.DataClasses.User;
import com.geotask.myapplication.QueryBuilder.SuperBooleanBuilder;
import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/*
API DOCUMENTATION
https://github.com/searchbox-io/Jest/tree/master/jest
 */


/**
 * ElasticsearchController - Our heart and soul
 *
 */
public class ElasticsearchController {
    private static JestDroidClient client;
    private final static String INDEX_NAME = "cmput301w18t23";

    /**
     * createNewDocument - creates the new document of whichever type it is passed
     * Note that the getType() function is overridden by every class that extends GTData. So in
     * other words if you pass it a Profile, it will be added to profile, if you pass it a Bid, it
     * will be added to bid ...
     *
     * @param data - GTData that should be added to the database
     * @return - ID of the document
     */
    public String createNewDocument(GTData data){
        Gson gson = new Gson();
        String json = gson.toJson(data);
        Index request = new Index.Builder(json).index(INDEX_NAME).type(data.getType()).build();

        System.out.println(request.toString());
        try {
            JestResult result = client.execute(request);
            return result.getJsonObject().get("_id").toString().replace("\"", "");
           // return result.get;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * getDocument - Get a single document by ID. Will downcast to correct object type.
     *
     * @param ID - ID of the document
     * @return GTData object
     */
    public GTData getDocument(String ID) {
        Get request = new Get.Builder(INDEX_NAME, ID).build();

        try {
            JestResult result = client.execute(request);
            GTData data = result.getSourceAsObject(Bid.class);
            if (data.getType().equals("bid")) {
                return (Bid) data;
            } else if (data.getType().equals("user")) {
                return (User) data;
            } else if (data.getType().equals("task")) {
                return (Task) data;
            } else if (data.getType().equals("bidList")) {
                return (BidList) data;
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * deleteDocument - deletes a document with the provided id of provided type
     *
     * @param ID - ID of document
     * @param type - type of document
     * @return - response code of deletion (200 on success, 400 on failure, -1 on exception)
     */
    public int deleteDocument(String ID, String type) {
        try {
            Log.i("build", new Delete.Builder(ID).index(INDEX_NAME).type(type).build().toString());
            return client.execute(new Delete.Builder(ID).index(INDEX_NAME).type(type).build()).getResponseCode();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * searchBids - Method that searches the elasticSearch database and returns a list of bid objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of Bid objects
     */
    public List<Bid> searchBids(String query) {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType("bid").build();
        try {

            SearchResult result = client.execute(search);
            List<Bid> bids = result.getSourceAsObjectList(Bid.class);
            return bids;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * searchTasks - Method that searches the elasticSearch database and returns a list of task objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of Task objects
     */
    public List<Task> searchTasks(String query) {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType("task").build();
        try {

            SearchResult result = client.execute(search);
            List<Task> tasks = result.getSourceAsObjectList(Task.class);
            return tasks;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * searchUsers - Method that searches the elasticSearch database and returns a list of profile objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of profile objects
     */
    public List<User> searchUsers(String query) {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType("user").build();
        try {

            SearchResult result = client.execute(search);
            List<User> users = result.getSourceAsObjectList(User.class);
            return users;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * existsProfile - Method for checking if an email is in use by another user
     *
     * @param email - email of the registering use
     * @return true if the email is in use, false otherwise
     */
    public boolean existsProfile(String email){
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("email");
        arrayList.add(email);
        SuperBooleanBuilder query = new SuperBooleanBuilder();
        query.put(arrayList);
        Search search = new Search.Builder(query.toString()).addIndex(INDEX_NAME).addType("user").build();
        try {
            if(client.execute(search).getTotal() > 0){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * verifySettings - Initializes client if not previously initialized
     */
    public static void verifySettings() {
        if(client == null) {
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);

            client = (JestDroidClient) factory.getObject();
        }
    }
}
