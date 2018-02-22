package data;

public class Profile {
	private String name;
	private String email;
	private String phone;
	
	public Profile(String Name, String Email, String Phone) {
		this.name = Name;
		this.email = Email;
		this.phone = Phone;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String Name) {
		this.name = Name;
	}
	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String Phone) {
		this.phone = Phone;
	}
	public void setEmail(String Email) {
		this.email = Email;
	}
	public String getEmail() {
		return this.email;
	}

	
}
