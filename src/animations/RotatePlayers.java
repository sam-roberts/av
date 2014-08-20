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

    public RotatePlayers(PApplet p, PublicInformation info) {
        rotators = new ArrayList<Rotater>();
        Rotater one = new Rotater(p, info, Duration.HALF);
        one.setLength(300);
        one.setOrigin(p.getWidth()/2 -325, p.getHeight()/2+100);

        Rotater quarter = new Rotater(p, info, Duration.QUARTER);
        quarter.setLength(300);
        quarter.setOrigin(p.getWidth()/2 + 325, p.getHeight()/2+100);


        Rotater two = new Rotater(p, info, Duration.EIGHTH);
        two.setLength(100);
        two.setOrigin(p.getWidth()/2-125, p.getHeight()/2-300);

        Rotater sixteenth = new Rotater(p, info, Duration.SIXTEENTH);
        sixteenth.setLength(100);
        sixteenth.setOrigin(p.getWidth()/2+125, p.getHeight()/2-300);

        rotators.add(one);
        rotators.add(two);
        rotators.add(quarter);

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
