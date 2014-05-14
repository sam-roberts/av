package helpers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sam on 5/04/2014.
 */
public class KeyTriggerManager {

    HashMap<Character,Sample> map;
    char[] keyboardKeys = {'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m'};
    public KeyTriggerManager(SampleManager s) {
        this.map = new HashMap<Character, Sample>();
        setup(s);

    }
    private void setup(SampleManager s) {
        int numSamples = s.getNumSamples();
        ArrayList<Sample> allSamples = s.getAllSamples();

        for (int i = 0; i < keyboardKeys.length && i < numSamples; i++) {
            map.put(keyboardKeys[i],allSamples.get(i));
        }
    }

    public Sample getSampleOfKey (char key){
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            System.out.println("No sample is mapped to the letter " + key);
            return null;
        }
    }
}
