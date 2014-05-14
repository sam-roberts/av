package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;

/**
 * Created by Sam on 14/05/2014.
 */
public class RotateVoiceInput extends ProcessingAnimation{

    float rotateDegrees = 0;
    final int speed = 2;
    public RotateVoiceInput(PApplet p, PublicInformation info) {
        super(p,info);
    }

    @Override
    protected void drawAnimation() {
        p.strokeWeight(5);
        p.stroke(Color.GREEN.getRGB());
        rotateDegrees = (rotateDegrees+speed) % 360;
        p.pushMatrix();
        p.translate(p.getWidth() / 2, p.getHeight() / 2);
        p.rotate(p.radians(rotateDegrees));

        p.line(-p.getWidth(), 0, p.getWidth(), 0);
        p.text("YOOOOOOOOOOOO",0,0);

        p.popMatrix();
    }

    @Override
    protected void resetTimedAnimation() {

    }

}
