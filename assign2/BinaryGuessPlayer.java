import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Binary-search based guessing player.
 * This player is for task C.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class BinaryGuessPlayer extends Game
{
	private Person chosenPerson;
    private ArrayList<Person> opponentPersons;
    // Collection of persons who have attribute-value pair
    // i.e. "hairColor black" ~> P1, P2, P3
    private HashMap<String, ArrayList<Person>> attrValToPersonsMap;
	
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
    public BinaryGuessPlayer(String gameFilename, String chosenName)
        throws IOException
    {
    	attrValToPersonsMap = new HashMap<>();
        
    	readGameConfig(gameFilename, attrValToPersonsMap);
    	chosenPerson = getPerson(chosenName);
        // Make a mutable copy of all persons
        opponentPersons = new ArrayList<Person>(allPersons);
    }


    public Guess guess() {
    	if (opponentPersons.size() == 1) {
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }

    	/* 1. Get half number of possible persons
    	 * 2. Iterate over all possible attribute-value pairs
    	 * 3. Determine attribute-value pair that is closest to half (1.) */
    	
        double half = (double) opponentPersons.size() / 2;
        double distance = 0;
        double bestDistance = opponentPersons.size();
        String attrValueToGuess = "";
        
        // Iterate over attribute-values remaining
        for (String attrVal : attrValToPersonsMap.keySet()) {
        	// Find the attribute-value that has closest to half no. of persons
        	distance = distanceBetween(half, attrValToPersonsMap.get(attrVal).size());
        	if (distance == 0) {
        		// Exactly half
                bestDistance = distance;
        		attrValueToGuess = attrVal;
        		break;
        	}
        	else if (distance < bestDistance) {
        		bestDistance = distance;
        		attrValueToGuess = attrVal;
        	}
        }

        // Split attribute-value string
        String[] attrValue = attrValueToGuess.split(" ");
        return new Guess(Guess.GuessType.Attribute, attrValue[0], attrValue[1]);
    }  
    
	public boolean answer(Guess currGuess) {
		if (currGuess.getType() == Guess.GuessType.Person)
            // Check if guessing for person
    		return currGuess.getValue().equals(chosenPerson.name);
    	else
    		// Guessing for attribute
    		return chosenPerson.hasAttribute(currGuess.getAttribute(), currGuess.getValue());
    }


	public boolean receiveAnswer(Guess currGuess, boolean answer) {
		if (currGuess.getType() == Guess.GuessType.Person && answer)
            return true;

	    String attrVal = currGuess.getAttribute() + " " + currGuess.getValue();
        ArrayList<Person> matchingPersons = attrValToPersonsMap.get(attrVal);
        ArrayList<Person> personsToRemove = new ArrayList<Person>(opponentPersons);

        if (answer) {
        	// Set to remove all persons that don't have attribute-value
        	personsToRemove.removeAll(matchingPersons);
        }
        else {
        	// Set to remove all persons that have attribute-value
            personsToRemove.retainAll(matchingPersons);
        }
        opponentPersons.removeAll(personsToRemove);

        // Remove the attribute from the pool of guessable ones
        attrValToPersonsMap.remove(attrVal);

        // Remove required persons from attribute-value pairs (attrValToPersonsMap)
        for (String currAttrVal : attrValToPersonsMap.keySet()) {
            ArrayList<Person> persons = attrValToPersonsMap.get(currAttrVal);
            persons.removeAll(personsToRemove);
        }

        return false;
    }

}
