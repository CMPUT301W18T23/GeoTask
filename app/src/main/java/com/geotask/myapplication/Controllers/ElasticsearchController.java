package com.geotask.myapplication.Controllers;

import com.geotask.myapplication.DataClasses.GTData;
import com.google.gson.Gson;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;

import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;

/*
API DOCUMENTATION
https://github.com/searchbox-io/Jest/tree/master/jest
 */


public class ElasticsearchController {
    private static JestDroidClient client;
    private final static String INDEX_NAME = "cmput301w18t23";

    public static void createNewDocument(GTData data){
        Gson gson = new Gson();
        String json = gson.toJson(data);
        Index request = new Index.Builder(json).index(INDEX_NAME).type(data.getClass().toString()).build();

        System.out.println(request.toString());
        try {
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GTData getDocument(String ID, String type) {
        Get request = new Get.Builder(INDEX_NAME, ID).type(type).build();

        try {
            JestResult result = client.execute(request);
            GTData data = result.getSourceAsObject(GTData.class);
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteDocument(String ID, String type) {
        try {
            client.execute(new Delete.Builder(ID).index(INDEX_NAME).type(type).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void search(String type, ArrayList<ArrayList<String>> terms){
    }

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
