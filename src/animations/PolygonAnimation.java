package animations;

import helpers.PublicInformation;
import processing.core.PApplet;
import processing.core.PShape;

import java.util.Random;

/**
 * Created by Sam on 14/05/2014.
 */
public class PolygonAnimation extends ProcessingAnimation {

    int numSides;
    final int size;

    float rotateDegrees = 0;
    final float speed = 2f;

    public PolygonAnimation(PApplet p, PublicInformation info) {
        super(p,info);
        Random rand = new Random();
        numSides = rand.nextInt(9) + 3;
        size = 250;
        fill = colourManager.getRandomColor();
        scale = 0.0f;

    }
    @Override
    protected void drawAnimation() {
        rotateDegrees += speed % 360;

        Random rand = new Random();
        scale = (scale*(1+scale)) + 0.1f;

        if (scale > 1.0f) {
            scale = 1.0f;
        }
        p.fill(this.fill.getRGB());
        p.noStroke();



        p.pushMatrix();
        PShape s = createPolygon(numSides,0, 0, size,0);
        s.scale(scale);
        p.translate(p.getWidth()/2, p.getHeight()/2);
        //s.rotate(p.radians(rotateDegrees));
        p.shape(s,0,0);
        p.popMatrix();

    }

    @Override
    protected void resetTimedAnimation() {
        Random rand = new Random();
        numSides = rand.nextInt(9) + 3;
        fill = colourManager.getRandomColor();
        scale = 0.0f;
    }

    PShape createPolygon(int numSides, float xPosition, float yPosition, float radius, float offset) {
        PShape s = p.createShape();
        if (numSides > 2) {
            float angle = p.TWO_PI / (float)numSides;

  /* The horizontal "radius" is one half the width;
     the vertical "radius" is one half the height */
            float w = p.width / 2.0f;
            float h = p.height / 2.0f;




            s.beginShape();
            for (int i = 0; i < numSides; i++) {
                s.vertex( (float)(xPosition + (radius * Math.cos((offset + angle * i)))),
                        (float) (yPosition + (radius * Math.sin((offset + angle * i))))
                );
            }
            s.endShape(p.CLOSE);
        }
        return s;
    }
}
