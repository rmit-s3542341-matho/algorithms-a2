import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Game  implements Player 
{
	protected ArrayList<Person> allPersons = new ArrayList<Person>(); // This would be a set however Java sets do not have a .get method
	private HashMap<String, ArrayList<String>> allAttributes = new HashMap<String, ArrayList<String>>();
	
	protected Person chosenPerson;
	protected ArrayList<Person> opponentPersons;
    // Collection of persons who have attribute-value pair
    // i.e. "hairColor black" ~> P1, P2, P3
	protected HashMap<String, ArrayList<Person>> attrValToPersonsMap;
	
	/**
     * Loads the game configuration from gameFilename, and also store the chosen
     * person.
     *
     * @param gameFilename Filename of game configuration.
     * @param chosenName Name of the chosen person for this player.
     * @throws IOException If there are IO issues with loading of gameFilename.
     *    Note you can handle IOException within the constructor and remove
     *    the "throws IOException" method specification, but make sure your
     *    implementation exits gracefully if an IOException is thrown.
     */
    public Game(String gameFilename, String chosenName)
            throws IOException
    {
    	attrValToPersonsMap = new HashMap<String, ArrayList<Person>>();
        
    	readGameConfig(gameFilename, attrValToPersonsMap);
    	chosenPerson = getPerson(chosenName);
        // Make a mutable copy of all persons
        opponentPersons = new ArrayList<Person>(allPersons);
    }
    
    
    // Helpers
	public Person getPerson(String name) {
		for (Person person : allPersons) {
			if (person.name.equals(name))
				return person;
		}
		return null;
	}
	
    protected static double distanceBetween(double base, double toCheck) {
    	double answer;    	
    	if (base > toCheck) 
    		answer = base - toCheck;
    	else 
    		answer = toCheck - base;
    	return answer;
    }
	
    // File reading
	public void readGameConfig(String fileName, HashMap<String, ArrayList<Person>> attrValToPersonsMap)
			throws IOException
	{
		int lineNoStart = readAttributes(fileName);
		readPersons(fileName, lineNoStart, attrValToPersonsMap);
	}
	
	private int readAttributes(String fileName) throws IOException
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
		  		final String attrName = fields[0];
		  		allAttributes.put(attrName, new ArrayList<String>());
		  		
		  		for (int i = 1; i < fields.length; i++) {
					allAttributes.get(attrName).add(fields[i]);
		  		}
		  	}
		  	else {
		  		// Break after transition to persons data
		  		break;
		  	}
        }
		assignedReader.close();
        
		return lineNo;
	}
	
	private void readPersons(String fileName, int lineNoStart, HashMap<String, ArrayList<Person>> attrValToPersonsMap)
			throws IOException
	{
        BufferedReader assignedReader = new BufferedReader(new FileReader(fileName));
        String line;
        int lineNo = 0;
        Person currentPerson = null;
        
        while ((line = assignedReader.readLine()) != null )
        {	
        	lineNo++;
        	
        	// Start reading at provided line number
        	if (lineNo >= lineNoStart) {
        		String[] fields = line.split(" ");
        		
        		// Skip if there's a blank line
        		if (fields[0].isEmpty()) {
        			continue;
        		}
        		
        		// Read person
        		if (fields.length == 1) {
        			currentPerson = new Person(fields[0]); 
        			allPersons.add(currentPerson);
        		}
        		else if (currentPerson != null) {
        			// Read person's attribute and value
        			currentPerson.attributes.put(fields[0], fields[1]);

					// Add person to attribute-value ~> persons map
					if (!attrValToPersonsMap.containsKey(line))
						attrValToPersonsMap.put(line, new ArrayList<Person>());
					attrValToPersonsMap.get(line).add(currentPerson);
        		}
        	}
        }
        assignedReader.close();
    } 
}
