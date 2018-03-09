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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

/*
API DOCUMENTATION
https://github.com/searchbox-io/Jest/tree/master/jest
 */


/**
 * ElasticsearchController - Our heart and soul
 *
 */
public class ElasticsearchController {
    private static final String SERVER_ADDRESS = "http://cmput301.softwareprocess.es:8080";
    private static JestDroidClient client;
    private String INDEX_NAME = "cmput301w18t23";


    /**
     * Creates the index
     * @throws IOException
     */
    public void createIndex() throws IOException {
        client.execute(new CreateIndex.Builder(INDEX_NAME).build());
    }

    /**
     * Delete the index
     * @throws IOException
     */
    public int deleteIndex() throws IOException {
        JestResult result = client.execute(new DeleteIndex.Builder(INDEX_NAME).build());
        return result.getResponseCode();
    }

    /**
     * createNewDocument - creates the new document of whichever type it is passed
     * Note that the getType() function is overridden by every class that extends GTData. So in
     * other words if you pass it a Profile, it will be added to profile, if you pass it a Bid, it
     * will be added to bid ...
     *
     * @param data - GTData that should be added to the database
     * @return - ID of the document
     */
    public String createNewDocument(GTData data) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(data);
        Index request = new Index.Builder(json).index(INDEX_NAME).type(data.getType().toString()).build();

        JestResult result = client.execute(request);
        return result.getJsonObject().get("_id").toString().replace("\"", "");
    }

    /**
     * getDocument - Get a single document by ID. Will downcast to correct object type.
     *
     * @param ID - ID of the document
     * @return GTData object
     */
    public GTData getDocument(String ID, Type type) throws IOException {
        Get request = new Get.Builder(INDEX_NAME, ID).build();

        JestResult result = client.execute(request);

        GTData data = null;
        if (type.equals(Bid.class)) {
            data = result.getSourceAsObject(Bid.class);
        } else if (type.equals(Task.class)) {
            data = result.getSourceAsObject(Task.class);;
        } else if (type.equals(User.class)) {
            data = result.getSourceAsObject(User.class);;
        } else if (type.equals(BidList.class)) {
            data = result.getSourceAsObject(BidList.class);;
        }
        return data;
    }

    /**
     * deleteDocument - deletes a document with the provided id of provided type
     *
     * @param ID - ID of document
     * @param type - type of document
     * @return - response code of deletion (200 on success, 400 on failure)
     */
    public int deleteDocument(String ID, Class<Bid> type) throws IOException {
        return client.execute(new Delete.Builder(ID).index(INDEX_NAME).type(type.toString()).build()).getResponseCode();
    }

    /**
     * searchBids - Method that searches the elasticSearch database and returns a list of bid objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of Bid objects
     */
    public List<? extends GTData> search(String query, Type type) throws IOException {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType(type.toString()).build();

        SearchResult result = client.execute(search);

        List<? extends GTData> dataList = null;
        if (type.equals(Bid.class)) {
            dataList = result.getSourceAsObjectList(Bid.class);
        } else if (type.equals(Task.class)) {
            dataList = result.getSourceAsObjectList(Task.class);;
        } else if (type.equals(User.class)) {
            dataList = result.getSourceAsObjectList(User.class);;
        } else if (type.equals(BidList.class)) {
            dataList = result.getSourceAsObjectList(BidList.class);;
        }
        return dataList;
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
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder(SERVER_ADDRESS);
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);

            client = (JestDroidClient) factory.getObject();
        }
    }

    /**
     * Turn off client
     */
    public void shutDown() {
        if(client != null) {
            client.shutdownClient();
        }
    }

    public void setTestSettings(String testServerAddress) {
        this.INDEX_NAME = testServerAddress;
    }
}
