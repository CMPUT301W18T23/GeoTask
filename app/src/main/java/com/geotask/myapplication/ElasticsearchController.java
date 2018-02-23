package com.geotask.myapplication;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MT on 2/19/2018.
 */

public class ElasticsearchController {

    private final String HOST_NAME = "cmput301.softwareprocess.es";
    private final String INDEX_NAME =  "cmput301w18t23";
    private RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost(HOST_NAME, 8080, "http")));


    public void createIndex(){
        CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
        try {
            CreateIndexResponse createIndexResponse = client.indices().create(request);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    //DO NOT use on production index
    public void deleteIndex() {
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX_NAME);
        try {
            DeleteIndexResponse deleteIndexResponse = client.indices().delete(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to check if a user has already registered with an email address.
     *
     * NOTE: We need to decide where to call this
     *
     * references: - https://stackoverflow.com/questions/30369962/getting-the-result-of-a-searchresponse-in-elasticsearch
     *             - https://www.programcreek.com/java-api-examples/index.php?api=org.elasticsearch.search.builder.SearchSourceBuilder
     *
     * @param email - email of the client
     * @return True if email is in system, False otherwise
     */
    public boolean existsProfile(String email){
        QueryBuilder existsProfileQuery = QueryBuilders.termQuery("email", email); //the query (email field = to new email
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();                   //sourcebuilder object
        sourceBuilder.query(existsProfileQuery);                                         //add the query to the sourcebuilder object
        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);                     //create the searchRequest object on the objecr
        searchRequest.source(sourceBuilder);                                             //add the builder to the searchRequest

        try {
            SearchResponse response = client.search(searchRequest);                      //perform the query
            if(response == null){
                return false;
            }

            /* //maybe can just check if null?
            SearchHit[] results = response.getHits().getHits();                          //get the results
            if(results.length == 0){
                return false;
            } else {
                return true;
            }
            */

        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /*
    insert profile into elasticsearch and returns the document id
    assumes profile does not exist. will create duplicate document otherwise
     */
    public String insertNewProfile(String name, String email, String phoneNumber) {
        Map<String, Object> jsonMap = new HashMap<>();
        //jsonMap.put("type", "profile"); not required for this server version
        jsonMap.put("name", name);
        jsonMap.put("email", email);
        jsonMap.put("phoneNumber", phoneNumber);

        // type is required by server, can be removed safely for newer server versions
        IndexRequest request = new IndexRequest(INDEX_NAME,"profile").source(jsonMap);

        IndexResponse response;
        try {
            response = client.index(request);
            return response.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteProfile(String ID) {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, "profile", ID);

        try {
            DeleteResponse response = client.delete(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProfile(String docID, ArrayList<String[]> updateList){

    }

    public String insertNewTask(String requester, String description, String status,
                                ArrayList<String> bidList, ArrayList<Byte> picturePaths, String location) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("requester", requester);
        jsonMap.put("description", description);
        jsonMap.put("status", status);
        jsonMap.put("bid_list", bidList);
        jsonMap.put("location", location);
        jsonMap.put("accepted_bid_provider", "");
        //TODO process pictures into bytes
        List<Byte> pictures = new ArrayList<Byte>();
        pictures.add((byte) 0b00100001);
        jsonMap.put("pictures", pictures);

        IndexRequest request = new IndexRequest(INDEX_NAME, "task").source(jsonMap);

        IndexResponse response;
        try {
            response = client.index(request);
            return response.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteTask(String ID) {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, "task", ID);

        try {
            DeleteResponse response = client.delete(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTask(String ID, String[]... fields){
        Map<String, Object> jsonMap = new HashMap<>();
        for(String[] field: fields) {
            jsonMap.put(field[0], field[1]);
        }

        UpdateRequest request = new UpdateRequest(INDEX_NAME, "task", ID).doc(jsonMap);

        try {
            UpdateResponse updateResponse = client.update(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPictures(String... picturePath) {
        //TODO process pictures
    }

    public void addLocation(String ID, String location){
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("location", location);

        UpdateRequest request = new UpdateRequest(INDEX_NAME, "task", ID).doc(jsonMap);

        try {
            UpdateResponse response = client.update(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //painless scripts not available on this server
    /*public void insertNewBid(String ID, Double bid) {
        UpdateRequest request = new UpdateRequest(INDEX_NAME, "task", ID);

        Map<String, Object> parameters = Collections.singletonMap("bid", (Object) bid);
        Script inline = new Script(ScriptType.INLINE, "painless", "ctx._source.bid_list += params.bid", parameters);
        request.script(inline);

        try {
            UpdateResponse response = client.update(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public void insertNewBid(String ID, ArrayList<String> bidList){
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("bid_list", bidList);

        UpdateRequest request = new UpdateRequest(INDEX_NAME, "task", ID).doc(jsonMap);

        try {
            UpdateResponse response = client.update(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getProfile(String ID) {
        GetRequest request = new GetRequest(INDEX_NAME, "profile", ID);

        GetResponse response = null;
        try {
            response = client.get(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    public String insertNewBid(String providerID, String TaskID) {
        Map<String, Object> jsonMap = new HashMap<>();
        IndexRequest request = new IndexRequest(INDEX_NAME, "bid").source(jsonMap);

        try {
            IndexResponse response = client.index(request);
            return response.getId();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //TODO search

}
