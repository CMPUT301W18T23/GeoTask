package com.geotask.myapplication.DataClasses;

import java.util.ArrayList;

public class PictureList{
	private ArrayList<String> photoList;

	public PictureList() {
		this.photoList = new ArrayList<String>();
	}
	
	public ArrayList<String> getPictures() { 
		return this.photoList; 
	}
	public void setPicture(String Picture) { 	
		this.photoList.add(Picture);
	}
	public void deletePicture(String Picture) { 
		this.photoList.remove(Picture);
	}

}
