import java.util.*;
	
public class Person {
	public String name;
	public HashMap<String, String> attributes;
	
	// Constructor
	public Person(String name) {
		this.name = name;
		attributes = new HashMap<String, String>(); 
	}
}
