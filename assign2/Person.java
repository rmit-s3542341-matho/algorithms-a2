import java.util.*;
	
public class Person {
	public String name;
	public HashMap<String, String> attributes;
	
	// Constructor
	public Person(String name) {
		this.name = name;
		attributes = new HashMap<String, String>(); 
	}

    /**
     * Remove this Person from all the attributes it's present in
     * in the given attribute map
     *
     * May not be needed, now that I wrote this I don't see any real
     * need to remove them from the map after they're gone
     *
     * @param attrMap
     */
	public void removeFromMap(HashMap<String, ArrayList<Person>> attrMap)
    {
        for (Map.Entry entry : attributes.entrySet())
        {
            String key = entry.getKey() + " " + entry.getValue();
            if (attrMap.containsKey(key)) attrMap.get(key).remove(this);
        }
    }

    public boolean hasAttribute(String attrName, String attrValue)
    {
        return attributes.get(attrName).equals(attrValue);
    }
}
