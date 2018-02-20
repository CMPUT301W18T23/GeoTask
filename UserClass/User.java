package placeholder;

/*
 * User information class
 * 
 */

public class User {
	String adress;
	String about;
	String name;
	String phone;
	
	//constructors. currently accepts depending on information given
	public User(String Name, String Adress, String About, String Phone) {
		this.name = Name;
		this.adress = Adress;
		this.about = About;
		this.phone = Phone;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	public String getAdress() {
		return this.adress;
	}
	public void setAdress(String Adress) {
		this.adress = Adress;
	}
	public String getAbout() {
		return this.about;
	}
	public void setAbout(String About) {
		this.about = About;
	}
	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String Phone) {
		this.phone = Phone;
	}
	
}
