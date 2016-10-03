import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
	
public class Person {
	// All persons in the game
	private static ArrayList<Person> persons;
	
	private String name;

	// Constructor
	public Person(String name) {
		this.name = name;
	}
	
	// Getters
	public String getName() {
		return name;
	}
	
	// Methods
	static public void readGameConfig(String fileName) 
	{
		try {
			int lineNoStart = readAttributes(fileName);
			readPersons(fileName, lineNoStart);
			
		} catch(IOException ioE) {
			System.out.println(ioE.getMessage());
		}
	}
	
	static private int readAttributes(String fileName) throws IOException
	{
        BufferedReader assignedReader = new BufferedReader(new FileReader(fileName));
        String line;
        int lineNo = 0;
        
        while ((line = assignedReader.readLine()) != null )
        {	
        	lineNo++;
        	
            String[] fields = line.split(" ");
            
		  	if (fields.length > 1) {
		  		// Reading attribute and values
		  		String attrName = fields[0];
		  		ArrayList<String> tempVals = new ArrayList<String>();
		  		
		  		for (int i = 1; i < fields.length; i++) {
		  			tempVals.add(fields[i]);
		  		}
		  		
		  		System.out.println(attrName + ": " + tempVals.toString());
		  	}
		  	else {
		  		// Break after transition to persons data
		  		break;
		  	}
        }
		assignedReader.close();
        
		return lineNo;
	}
	
	static private void readPersons(String fileName, int lineNoStart) throws IOException
	{
        BufferedReader assignedReader = new BufferedReader(new FileReader(fileName));
        String line;
        int lineNo = 0;
        
        while ((line = assignedReader.readLine()) != null )
        {	
        	lineNo++;
        	
        	// Start reading at provided line number
        	if (lineNo >= lineNoStart) {
        		String[] fields = line.split(" ");
        		
        		// Read person name TODO not perfect as it reads in the empty lines too
        		if (fields.length == 1) {
        			System.out.println(fields[0]);
        		}
        		else {
        			// Read person's attribute and value
        			System.out.println(fields[0] + ": " + fields[1]);
        		}
        	}
        }
        assignedReader.close();
    }

}
