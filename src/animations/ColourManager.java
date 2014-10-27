package animations;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Sam on 14/05/2014.
 */
public class ColourManager {

    final public static Color RED_ONE = new Color(237,99,108);
    final public static Color BLUE_ONE = new Color(38,216,248);
    final public static Color YELLOW_ONE = new Color(251, 241, 110);
    final public static Color PURPLE_ONE = new Color(231,91,248);
    final public static Color GREEN_ONE = new Color(27,231,137);
    final public static Color ORANGE_ONE = new Color(245, 183, 54);


    final public static Color MENU_GREEN = new Color(144,189,128);
    final public static Color MENU_RED = new Color(203,114,116);
    final public static Color MENU_BLUE = new Color(128,163,189);



    ArrayList<Color> colours;
    int numUsed = 0;


    private HashMap<String,Integer> seedMap;
    public ColourManager() {
        colours = new ArrayList<Color>();
        colours.add(RED_ONE);
        colours.add(BLUE_ONE);
        colours.add(PURPLE_ONE);
        colours.add(GREEN_ONE);
        colours.add(ORANGE_ONE);
        colours.add(YELLOW_ONE);

        seedMap = new HashMap<String, Integer>();

    }

    Color getRandomColor() {
        Random r = new Random();

        float offset = r.nextFloat();
        int seed = r.nextInt();
        float hue = (offset + (0.618033988749895f * seed) % 1);
        Color c = Color.getHSBColor(hue, 0.9f, 0.95f);
        return c;

    }


    public HashMap<String, Integer> getSeedMap() {
        return seedMap;
    }

    public Color getRandomColor(String seed) {
        //float hue = (seed + (0.618033988749895f * seed) % 1);
        if (seedMap.containsKey(seed)) {
            return colours.get(seedMap.get(seed));
        } else {
            if (numUsed + 1 > colours.size()) {
                numUsed = 0;
            }
            seedMap.put(seed, numUsed);
            numUsed++;

            return colours.get(seedMap.get(seed));
        }

        /*
        int numDigits = (int) (Math.log10(seed));

        float factorTenDivide = (int) Math.pow(10, (numDigits+1));

        float hue = 1 + (seed / factorTenDivide);
        hue = (float) Math.pow(hue,5);
        Color c = Color.getHSBColor(hue, 0.9f, 0.95f);
        return c;
        */

    }
}
