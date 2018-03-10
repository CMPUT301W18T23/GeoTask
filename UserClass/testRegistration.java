package placeholder;

public class testRegistration {

	public static void main(String[] args) {
		User a = new User("a","b","c","d");
		
		PRINT(a);
		a.setName("e");
		a.setAdress("f");
		a.setAbout("g");
		a.setPhone("h");
		PRINT(a);

		}
	public static void PRINT(User a) {
		System.out.println(a.getName());
		System.out.println(a.getAdress());
		System.out.println(a.getAbout());
		System.out.println(a.getPhone());

	}

}
