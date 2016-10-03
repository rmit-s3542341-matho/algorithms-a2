import java.util.*;

public class Person {

    HashMap<String, String> attributes;

    public static Person[] loadPersons(String filePath) {
        return null;
    }

    public Person(String[] lines)  {
        attributes = new HashMap<>();

        for (String line: lines) {
            String[] words = line.split(" ");

            attributes.put(words[0], words[1])
        }
    }

    public String attr(String attrName) {
        return attributes.get(attrName);
    }

}