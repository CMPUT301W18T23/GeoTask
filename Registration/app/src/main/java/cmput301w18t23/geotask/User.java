package cmput301w18t23.geotask;

/*
 * User information class to send to elastic search
 *
 */

public class User {
    String adress;
    String about;
    String name;
    String phone;
    String password;
    //constructors. currently accepts depending on information given
    public User(String Name, String Adress, String About, String Phone, String Password) {
        this.name = Name;
        this.adress = Adress;
        this.about = About;
        this.phone = Phone;
        this.password = Password;
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
    public void setPhone(String Phone) {this.phone = Phone;}
    public void setPassword(String Password) { this.password = Password; }
    public String getPassword() {
        return this.password;
    }
}
