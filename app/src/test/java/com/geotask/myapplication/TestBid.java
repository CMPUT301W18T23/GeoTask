package com.geotask.myapplication;

import org.junit.Test;

public class TestBid {

	
//	public static void main(String[] args) {

//		BidTest();
//	}

	@Test
	public static void BidTest() {
		testEntry();
		testChange();
	}
	public static void testEntry(){
		String Provider = "abcd1234";
		Double Value = 1.01;
		String Task = "ZZZZ9999";
		Bid bid = new Bid(Provider, Value, Task);
		assert(bid.getProviderID() == Provider);
		assert(bid.getValue() == Value);
		assert(bid.getTaskID() == Task);
		System.out.print(bid.getProviderID() + " "+ bid.getValue() + "" + bid.getTaskID());
	}
	public static void testChange() {
		String Provider = "abcd";
		Double Value = 1.01;
		String Task = "ZZZZ";
		String newProvider = "1234";
		Double newValue = 2.01;
		String newTask = "9999";
		Bid bid = new Bid(Provider, Value, Task);
		bid.setProviderID(newProvider);
		bid.setTaskID(newTask);
		bid.setValue(newValue);
		assert(bid.getProviderID() == newProvider);
		assert(bid.getValue() == newValue);
		assert(bid.getTaskID() == newTask);
		System.out.print(bid.getProviderID() + " "+ bid.getValue() + "" + bid.getTaskID());
	}

}
