package com.geotask.myapplication.DataClasses;

import java.util.ArrayList;

/**
 *holds the location of picurures to load
 */
public class PictureList{
	private ArrayList<String> photoList;
	/**
	 *constructor
	 * inits a new arraylist of strings for the locations of photos
	 */
	public PictureList() {
		this.photoList = new ArrayList<String>();
	}

	/**
	 *returns an arrayList of pictures to return
	 * @reutrn photoList
	 */
	public ArrayList<String> getPictures() { 
		return this.photoList; 
	}
	/**
	 *adds a new picture to the list
	 * @param Picture
	 */
	public void setPicture(String Picture) { 	
		this.photoList.add(Picture);
	}
	/**
	 *deletes a given picture
	 * @param Picture
	 */
	public void deletePicture(String Picture) { 
		this.photoList.remove(Picture);
	}

}
