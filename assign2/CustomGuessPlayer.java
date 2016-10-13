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

        /*
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

        double half = (double) opponentPersons.size() / 2;
        double distance = 0;
        double bestDistance = opponentPersons.size();

        String attrToGuess = ""; // never used in this state, this could cause an issue
        for (String attrVal : attrMap.keySet()) {
            distance = distanceBetween(half, attrMap.get(attrVal).size());
            if (distance == 0) {
                // Exactly half
                bestDistance = distance;
                attrToGuess = attrVal;
                break;
            }
            else if (distance < bestDistance) {
                bestDistance = distance;
                attrToGuess = attrVal;
            }
        }

        // attr key not removed here, as it is needed in receiveAnswer

        System.out.println("CUSTOM: Closest is " + (int) (half - bestDistance) + "/" + opponentPersons.size());

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

            // assuming attrToGuess is always SHORT of half (i.e. 1/3 instead of 2/3)
            Person personToGuess = attrMap.get(attrToGuess).get(0);

            return new Guess(Guess.GuessType.Person, "", personToGuess.name);
        }

        // split and return guess
        String[] attr = attrToGuess.split(" ");
        return new Guess(Guess.GuessType.Attribute, attr[0], attr[1]);
    }

    public static double distanceBetween(double base, double toCheck) {
        double answer;
        if (base > toCheck)
            answer = base - toCheck;
        else
            answer = toCheck - base;
        return answer;
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
        if (currGuess.getType() == Guess.GuessType.Person) {
            if (answer) return true;
            else {
                for (Person person : opponentPersons) {
                    if (person.name.equals(currGuess.getValue())) {
                        opponentPersons.remove(person);
                        person.removeFromMap(attrMap);
                        return false;
                    }
                }
                System.out.println("Player not found");
            }
        }

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

        // remove all from potential persons
        opponentPersons.removeAll(personsToRemove);

        System.out.println("CUSTOM: Eliminated " + personsToRemove.size());

        // remove the attr from the pool of guessable ones since
        // it has been used
        attrMap.remove(key);

        for (String currKey : attrMap.keySet()) {
            ArrayList<Person> persons = attrMap.get(currKey);
            // Prevents trying to delete from nulls (wouldn't throw an error anyway)
            if (!persons.isEmpty()) persons.removeAll(personsToRemove);
        }

        return false;
    }

}
