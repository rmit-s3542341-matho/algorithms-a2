import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Random guessing player.
 * This player is for task B.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class RandomGuessPlayer extends Game
{
	private Person chosenPerson;
    private ArrayList<Person> opponentPersons;
    private ArrayList<String> guessedAttrValPairs;
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
    public RandomGuessPlayer(String gameFilename, String chosenName)
        throws IOException
    {
        attrValToPersonsMap = new HashMap<>();
        guessedAttrValPairs = new ArrayList<String>();
        
    	readGameConfig(gameFilename, attrValToPersonsMap);
    	chosenPerson = getPerson(chosenName);
        // Make a mutable copy of all persons
        opponentPersons = new ArrayList<Person>(allPersons);
    }


    public Guess guess()
    {
        if (opponentPersons.size() == 1) {
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }
        
    	/* 1. Randomly select a person from opponentPersonss
    	 * 2. Randomly select an attribute-value pair of that person
    	 * 3. Store the attribute-value pair that has been guessed */
    	
    	Random rand = new Random();
    	Guess guess = null;
    	
    	// Grab a random person
    	int randPersonIndex = rand.nextInt(opponentPersons.size());
    	Person guessPerson = opponentPersons.get(randPersonIndex);
    	
    	// Loop until an attribute-value pair is found that hasn't been guessed
    	do {
        	// Grab a random attribute from the person
        	int randAttributeIndex = rand.nextInt(guessPerson.attributes.size());
        	int i = 0;
        	
        	// Iterate over all of the person's attributes
        	for (Map.Entry<String, String> entry : guessPerson.attributes.entrySet()) {
        		String attr = entry.getKey();
        		String value = entry.getValue();        		
        	    String attrValPair = attr + value;
        	    
        	    // Iterate to random index and check if attribute-value pair has been guessed 
        		if (i == randAttributeIndex && !guessedAttrValPairs.contains(attrValPair)) {
        			guess = new Guess(Guess.GuessType.Attribute, attr, value);
        			guessedAttrValPairs.add(attrValPair);
        		}
        		i++;
        	}

    	} while (guess == null);
    	
    	return guess;
    }


    public boolean answer(Guess currGuess)
    {
    	if (currGuess.getType() == Guess.GuessType.Person)
            // Check if guessing for person
    		return currGuess.getValue().equals(chosenPerson.name);
    	else
    		// Guessing for attribute
    		return chosenPerson.hasAttribute(currGuess.getAttribute(), currGuess.getValue());
    }


    public boolean receiveAnswer(Guess currGuess, boolean answer)
    {
        if (currGuess.getType() == Guess.GuessType.Person && answer)
            return true;

        String attrVal = currGuess.getAttribute() + " " + currGuess.getValue();
        ArrayList<Person> matchingPersons = attrValToPersonsMap.get(attrVal);

        if (answer) {
        	// Remove all persons that don't have attribute-value
        	opponentPersons.retainAll(matchingPersons);
        }
        else {
        	// Remove all persons that have attribute-value
        	opponentPersons.removeAll(matchingPersons);
        }

        return false;
    }

}
