package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;

/**
 * Created by Sam on 8/10/2014.
 */
public class SliderBox extends MovableBox {

    public SliderBox(PApplet p, PublicInformation info, float xLocation, float yLocation, float width, float height) {
        super(p, info, xLocation, yLocation, width, height);
        setLockXMovement(true);
        setInitialFill(Color.GRAY);
        setFill(Color.GRAY);

    }
}
