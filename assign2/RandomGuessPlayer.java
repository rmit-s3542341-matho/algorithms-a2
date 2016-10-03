import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Random guessing player.
 * This player is for task B.
 *
 * You may implement/extend other interfaces or classes, but ensure ultimately
 * that this class implements the Player interface (directly or indirectly).
 */
public class RandomGuessPlayer extends Game implements Player
{
	Person chosenPerson;

    // list of possible Persons that may be chosen for the other player
    ArrayList<Person> opponentPersons;

    // map of string tuple to list of Persons that contain that attribute
    // i.e. "hairColor black" ~> P1, P2, P3
    HashMap<String, ArrayList<Person>> attrMap;
	
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
        attrMap = new HashMap<>();
    	readGameConfig(gameFilename, attrMap);
    	
    	chosenPerson = getPerson(chosenName);

        // copy the list of all persons so that this
        // player can change it
        opponentPersons = new ArrayList<>(allPersons);

        // I'm assuming the chosen person cannot be the
        // same for both players
        opponentPersons.remove(chosenPerson);
    } // end of RandomGuessPlayer()


    public Guess guess()
    {
        if (opponentPersons.size() == 1) {
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }
        else {
            // TODO:
            // check Persons for an attribute that has not been used (or keep a list)
            // choose one at random
        }

        return new Guess(Guess.GuessType.Person, "", "Placeholder");
    } // end of guess()


    public boolean answer(Guess currGuess) {

        // placeholder, replace
        return false;
    } // end of answer()


	public boolean receiveAnswer(Guess currGuess, boolean answer)
    {
	    String key = currGuess.getAttribute() + " " + currGuess.getValue();

        ArrayList<Person> matchingPersons = attrMap.get(key);

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

        return true;
    } // end of receiveAnswer()

} // end of class RandomGuessPlayer
