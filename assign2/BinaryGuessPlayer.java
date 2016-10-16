import java.io.*;
import java.util.ArrayList;

/**
 * Binary-search based guessing player.
 * This player is for task C.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class BinaryGuessPlayer extends Game
{
	
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
    	super(gameFilename, chosenName);
    }


    public Guess guess() {
    	if (opponentPersons.size() == 1) {
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }

    	/* 
    	 * 1. Get half number of possible persons
    	 * 2. Iterate over all possible attribute-value pairs
    	 * 3. Determine attribute-value pair that is closest to half (1.) 
    	 */
    	
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

        if (answer)
            // if the guess was correct we only want to retain the matching Persons
            opponentPersons.retainAll(matchingPersons);
        else
            // if the guess wasn't correct we need to remove all matching Persons
            opponentPersons.removeAll(matchingPersons);

        // Remove the attribute from the pool of guessable ones
        attrValToPersonsMap.remove(attrVal);

        // Remove required persons from attribute-value pairs (attrValToPersonsMap)
        for (String currAttrVal : attrValToPersonsMap.keySet()) {
            ArrayList<Person> persons = attrValToPersonsMap.get(currAttrVal);
            if (answer)
                persons.retainAll(matchingPersons);
            else
                persons.removeAll(matchingPersons);
        }

        return false;
    }

}
