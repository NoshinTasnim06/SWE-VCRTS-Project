package vccontroller;

public class Person {
	private String fname;
	private String lname;
	
	public Person(String fname, String lname) {
		this.fname = fname;
		this.lname = lname;
	}
	
	public String getName() {
		return fname + " " + lname;
	}
}
