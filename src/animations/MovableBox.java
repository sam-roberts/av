package animations;

import helpers.PublicInformation;
import helpers.Sample;
import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sam on 31/05/2014.
 */
public class MovableBox extends ProcessingAnimation {

    float xLocation;
    float yLocation;
    int width;

    boolean playsound = true;

    Sample sound = null;

    boolean hit = false;
    private int hitByRotor = 0;
    private boolean hover;
    private double angleToRotor;
    private boolean clone;
    private boolean lockXMovement;

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

    ProcessingAnimation extraAnimation;

    float xAcceleration = 0;
    float yAcceleration = 0;
    boolean dragged = false;

    private float initialSpawnX;
    private float initialSpawnY;

    final private float GRAVITY_THRESHOLD = 0.01f;


    Rotater hitBy = null;
    public MovableBox(PApplet p, PublicInformation info, float xLocation, float yLocation, int width, int height) {
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
        angleToRotor = 0;
        this.clone = false;
        this.opacity = 255;
        this.initialSpawnX = xLocation;
        this.initialSpawnY = yLocation;
        lockXMovement = false;


    }

    @Override
    protected void drawAnimation() {
        applyGravity();

        if (isHit()) {
            applyGravity();

        }
            //gravity
        if (! isDragged()) {
            setxLocation((getxLocation() + getxAcceleration()));
            setyLocation((getyLocation() + getyAcceleration()));
        }


        Iterator<FadeAnimation> it = fadeAnimations.iterator();
        while (it.hasNext()) {
            FadeAnimation f = it.next();
            if (f.isStartAnimation()) {
                f.drawAnimation();
            } else {
                it.remove();
            }
        }




        if (isHit()) {
            //setFill(Color.red);
            pressed();
        } else {
            release();
            //setFill(initialFill);
            playsound = true;
        }
        if (getSound() != null) {
            if (getSound().getGain() < 0) {
                //System.out.println("this shit has gain" + getSound().getGain());
                //System.out.println("width was " + this.width);

                this.opacity = (int) p.map(getSound().getGain(), -24, 0, 10, 255);
                this.width = (int) p.map(this.opacity, 10, 255, this.width, this.width * 2.0f);
                this.height = (int) p.map(this.opacity, 10, 255, this.height, this.height * 2.0f);
            } else {
                this.opacity = 255;


            }
        }

        p.fill(getFill().getRGB(), this.opacity);
        p.stroke(getFill().darker().getRGB());
        p.strokeWeight(2);
        p.pushMatrix();
        p.translate(getxLocation(), getyLocation(), 1);

        p.ellipse(0, 0, this.width, this.height);
        p.popMatrix();
        if (getSound() != null) {
            if (sound.getSimpleFilename() != null) {
                p.fill(0);
                String text = sound.getSimpleFilename();
                if (getxAcceleration() != 0.0f) {
                    text = text.concat("\nxAccel:" + getxAcceleration());
                }
                if (getyAcceleration() != 0.0f) {
                    text = text.concat("\nyAccel:" + getyAcceleration());
                }
                p.text(text, xLocation, yLocation - 20);
            }
        }


    }

    private void applyGravity() {
        if (xAcceleration < GRAVITY_THRESHOLD  && xAcceleration > -GRAVITY_THRESHOLD) {
            xAcceleration = 0.0f;
        } else {
            this.xAcceleration = (float) (this.getxAcceleration() / 1.5f);

        }
        if (yAcceleration < GRAVITY_THRESHOLD  && yAcceleration > -GRAVITY_THRESHOLD) {
            yAcceleration = 0.0f;
        } else {
            this.yAcceleration = (float) (this.getyAcceleration() / 1.5f);
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
    public float getxLocation() {
        return xLocation;
    }

    public void setxLocation(float xLocation) {
        this.xLocation = xLocation;
    }

    public float getyLocation() {
        return yLocation;
    }

    public void setyLocation(float yLocation) {
        this.yLocation = yLocation;
    }

    public Sample getSound() {
        return sound;

    }
    public float getCenterX() {
        return ((getxLocation() + getWidth()/2.0f));
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
        if (hit == false) {
            this.width = initialWidth;
            this.height = initialHeight;
            this.getSound().setGain(0);
        }
    }

    public void playsound() {
        if (this.isVisible()) {
            getSound().setLoop(false);

            if (!getSound().isRunning()) {
                FadeAnimation fadeAnimation = new FadeAnimation(this.p, this.info, this.getxLocation(), this.getyLocation(), getWidth());
                fadeAnimation.setFill(this.getFill());
                fadeAnimations.add(fadeAnimation);
                new Thread(getSound()).start();


            }
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
            setFill(this.initialFill.brighter());
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

    public void overrideFill(Color c) {
        this.initialFill = c;
    }

    public Color getInitialFill() {
        return initialFill;
    }

    public float getxAcceleration() {
        return xAcceleration;
    }

    public void setxAcceleration(float xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public float getyAcceleration() {
        return yAcceleration;
    }

    public void setyAcceleration(float yAcceleration) {
        this.yAcceleration = yAcceleration;
    }


    public boolean isDragged() {
        return dragged;
    }

    public void setDragged(boolean dragged) {
        this.dragged = dragged;
    }


    public void setClone(boolean clone) {
        this.clone = clone;
    }

    public boolean isClone() {
        return clone;
    }

    public float getInitialSpawnX() {
        return initialSpawnX;
    }

    public float getInitialSpawnY() {
        return initialSpawnY;
    }

    public boolean isLockXMovement() {
        return lockXMovement;
    }

    public void setLockXMovement(boolean lockXMovement) {
        this.lockXMovement = lockXMovement;
    }

    public void setInitialFill(Color initialFill) {
        this.initialFill = initialFill;
        this.fill = initialFill;
    }
}
