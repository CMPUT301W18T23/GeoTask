package com.geotask.myapplication;

import com.geotask.myapplication.DataClasses.Task;

import org.junit.Test;

import java.util.ArrayList;


public class TestTask {

//	public static void main(String[] args) {
//		testTask();

//	}
	@Test
	public static void testTask() {
		testEntered();
		testChanged();
		testEmptyPicture();
		testAddPicture();
		testDeletePicture();
		testDigits();
//		testNotNegative();  // can't test these here
//		testStringConstraints();
//		testNotEmpty();

	}
	public static void testEntered() {
		String Name = "jejewitt";
		String Description  = "i need someone to test this task";
		String Status = "Requested";
		String Provider = "James";
		String Requester = "Jewitt";
		Task task = new Task(Name, Description, "lolol");
		task.setRequesterID(Provider);
		task.setAcceptedProviderID(Requester);
		assert(Name == task.getName());
		assert(Description  ==  task.getDescription());
		assert(Status == task.getStatus());
		assert(Provider == task.getRequesterID());
		assert(Requester == task.getAcceptedProviderID());
		System.out.print(task.getName()+" "+  task.getDescription() + " "+ task.getStatus()+ " "+ task.getRequesterID() + " " + task.getAcceptedProviderID());
	}
	
	public static void testChanged() { //check if adding new data
		String Name = "jejewitt";
		String Description  = "i need someone to test this task";
		String Status = "Requested";
		String newName = "notjejewitt";
		String newDescription  = "i will do it myself";
		String newStatus = "Accepted";
		String Provider = "James";
		String Requester = "Jewitt";
		String newProvider = "notJames";
		String newRequester = "notJewitt";
		Task task = new Task(Name, Description, ";p;pp;");
		task.setName(newName);
		task.setDescription(newDescription);
		task.setStatus(newStatus);
		task.setRequesterID(Provider);
		task.setAcceptedProviderID(Requester);
		task.setRequesterID(newProvider);
		task.setAcceptedProviderID(newRequester);
		assert(newName == task.getName());
		assert(newDescription  ==  task.getDescription());
		assert(newStatus == task.getStatus());
		assert(newProvider == task.getRequesterID());
		assert(newRequester == task.getAcceptedProviderID());
		System.out.print(task.getName()+" "+  task.getDescription() + " "+ task.getStatus()+ " "+ task.getRequesterID() + " " + task.getAcceptedProviderID());
	}
	
	
	//testing pictures
	public static void testEmptyPicture() {
		Task task = new Task("jejewitt", "i need someone to test this task", "loloo");
		assert(task.getPictures().isEmpty());   //need to hide because this will fail otherwise	
	}
	public static void testAddPicture() {
		Task task = new Task("jejewitt", "i need someone to test this task", "lolool");
		String picture = "/test/a/b";
		ArrayList<String> pic = new ArrayList<String>();
		task.setPicture(picture); //need to add picture to
		pic = task.getPictures();
		assert(pic.contains(picture));
	}
	
	public static void testDeletePicture() {
		Task task = new Task("jejewitt", "i need someone to test this task", "lolol");
		String picture = "/test/a/b";
		ArrayList<String> pic = new ArrayList<String>();
		task.setPicture(picture); //need to add picture to
		task.deletePicture(picture);
		assert(!(pic.contains(picture)));
	}
	
	public static void testDigits() {
		Task task = new Task("jejewitt", "i need someone to test this task", "lolollol");
		Double ammount = 2.22;
		task.setAcceptedBid(ammount);
		assert(task.getAcceptedBid() == ammount);
	}
	//cant test becase data structure does not manage constraints
//	public static void testNotNegative() {
//		Task task = new Task("jejewitt", "i need someone to test this task");
//		Double ammount = -2.22;
//		task.setAcceptedBid(ammount);
//		assert(task.getAcceptedBid() == null);
//	}
//	
//	public static void testStringConstraints() { //test if too long
//		String Name = "0123456789012345678901234567890123456789"; //40 char string
//		String Description  = "01234567890123456789012345678901234567890123456789"
//				+ "012345678901234567890123456789012345678901234567890123456789"
//				+ "012345678901234567890123456789012345678901234567890123456789"
//				+ "012345678901234567890123456789012345678901234567890123456789"
//				+ "012345678901234567890123456789012345678901234567890123456789"
//				+ "012345678901234567890123456789012345678901234567890123456789"
//				+ "012345678901234567890123456789012345678901234567890123456789"
//				+ "0123456789012345678901234567890123456789"; //450 char long description
//		Task task = new Task(Name, Description);
//		assert(task.getName() == null);
//		assert(task.getDescription() == null);
//
//	}
//	public static void testNotEmpty() { //will return null if not empty
//		String Name = "";
//		String Description  = "";
//		Task task = new Task(Name, Description);
//		assert(task.getName() == null);
//		assert(task.getDescription() == null);
//	}
}
