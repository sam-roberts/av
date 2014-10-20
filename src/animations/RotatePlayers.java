package animations;

import helpers.Duration;
import helpers.PublicInformation;
import processing.core.PApplet;

import java.util.ArrayList;

/**
 * Created by Sam on 31/05/2014.
 */
public class RotatePlayers {

    ArrayList<Rotater> rotators;

    public static int BOTTOM_LEFT = 0;
    public static int BOTTOM_RIGHT = 1;
    public static int TOP_LEFT = 2;
    public static int TOP_RIGHT = 3;

    public RotatePlayers(PApplet p, PublicInformation info) {
        rotators = new ArrayList<Rotater>();
        Rotater one = new Rotater(p, info, Duration.HALF);
        one.setLength(280);
        one.setOrigin(p.getWidth()/2 -450, p.getHeight()/2+150);

        Rotater quarter = new Rotater(p, info, Duration.QUARTER);
        quarter.setLength(280);
        quarter.setOrigin(p.getWidth()/2 + 550, p.getHeight()/2+150);


        Rotater two = new Rotater(p, info, Duration.EIGHTH);
        two.setLength(150);
        two.setOrigin(p.getWidth()/2-200, p.getHeight()/2-300);

        Rotater sixteenth = new Rotater(p, info, Duration.SIXTEENTH);
        sixteenth.setLength(150);
        sixteenth.setOrigin(p.getWidth()/2+200, p.getHeight()/2-300);

        rotators.add(one);
        rotators.add(quarter);
        rotators.add(two);
        rotators.add(sixteenth);


    }

    public void play() {
        for (Rotater r: rotators) {
            r.draw();
        }
    }

    public ArrayList<Rotater> getRotators() {
        return rotators;
    }


}
