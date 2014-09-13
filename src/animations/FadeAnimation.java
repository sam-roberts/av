package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

/**
 * Created by Sam on 15/08/2014.
 */
public class FadeAnimation extends ProcessingAnimation {

    float xLocation;
    float yLocation;

    int animationSize;
    int animationOpacity;
    boolean startAnimation = false;
    int normalSize;

    public FadeAnimation(PApplet p, PublicInformation info, float xLocation, float yLocation, int size) {
        super(p, info);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        this.animationSize = size;
        this.normalSize = size;
        this.animationOpacity = 255;

        startAnimation = true;
    }

    @Override
    protected void drawAnimation() {
        if (startAnimation) {
            p.noStroke();
            p.pushMatrix();
            p.translate(xLocation, yLocation);


            p.noFill();
            //Color c = new Color(255 - getFill().getRed(), getFill().getGreen(), getFill().getBlue());

            p.stroke(getFill().getRGB(), animationOpacity);
            p.strokeWeight(2.0f);
            p.ellipse(0, 0, animationSize, animationSize);

            if (animationOpacity > 0) {
                animationSize += 8;
                animationOpacity -= 5;

            } else {
                startAnimation=false;
                animationSize= this.normalSize;
                animationOpacity=255;
            }
            p.popMatrix();
        }
    }

    public boolean isStartAnimation() {
        return startAnimation;
    }

    @Override
    protected void resetTimedAnimation() {

    }
}
