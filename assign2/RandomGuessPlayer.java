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
public class RandomGuessPlayer extends Game implements Player
{
	private Person chosenPerson;

    // list of possible Persons that may be chosen for the other player
    private ArrayList<Person> opponentPersons;
    private ArrayList<String> guessedAttrValPairs;

    // map of string tuple to list of Persons that contain that attribute
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
    	readGameConfig(gameFilename, attrValToPersonsMap);
    	
    	chosenPerson = getPerson(chosenName);

        // copy the list of all persons so that this
        // player can change it
        opponentPersons = new ArrayList<>(allPersons);
        guessedAttrValPairs = new ArrayList<String>();
    }


    public Guess guess()
    {
        if (opponentPersons.size() == 1) {
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }
        
    	/* Randomly select a person from opponentPersons. Randomly select an attribute-
    	 * value pair of that person. Removing of all persons who do/don't have the 
    	 * attribute-value pair is handled in recieveAnswer. Store the attribute-value
    	 * pair that has been guessed. */
    	
    	Random rand = new Random();
    	Guess guess = null;
    	
    	// Grab a random person
    	int randPersonIndex = rand.nextInt(opponentPersons.size());
    	Person guessPerson = opponentPersons.get(randPersonIndex);
    	
    	// Could be an infinite loop however it shouldn't
    	do {
        	// Grab a random attribute from the person
        	int randAttributeIndex = rand.nextInt(guessPerson.attributes.size());
        	int i = 0;
        	
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

        String key = currGuess.getAttribute() + " " + currGuess.getValue();

        ArrayList<Person> matchingPersons = attrValToPersonsMap.get(key);
        ArrayList<Person> personsToRemove = new ArrayList<>();

        // if answer is true (guess was correct), then we should remove
        // all Persons that don't have the attribute
        // otherwise remove all Persons that do have the attribute
        if (answer) {
            // remove everybody that doesn't match
            for (Person person : opponentPersons) {
                // not sure of the speed of this \/
                if (!matchingPersons.contains(person))
                    personsToRemove.add(person);
            }
        }
        else
            // remove everybody that does match
            personsToRemove = matchingPersons;

        for (Person person : personsToRemove)
            opponentPersons.remove(person);

        // remove the attr from the pool of guessable ones since
        // it has been used
        // attrValToPersonsMap.remove(key);

        return false;
    }

}
