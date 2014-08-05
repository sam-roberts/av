package animations;

import helpers.PublicInformation;
import helpers.Sample;
import processing.core.PApplet;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sam on 31/05/2014.
 */
public class MovableBox extends ProcessingAnimation {

    int xLocation;
    int yLocation;
    int width;

    boolean playsound = true;

    Sample sound = null;

    boolean hit = false;
    private int hitByRotor = 0;
    private boolean hover;

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
    Color initialFill;
    boolean movable;

    Rotater hitBy = null;
    public MovableBox(PApplet p, PublicInformation info, int xLocation, int yLocation, int width, int height) {
        super(p, info);
        this.xLocation = xLocation;
        this.yLocation = yLocation;
        initialWidth = width;
        initialHeight = height;
        this.width = initialWidth;
        this.height = initialHeight;
        this.fill = this.colourManager.getRandomColor();
        initialFill = this.fill;
        this.movable = true;


    }

    @Override
    protected void drawAnimation() {

        if (isHit()) {
            setFill(Color.red);
            pressed();
            if (playsound) {
                getSound().setLoop(false);
                (new Thread(getSound())).start();
            }
            playsound = false;


        } else {
            release();
            //setFill(initialFill);
            playsound = true;
        }

        if (getSound().getGain() < 0) {
            //System.out.println("this shit has gain" + getSound().getGain());
            //System.out.println("width was " + this.width);

            this.opacity = (int) p.map(getSound().getGain(), -12,0,50,255);
            this.width = (int) p.map(this.opacity, 50,255, this.width/2, this.width*2);
            this.height = (int) p.map(this.opacity, 50,255, this.height/2, this.height*2);
        } else {
            this.opacity = 255;

        }

        p.fill(getFill().getRGB(), this.opacity);
        p.noStroke();
        p.pushMatrix();
        p.translate(xLocation, yLocation);

        p.ellipse(0, 0, this.width, this.height);
        p.popMatrix();

        if (sound.getSimpleFilename() != null) {
            p.text(sound.getSimpleFilename(), xLocation,yLocation-20);
        }


    }

    @Override
    protected void resetTimedAnimation() {

    }

    public void pressed() {
        this.width = (int) (initialWidth*0.75);
        this.height = (int) (initialWidth*0.75);
        //setFill(colourManager.getRandomColor());
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

    public Sample getSound() {
        return sound;

    }

    public void setSound(Sample sound) {
        this.sound = sound;
        this.initialFill = colourManager.getRandomColor(sound.hashCode());
        //System.out.println("sound hashes" + sound.hashCode());


    }


    public void setHit(boolean hit, Rotater r) {
        this.hit = hit;
        this.hitBy = r;

    }

    public Rotater getHitBy() {
        return hitBy;
    }

    public void setHitBy(Rotater hitBy) {
        this.hitBy = hitBy;
    }

    public boolean isHit() {
        return hit;
    }

    public boolean isMovable() {
        return movable;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public void setHover(boolean hover) {
        this.hover = hover;

        if (isHover()) {
            setFill(this.initialFill.brighter().brighter().brighter());
        } else {
            setFill(this.initialFill);
        }
        this.hover = hover;
    }

    public boolean isHover() {
        return hover;
    }
}
