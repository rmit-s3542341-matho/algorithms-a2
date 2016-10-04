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
public class BinaryGuessPlayer extends Game implements Player
{
	private Person chosenPerson;

    // list of possible Persons that may be chosen for the other player
    private ArrayList<Person> opponentPersons;

    // map of string tuple to list of Persons that contain that attribute
    // i.e. "hairColor black" ~> P1, P2, P3
    private HashMap<String, ArrayList<Person>> attrMap;
	
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
    	attrMap = new HashMap<>();
    	readGameConfig(gameFilename, attrMap);
    	
    	chosenPerson = getPerson(chosenName);

        // copy the list of all persons so that this
        // player can change it
        opponentPersons = new ArrayList<>(allPersons);

        // I'm assuming the chosen person cannot be the
        // same for both players
        opponentPersons.remove(chosenPerson);
    } // end of BinaryGuessPlayer()


    public Guess guess() {
    	if (opponentPersons.size() == 1) {
            // only one person left, so it must be the correct one
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }

        double half = (double) opponentPersons.size() / 2;
        double distance = 0;
        double bestDistance = opponentPersons.size();
        
        String attrToGuess = ""; // never used in this state
        for (String attrVal : attrMap.keySet()) {        	
        	distance = distanceBetween(half, attrMap.get(attrVal).size());
        	if (distance == 0) {
        		// Exactly half
        		attrToGuess = attrVal;
        		break;
        	}
        	else if (distance < bestDistance) {
        		bestDistance = distance;
        		attrToGuess = attrVal;
        	}
        }
        
        // attr key not removed here, as it is needed in receiveAnswer
    	
        // split and return guess
        String[] attr = attrToGuess.split(" ");
        return new Guess(Guess.GuessType.Attribute, attr[0], attr[1]);
    } // end of guess()

    public static double distanceBetween(double base, double toCheck) {
    	double answer;    	
    	if (base > toCheck) 
    		answer = base - toCheck;
    	else 
    		answer = toCheck - base;
    	return answer;
    }
    
    
	public boolean answer(Guess currGuess) {
		if (currGuess.getType() == Guess.GuessType.Person)
            return chosenPerson.name.equals(currGuess.getValue());
        else
            return chosenPerson.hasAttribute(currGuess.getAttribute(), currGuess.getValue());
    } // end of answer()


	public boolean receiveAnswer(Guess currGuess, boolean answer) {
		if (currGuess.getType() == Guess.GuessType.Person && answer)
            return true;

	    String key = currGuess.getAttribute() + " " + currGuess.getValue();

        ArrayList<Person> matchingPersons = attrMap.get(key);
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
        attrMap.remove(key);

        return false;
    } // end of receiveAnswer()

} // end of class BinaryGuessPlayer
