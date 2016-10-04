import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    private ArrayList<String> guessedAttributes;

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
        guessedAttributes = new ArrayList<String>();

        // I'm assuming the chosen person cannot be the
        // same for both players
        opponentPersons.remove(chosenPerson);
    } // end of RandomGuessPlayer()


    public Guess guess()
    {
        if (opponentPersons.size() == 1) {
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }
        
    	// Randomly select a person from opponentPersons. Randomly select an attribute 
    	// of that person. Removing of all persons who do/don't have the attribute is
    	// handled in recieveAnswer. Store the attribute that has been guessed.
    	
    	Random rand = new Random();
    	Guess guess = null;
    	
    	// Grab a random person
    	int randPersonIndex = rand.nextInt(opponentPersons.size());
    	Person guessPerson = opponentPersons.get(randPersonIndex);
    	
    	// Could be an infinite loop however it shouldn't
    	do {
        	// Grab a random attribute from the person
        	int i = 0, randAttributeIndex = rand.nextInt(guessPerson.attributes.size());
        	
        	for (String attribute : guessPerson.attributes.keySet()) {
        		
        		// Iterate to random index and check if attribute has been guessed 
        		if (i == randAttributeIndex && !guessedAttributes.contains(attribute)) {
        			guess = new Guess(Guess.GuessType.Attribute, attribute, guessPerson.attributes.get(attribute));
        			guessedAttributes.add(attribute);
        		}
        		i++;
        	}
    	} while(guess == null);
    	
    	return guess;
    } // end of guess()


    public boolean answer(Guess currGuess) {
    	// Check if guessing for person
    	if (currGuess.getType() == Guess.GuessType.Person) {
    		return currGuess.getValue().equals(chosenPerson.name);
    	}
    	else {
    		// Guessing for attribute
    		return chosenPerson.attributes.get(currGuess.getAttribute()) != null ? true : false;
    	}
    } // end of answer()


	public boolean receiveAnswer(Guess currGuess, boolean answer)
    {
		// Check if guess was of opponents person and was correct
		if (currGuess.getType() == Guess.GuessType.Person && answer) {
	        return true;
		}
		
	    String key = currGuess.getAttribute() + " " + currGuess.getValue();

        ArrayList<Person> matchingPersons = attrValToPersonsMap.get(key);

        if (answer) {
            // remove everybody that doesn't match
            for (Person person : opponentPersons) {
                // not sure of the speed of this \/
                if (matchingPersons.contains(person))
                    // UNTESTED
                    // will probably throw concurrent modification error
                    opponentPersons.remove(person);
            }
        }
        else {
            // remove everybody that matches

            for (Person person : matchingPersons)
                opponentPersons.remove(person);

        }

        return false;
    } // end of receiveAnswer()

} // end of class RandomGuessPlayer
