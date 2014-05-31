package animations;

import java.awt.*;
import java.util.Random;

/**
 * Created by Sam on 14/05/2014.
 */
public class ColourManager {
    public ColourManager() {

    }

    Color getRandomColor() {
        Random r = new Random();

        float offset = r.nextFloat();
        int seed = r.nextInt();
        float hue = (offset + (0.618033988749895f * seed) % 1);
        Color c = Color.getHSBColor(hue, 0.9f, 0.95f);
        return c;

    }

    Color getRandomColor(int seed) {
        //float hue = (seed + (0.618033988749895f * seed) % 1);

        int numDigits = (int) (Math.log10(seed));

        float factorTenDivide = (int) Math.pow(10, (numDigits+1));

        float hue = 1 + (seed / factorTenDivide);
        hue = (float) Math.pow(hue,5);
        Color c = Color.getHSBColor(hue, 0.9f, 0.95f);
        return c;

    }
}
