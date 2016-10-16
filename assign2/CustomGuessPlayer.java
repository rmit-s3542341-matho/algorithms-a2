import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Your customised guessing player.
 * This player is for bonus task.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class CustomGuessPlayer extends Game
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
    public CustomGuessPlayer(String gameFilename, String chosenName)
        throws IOException
    {
    	attrValToPersonsMap = new HashMap<>();
        
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

        /*
         * Idea:
         *
         * At some point, the closest is something like 1/2 or 1/3
         * This means that it will ask for an attribute that can match only one, or two Persons
         * Best case scenario here: The guess is correct, meaning one Person is left, and then we
         * guess for that Person next round.
         *
         * I think at this point it should just guess the Person. If it is wrong, it can still remove the Person
         * i.e. 1/3 => 2, then try the same strategy next round (which should work).
         *
         * If it is right, it saves narrowing it down to one Person, effectively removing an entire round.
         *
         * To incorporate this we have to check for a Person guess in receiveAnswer
         *
         */

        if (opponentPersons.size() <= 3) {
            // guess a Person now
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
                for (Person person : opponentPersons) {
                    if (person.name.equals(currGuess.getValue())) {
                        opponentPersons.remove(person);
                        person.removeFromMap(attrValToPersonsMap);
                        return false;
                    }
                }
                System.out.println("Player not found");
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
