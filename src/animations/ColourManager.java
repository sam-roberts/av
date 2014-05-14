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
}
