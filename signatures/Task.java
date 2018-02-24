package data;

public class Task {
	String name;
	String description;
	String status;
	//i am not sure of what datatype for pictures
	public Task(String Name, String Description, String Status) { //need string for pictures 
		this.name = Name;
		this.description = Description;
		this.status = Status;
		//another storage device for pictures
	}
	public String getName() {
		return this.name;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String Description) {
		this.description = Description;
	}
	public String getStatus() {
		return this.status;
	}
	public void setStatus(String Status) {
		this.status = Status;
	}
	public void getPictures() { //will eventually return a list of picture
		
	}
	public void setPicture() { 	//
		
	}
	public void deletePicture() { //will delete picture given to data - needs to send updated array
		
	}
	
	
}
