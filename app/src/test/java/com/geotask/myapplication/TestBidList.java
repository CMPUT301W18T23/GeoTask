package com.geotask.myapplication;

import org.junit.Test;

public class TestBidList {

    @Test
    public void BidListTest() {
        testAdd();
        testDelete();
        testGet();
        testGetNum();
    }


    @Test
    public void testGetNum(){
        String Task = "ZZZZ9999";
        String Provider = "abcd1234";
        Double Value = 1.01;
        BidList testBidList = new BidList(Task);
        Bid bid = new Bid(Provider, Value, Task);

        testBidList.addBid(bid);
        assert(testBidList.getNumBids() == 1);
        testBidList.addBid(bid);
        assert(testBidList.getNumBids() == 2);
        testBidList.addBid(bid);
        assert(testBidList.getNumBids() == 3);
    }

    @Test
    public void testAdd(){
        String Task = "ZZZZ9999";
        String Provider = "abcd1234";
        Double Value = 1.01;
        BidList testBidList = new BidList(Task);
        Bid bid = new Bid(Provider, Value, Task);
        testBidList.addBid(bid);

        assert(testBidList.getBid(0).getTaskID() == Task);
        assert(testBidList.getBid(0).getValue() == Value);
        assert(testBidList.getBid(0).getTaskID() == Task);
        assert(testBidList.getNumBids() == 1);
    }


    @Test
    public void testDelete(){
        String Task = "ZZZZ9999";
        String Provider = "abcd1234";
        Double Value = 1.01;
        BidList testBidList = new BidList(Task);
        Bid bid = new Bid(Provider, Value, Task);

        testBidList.addBid(bid);
        assert(testBidList.getNumBids() == 1);
        testBidList.deleteBid(0);
        assert(testBidList.getNumBids() == 0);
    }

    @Test
    public void testGet(){
        String Task = "ZZZZ9999";
        String Provider = "abcd1234";
        Double Value = 1.01;
        BidList testBidList = new BidList(Task);
        Bid bid = new Bid(Provider, Value, Task);

        testBidList.addBid(bid);
        assert(testBidList.getBid(0).getTaskID() == bid.getTaskID());
        assert(testBidList.getBid(0).getProviderID() == bid.getProviderID());
        assert(testBidList.getBid(0).getValue() == bid.getValue());

    }





}
