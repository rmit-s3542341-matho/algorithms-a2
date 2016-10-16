import java.io.*;
import java.util.ArrayList;

/**
 * Your customised guessing player.
 * This player is for bonus task.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class CustomGuessPlayer extends Game
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
    public CustomGuessPlayer(String gameFilename, String chosenName)
        throws IOException
    {
    	super(gameFilename, chosenName);
    }


    public Guess guess()
    {
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

        if (opponentPersons.size() <= 3) {
            // guess a Person now (this is the smart part)
            // best case: correct and finish (save 2 whole rounds)
            // worst case: (size == 3) only one Person is removed (same result as if guessing attr)
            // worse case: (size == 2) one Person is removed, guess Person next round (save 1 round)

            // assuming attrValueToGuess is always SHORT of half (i.e. 1/3 instead of 2/3)
            Person personToGuess = attrValToPersonsMap.get(attrValueToGuess).get(0);

            return new Guess(Guess.GuessType.Person, "", personToGuess.name);
        }

        // Split attribute-value string
        String[] attrValue = attrValueToGuess.split(" ");
        return new Guess(Guess.GuessType.Attribute, attrValue[0], attrValue[1]);
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
        if (currGuess.getType() == Guess.GuessType.Person) {
            if (answer) return true;
            else {
                // we need to handle when the Person guessed wasn't
                // correct (unlike other implementations where the guessed
                // Person is always the only one left and therefore always
                // correct). This implementation attempts to guess Persons
                // before it is certain which is correct.

                // go through the list of remaining Persons, and remove the
                // person that we guessed - as well as removing them from
                // all attributes they were present in
                for (Person person : opponentPersons) {
                    if (person.name.equals(currGuess.getValue())) {
                        opponentPersons.remove(person);
                        person.removeFromMap(attrValToPersonsMap);
                        return false;
                    }
                }
                System.out.println("Person not found");
            }
        }

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
