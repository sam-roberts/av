package animations;

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
        Rotater one = new Rotater(p, info);
        one.setLength(270);
        one.setOrigin(p.getWidth()/2, p.getHeight()/2);
        rotators.add(one);

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
