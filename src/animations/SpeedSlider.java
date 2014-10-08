package animations;

import helpers.Duration;
import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;

/**
 * Created by Sam on 6/10/2014.
 */
public class SpeedSlider extends ProcessingAnimation {

    int numSettings = 5;
    int position = 0;

    float xLocation;
    float yLocation;
    int segment = 50;
    int totalWidth = (numSettings-1) * segment;

    MovableBox slider;
    boolean hasClicked;




    public SpeedSlider(PApplet p, PublicInformation info, float xLocation, float yLocation, double position){
        super(p,info);
        this.xLocation = xLocation;
        this.yLocation = yLocation;

        if (position == Duration.SIXTEENTH) {
            this.position = 4;
        } else if (position == Duration.EIGHTH) {
            this.position = 3;
        } else if (position == Duration.QUARTER)  {
            this.position = 2;
        } else if (position == Duration.HALF)  {
            this.position = 1;
        } else if (position == Duration.WHOLE)  {
            this.position = 0;
        }
        hasClicked = false;
        slider = new SliderBox(p, info, (float)(xLocation + (position * segment)), yLocation, 20, 20);

    }
    @Override
    protected void drawAnimation() {
        p.stroke(230);
        p.pushMatrix();
        p.translate(-getOffset(),0,-1);
        p.line(this.xLocation, this.yLocation, this.xLocation + (totalWidth), this.yLocation);
        p.stroke(200);
        p.fill(120);
        p.textSize(16);
        for (int i = 0 ; i < numSettings; i++) {
            float localX = this.xLocation + (i * segment);
            p.line(localX, yLocation - 15, localX, yLocation + 15);
            p.text(getText(i), localX-15, yLocation + 30);
        }

        //draw the correct position
        /*
        p.fill(Color.RED.getRGB());
        p.noStroke();
        p.translate(0,0,1);
        p.ellipse(this.xLocation + (position * segment), this.yLocation, 20,20);
        */
        //slider.draw();

        p.popMatrix();

    }

    public void draw(float x, float y) {
        this.xLocation = x;
        this.yLocation = y;
        //slider.setxLocation(xLocation + (position * segment));
        //slider.setyLocation(yLocation);

        draw();
    }
    public String getText(int n) {
        switch (n) {
            case 0: return "1/1";
            case 1: return "1/2";
            case 2: return "1/4";
            case 3: return "1/8";
            case 4: return "1/16";
        }
        return "??";
    }
    @Override
    protected void resetTimedAnimation() {

    }

    public MovableBox getSlider() {
        return slider;
    }

    public float getyLocation() {
        return yLocation;
    }

    public void setyLocation(float yLocation) {
        this.yLocation = yLocation;
        slider.setyLocation(yLocation);

    }

    public float getxLocation() {
        return xLocation;
    }

    public int getOffset() {
        return totalWidth/2;
    }

    public void setxLocation(float xLocation) {
        this.xLocation = xLocation;
        slider.setxLocation(xLocation - getOffset() + (position*segment));

    }

    public float getClosestXPosition(float x) {
        //input
        float startPoint = getxLocation() - getOffset();
        float distance = x - startPoint;
        float endPoint = startPoint + (totalWidth);

        float returnValue = 0;

        int newPosition = position;
        if (distance < 0) {
            newPosition = 0;
            returnValue = startPoint;
        } else if (distance > totalWidth) {
            newPosition = 4;
            returnValue = endPoint;
        } else {
            float n = roundToNearest(distance, segment);
            newPosition = (int) (n/segment);
            if (newPosition > numSettings - 1) {
                newPosition = 0;
            }
            newPosition = Math.max(0, newPosition);
            returnValue = startPoint + n;
        }
        if (newPosition == position) {
            hasClicked = false;
        } else {
            //they are different
            position = newPosition;
            hasClicked = true;

        }

        return returnValue;

    }

    private float roundToNearest(float numToRound, int multiple) {

            return multiple * (Math.round(numToRound/multiple));
    }

    public double getSpeed() {

        if (position == 4) {
            return Duration.SIXTEENTH;
        } else if (position == 3) {
            return Duration.EIGHTH;
        } else if (position == 2) {
            return Duration.QUARTER;
        } else if (position == 1) {
            return Duration.HALF;
        } else if (position == 0) {
            return Duration.WHOLE;
        } else {
            System.out.println("VERY BAD");
            return Duration.WHOLE;
        }
    }

    public boolean isHasClicked() {
        return hasClicked;
    }
}
