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


    private static final int DIVIDE_FACTOR = 8;

    public RotatePlayers(PApplet p, PublicInformation info) {
        rotators = new ArrayList<Rotater>();
        Rotater one = new Rotater(p, info, Duration.HALF);
        one.setLength(p.getWidth()/DIVIDE_FACTOR);
        one.setOrigin(p.getWidth()/2 - (one.getLength() * 1.40f), p.getHeight()/2+one.getLength()/2.5f);

        Rotater quarter = new Rotater(p, info, Duration.QUARTER);
        quarter.setLength(p.getWidth()/DIVIDE_FACTOR);
        quarter.setOrigin(p.getWidth()/2 + (quarter.getLength() * 1.40f), p.getHeight()/2+quarter.getLength()/2.5f);


        Rotater two = new Rotater(p, info, Duration.EIGHTH);
        two.setLength(p.getWidth()/13);
        two.setOrigin(p.getWidth()/2- (two.getLength() * 1.40f), two.getLength() * 1.6f);

        Rotater sixteenth = new Rotater(p, info, Duration.SIXTEENTH);
        sixteenth.setLength(p.getWidth()/13);
        sixteenth.setOrigin(p.getWidth()/2+ (sixteenth.getLength() * 1.40f),sixteenth.getLength() * 1.6f);

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
