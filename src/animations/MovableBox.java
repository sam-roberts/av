package animations;

import helpers.PublicInformation;
import helpers.Sample;
import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
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
    private double angleToRotor;

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

    ArrayList<FadeAnimation> fadeAnimations;
    ArrayList<FadeAnimation> toDeleteList;

    ProcessingAnimation extraAnimation;


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
        fadeAnimations = new ArrayList<FadeAnimation>();
        toDeleteList =new ArrayList<FadeAnimation>();
        angleToRotor = 0;


    }

    @Override
    protected void drawAnimation() {
        Iterator<FadeAnimation> it = fadeAnimations.iterator();
        while (it.hasNext()) {
            FadeAnimation f = it.next();
            if (f.isStartAnimation()) {
                f.drawAnimation();
            } else {
                toDeleteList.add(f);
            }
        }
        for (FadeAnimation f: toDeleteList) {
            fadeAnimations.remove(f);
        }
        toDeleteList.clear();


        if (isHit()) {
            //setFill(Color.red);
            pressed();
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

        p.fill(getFill().getRGB());
        p.stroke(getFill().darker().getRGB());
        p.strokeWeight(2);
        p.pushMatrix();
        p.translate(xLocation, yLocation, 1);

        p.ellipse(0, 0, this.width, this.height);
        p.popMatrix();

        if (sound.getSimpleFilename() != null) {
            p.fill(0);
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
        if (! isHover()) {
            this.fill = initialFill;
        }
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
        if (sound.getCategory() != null) {
            this.initialFill = colourManager.getRandomColor(sound.getCategory().hashCode());
            System.out.println(sound.getCategory() + sound.getCategory().hashCode());
        }

        extraAnimation = new fftAnimation(p, info, getSound());

        //System.out.println("sound hashes" + sound.hashCode());


    }


    public void setHit(boolean hit, Rotater r) {
        this.hit = hit;
        this.hitBy = r;

    }

    public void playsound() {
            getSound().setLoop(false);

            if (! getSound().isRunning()) {
                FadeAnimation fadeAnimation = new FadeAnimation(this.p, this.info, this.getxLocation(), this.getyLocation(), getWidth());
                fadeAnimation.setFill(this.getFill());
                fadeAnimations.add(fadeAnimation);
                new Thread(getSound()).start();



            }

        //playsound = false;

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
            setFill(this.initialFill.brighter().brighter());
        } else {
            setFill(this.initialFill);
        }
        this.hover = hover;
    }

    public boolean isHover() {
        return hover;
    }

    public ProcessingAnimation getExtraAnimation() {
        return extraAnimation;
    }

    public void setExtraAnimation(ProcessingAnimation extraAnimation) {
        this.extraAnimation = extraAnimation;
    }

    public void setAngleToRotor(double angleToRotor) {
        this.angleToRotor = angleToRotor;
    }

    public double getAngleToRotor() {
        return angleToRotor;
    }
}
