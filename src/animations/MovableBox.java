package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;

/**
 * Created by Sam on 31/05/2014.
 */
public class MovableBox extends ProcessingAnimation {

    int xLocation;
    int yLocation;
    int width;

    boolean hit = false;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    int height;
    int initialWidth;
    int initialHeight;
    public MovableBox(PApplet p, PublicInformation info, int xLocation, int yLocation, int width, int height) {
        super(p, info);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        initialWidth = width;
        initialHeight = height;
        this.width = initialWidth;
        this.height = initialHeight;
        this.fill = this.colourManager.getRandomColor();

    }

    @Override
    protected void drawAnimation() {
        if (isHit()) {
            setFill(Color.red);
        } else {
            setFill(Color.green);
        }
        p.fill(getFill().getRGB());
        p.noStroke();
        p.pushMatrix();
        p.translate(xLocation,yLocation);
        p.ellipse(0, 0, width, height);
        p.popMatrix();



    }

    @Override
    protected void resetTimedAnimation() {

    }

    public void pressed() {
        this.width = (int) (initialWidth*1.5);
        this.height = (int) (initialWidth*1.5);
        setFill(colourManager.getRandomColor());
    }

    public void release() {
        this.width = initialWidth;
        this.height = initialHeight;
    }
    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }



    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public boolean isHit() {
        return hit;
    }
}
