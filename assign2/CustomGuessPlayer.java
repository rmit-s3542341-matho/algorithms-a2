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
public class CustomGuessPlayer extends Game implements Player
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
    public CustomGuessPlayer(String gameFilename, String chosenName)
        throws IOException
    {
        attrMap = new HashMap<>();
        readGameConfig(gameFilename, attrMap);

        chosenPerson = getPerson(chosenName);

        // copy the list of all persons so that this
        // player can change it
        opponentPersons = new ArrayList<>(allPersons);

    }


    public Guess guess()
    {
        if (opponentPersons.size() == 1) {
            // only one person left, so it must be the correct one
            return new Guess(Guess.GuessType.Person, "", opponentPersons.get(0).name);
        }

        /**
         * Extra strategies courtesy of https://boardgamegeek.com/thread/302791/advanced-strategies
         *
         * Person isn't going to be yours - do not immediately guess any attributes that this Player's
         * chosenPerson has (these attributes are less likely assuming even distribution)
         *
         * Have a last chance guess when the opponent only has one person left
         * Perhaps this means this Player should keep track of what the other Player has (can update inside answer())
         *
         * Cannot ask generic questions due to the implementation of guessing ("does your person have a beard?", etc)
         *
         */

        return new Guess(Guess.GuessType.Person, "", "Placeholder");
    }


    public boolean answer(Guess currGuess)
    {
        if (currGuess.getType() == Guess.GuessType.Person)
            return chosenPerson.name.equals(currGuess.getValue());
        else
            return chosenPerson.hasAttribute(currGuess.getAttribute(), currGuess.getValue());
    }


	public boolean receiveAnswer(Guess currGuess, boolean answer)
    {
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
        for (ArrayList<Person> persons : attrMap.values()) {
            // Prevents trying to delete from nulls (wouldn't throw an error anyway)
            if (!persons.isEmpty()) persons.removeAll(personsToRemove);
        }

        // remove the attr from the pool of guessable ones since
        // it has been used
        attrMap.remove(key);

        return false;
    }

}
