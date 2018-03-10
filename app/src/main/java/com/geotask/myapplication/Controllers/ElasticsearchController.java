package com.geotask.myapplication.Controllers;

import android.os.StrictMode;
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

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Index request = new Index.Builder(json).index(INDEX_NAME).type(data.getType()).build();

        JestResult result = client.execute(request);
        return result.getJsonObject().get("_id").toString().replace("\"", "");
    }

    /**
     * getDocument - Get a single document by ID. Will downcast to correct object type.
     *
     * @param ID - ID of the document
     * @return GTData object
     */
    public GTData getDocument(String ID, String type) throws IOException {
        Get request = new Get.Builder(INDEX_NAME, ID).build();

        JestResult result = client.execute(request);

        GTData data = null;
        if (type.equals("bid")) {
            data = result.getSourceAsObject(Bid.class);
        } else if (type.equals("task")) {
            data = result.getSourceAsObject(Task.class);;
        } else if (type.equals("user")) {
            data = result.getSourceAsObject(User.class);;
        } else if (type.equals("bidList")) {
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
    public int deleteDocument(String ID, String type) throws IOException {
        return client.execute(new Delete.Builder(ID).index(INDEX_NAME).type(type).build()).getResponseCode();
    }

    /**
     * searchBids - Method that searches the elasticSearch database and returns a list of bid objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of Bid objects
     */
    public List<? extends GTData> search(String query, String type) throws IOException {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType("user").build();

        SearchResult result = client.execute(search);

        List<? extends GTData> dataList = null;
        if (type.equals("bid")) {
            dataList = result.getSourceAsObjectList(Bid.class);
        } else if (type.equals("task")) {
            dataList = result.getSourceAsObjectList(Task.class);;
        } else if (type.equals("user")) {
            dataList = result.getSourceAsObjectList(User.class);;
        } else if (type.equals("bidList")) {
            dataList = result.getSourceAsObjectList(BidList.class);;
        }
        return dataList;
    }

    /**
     * searchTasks - Method that searches the elasticSearch database and returns a list of task objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of Task objects
     */
    public List<Task> searchTasks(String query) throws IOException {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType("task").build();

        SearchResult result = client.execute(search);
        List<Task> tasks = result.getSourceAsObjectList(Task.class);
        return tasks;
    }

    /**
     * searchUsers - Method that searches the elasticSearch database and returns a list of profile objects
     *
     * @param query - query of terms that has been already formatted
     * @return a list of profile objects
     */
    public List<User> searchUsers(String query) throws IOException {
        Search search = new Search.Builder(query).addIndex(INDEX_NAME).addType("user").build();

        SearchResult result = client.execute(search);
        List<User> users = result.getSourceAsObjectList(User.class);
        return users;
    }

    /**
     * existsProfile - Method for checking if an email is in use by another user
     *
     * @param email - email of the registering use
     * @return true if the email is in use, false otherwise
     */
    public boolean existsProfile(String email) {
        SuperBooleanBuilder query = new SuperBooleanBuilder();
        query.put("email", ElasticsearchController.convertEmailForElasticSearch(email));
        Search search = new Search.Builder(query.toString()).addIndex(INDEX_NAME).addType("user").build();
        try {
            Log.i("----->", query.toString());
            Log.i("----->", client.execute(search).getJsonObject().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            //List<User> searchResult = (List<User>) this.search(query.toString(), "user");
            //Log.i("------>", searchResult.toString());
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

    public static String convertEmailForElasticSearch(String email){
        String convertedEmail = "";
        for(int i = 0; i < email.length(); i++){
            int character = (int) email.charAt(i);
            convertedEmail += Integer.toString(character) + "c";
        }
        return convertedEmail;
    }

    public static String revertEmailFromElasticSearch(String convertedEmail){
        String email = "";
        for(String character : convertedEmail.split("c")){
            int intCharacter = Integer.valueOf(character);
            email += (char) intCharacter;
        }
        return email;
    }
}
