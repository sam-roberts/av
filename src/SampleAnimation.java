import processing.core.PApplet;
import processing.core.PShape;

import java.awt.*;
import java.util.Random;

/**
 * Created by Sam on 5/04/2014.
 */
public class SampleAnimation {
    PApplet p;
    public SampleAnimation(PApplet p) {

        this.p = p;
    }

    public void draw () {
        System.out.println("Drawing a nice hexagon");
        Random rand = new Random();

        final float hue = rand.nextFloat();
        // Saturation between 0.1 and 0.3
        final float saturation = (rand.nextInt(2000) + 1000) / 10000f;
        final float luminance = 0.9f;
        final Color color = Color.getHSBColor(hue, saturation, luminance);

        p.fill(color.getRGB());
        p.noStroke();
        p.smooth();

        final int size = (rand.nextInt(300) + 200);
        final int randomRotation = 0;
        final int numSides = rand.nextInt(9) + 3;
        PShape s = createPolygon(numSides,p.width/2, p.height/2, size,randomRotation);
        p.shape(s,0,0);

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
