package com.geotask.myapplication;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by MT on 2/19/2018.
 */

public class ElasticsearchBasicTest {

    ElasticsearchController controller = new ElasticsearchController();

    @Test
    public void testCreateIndex() {
        controller.deleteIndex();
        controller.createIndex();
    }

    @Test
    public void testInsertProfile(){
        String docID = controller.insertNewProfile("apple", "123@gmail.com","123123");
        System.out.println(docID);
    }

    @Test
    public void testDeleteProfile() {
        String docID = controller.insertNewProfile("Michael Tang", "123@gmail.com","123123");
        System.out.println(docID);
        controller.deleteProfile(docID);
    }

    @Test
    public void testInsertNewTask() {
        ArrayList<String> bidList = new ArrayList<>();
        bidList.add("ID1");
        String docID = controller.insertNewTask("insertnewtask", "what", "1234",
                                                bidList, new ArrayList<Byte>(), "up");

        System.out.println(docID);
    }

    @Test
    public void testAddLocation() {
        ArrayList<String> bidList = new ArrayList<>();
        bidList.add("ID1");
        String docID = controller.insertNewTask("michael", "what", "complete",
                                                bidList, new ArrayList<Byte>(), "up");

        controller.addLocation(docID, "left");
    }

    @Test
    public void testUpdateTask() {
        ArrayList<String> bidList = new ArrayList<>();
        bidList.add("ID1");
        String docID = controller.insertNewTask("oldmichael", "oldwhat", "oldcomplete",
                                                bidList, new ArrayList<Byte>(), "old");

        String[] name = new String[]{"requester", "newmichael"};
        String[] description = new String[]{"description", "newwhat"};
        controller.updateTask(docID, name, description);
    }

    @Test
    public void testInsertNewBid() {
        ArrayList<String> bidList = new ArrayList<>();
        bidList.add("ID1");
        String docID = controller.insertNewTask("bidtest", "oldwhat", "oldcomplete",
                bidList, new ArrayList<Byte>(), "old");

        bidList.add("ID2");
        controller.insertNewBid(docID, bidList);
    }

    @Test
    public void testExistsProfile() {
        boolean result = controller.existsProfile("123@gmail.com");
        System.out.println(result);
    }

    @Test
    public void testBasicSearch() {
        ArrayList<String> tuple = new ArrayList<>();
        tuple.add("email");
        tuple.add("12364gmail46com");
        ArrayList<ArrayList<String>> list = new ArrayList<>();
        list.add(tuple);
        SearchResponse response = controller.basicSearch("profile", list);
        SearchHit[] results = response.getHits().getHits();
        for(SearchHit result:results){
            System.out.println(result.getSourceAsString());
        }
    }
}
