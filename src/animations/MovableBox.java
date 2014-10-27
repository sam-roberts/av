package animations;

import com.sun.javaws.exceptions.MissingFieldException;
import geomerative.RG;
import geomerative.RShape;
import helpers.PublicInformation;
import helpers.Sample;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Sam on 31/05/2014.
 */
public class MovableBox extends ProcessingAnimation {

    public static final int BIG_BUTTON = 70;
    public static final int RECORDED_WIDTH = 250;
    public static final int RECORDED_HEIGHT = 100;
    public static final int NODE_WIDTH = 50;


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
    private boolean recording;



    MovableBox child;
    MovableBox parent;

    private boolean original;
    private boolean toDelete;

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

    PImage extraAnimation2;


    float xAcceleration = 0;
    float yAcceleration = 0;
    boolean dragged = false;

    private float initialSpawnX;
    private float initialSpawnY;

    final private float GRAVITY_THRESHOLD = 0.01f;

    RShape graphic;
    PShape graphic2;


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
        this.recording = false;
        child = null;
        parent = null;
        original = true;

        toDelete = false;
        graphic = null;







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
                this.width = (int) p.map(this.opacity, 10, 255, this.width, this.width * 1.5f);
                this.height = (int) p.map(this.opacity, 10, 255, this.height, this.height * 1.5f);
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
        p.shapeMode(p.CENTER);
        if (graphic2 != null) {
            p.fill(0);
            p.stroke(0);


            //graphic.draw();

            p.shape(graphic2, 0,0,getWidth()*0.65f, getWidth()*0.65f);



        }
        p.popMatrix();
        if (getSound() != null) {
            if (sound.getSimpleFilename() != null) {
                p.fill(0);
                String text = sound.getCategory();
                /*

                text = text.concat( "\nhash: " + this.hashCode() + "\n");

                if (getxAcceleration() != 0.0f) {
                    text = text.concat("\nxAccel:" + getxAcceleration());
                }
                if (getyAcceleration() != 0.0f) {
                    text = text.concat("\nyAccel:" + getyAcceleration());
                }
                */
                //TODO: needed?
               //p.text(text, xLocation, yLocation - getWidth());
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
            this.initialFill = colourManager.getRandomColor(sound.getCategory());
            this.fill = initialFill;
            System.out.println(sound.getCategory() + sound.getCategory().hashCode());



            File f = new File(info.getRootDirectory() + "images\\" + sound.getCategory()+".svg");

            if (f.isFile()) {
                //graphic = RG.loadShape(f.getAbsolutePath());
                graphic2 = p.loadShape(f.getAbsolutePath());

                graphic2.setFill(Color.WHITE.getRGB());
                graphic2.setStroke(Color.WHITE.getRGB());

            }


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
        if (this.isVisible() && getSound() != null) {
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

    public void setRecording(boolean recording) {
        this.recording = recording;
    }

    public boolean isRecording() {
        return recording;
    }

    public MovableBox cloneThis() throws FileNotFoundException {
        MovableBox temp = new MovableBox(p,info,getxLocation(),getyLocation(),initialWidth,initialHeight);
        temp.setClone(true);
        Sample copy = getSound();
        Sample newSound = new Sample(info, copy.getFilepath());
        newSound.setCategory(copy.getCategory());
        temp.setSound(newSound);
        temp.overrideFill(getInitialFill());
        temp.setOriginal(false);
        return temp;

    }

    public MovableBox getChild() {
        return child;
    }

    public void setChild(MovableBox child) {

        if (getParent() != null) {
            System.out.println("PARENT OF " + this.hashCode() + " IS " + getParent().hashCode());

        }

        if (child == null) {
            System.out.println("SET CHILD ON " + this.hashCode() + " to NULL");

            this.child = child;
        } else {

            System.out.println("SET CHILD ON " + this.hashCode() + " to " + child.hashCode());
            //simple case where we are just adding a node to the end
            if (getChild() == null) {
                this.child = child;
                child.setParent(this);
                child.setChild(null);

                System.out.println("PARENT OF " + child.hashCode() + " is  " + child.getParent().hashCode());


            } else {
                throw new RuntimeException("don't do this");
            }
        }


    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public MovableBox getParent() {
        return parent;
    }

    public void setParent(MovableBox parent) {
        this.parent = parent;
    }

    public void setInitialSpawnX(float initialSpawnX) {
        this.initialSpawnX = initialSpawnX;
    }

    public void setInitialSpawnY(float initialSpawnY) {
        this.initialSpawnY = initialSpawnY;
    }

    public MovableBox getLastChild() {
        MovableBox iter = this;
        MovableBox iter2 = this;

        if (iter == null || iter2 == null) {
            throw  new RuntimeException("getLastChild of a null object");
        }

        if (iter2.getChild() == null) {
            return this;
        }

        while (iter2 != null) {
            iter = iter.getChild();
            iter2 = iter.getChild();

        }
        return  iter;
    }

    public void deleteThis() {
        System.out.println("flagging a node to be deleted on next cycle");
        if (getChild() != null) {
            System.out.println(" parent x " + getParent().getxLocation() + " this x " + getxLocation() + " child location " + getChild().getxLocation());
            getParent().forceSetChild(getChild());
            getChild().setParent(getParent());
        } else {
            System.out.println("node has no children");

            System.out.println("parent hash: " + this.getParent().hashCode() + " this hash: " + hashCode());

            //should be impossible;
            if (getParent() != null) {
                //somehow this isn't working
                getParent().forceSetChild(null);

                if (getParent().getChild() == null ){
                    System.out.println("working normally - The parent no longer points to this");

                } else {
                    throw new RuntimeException(("WTF IS GOING ON"));
                }

            } else {
                throw new NullPointerException("somehow trying to delete the head node (should be impossible)");
            }
        }

    }

    public void flagToDelete() {
        toDelete = true;

    }

    private void forceSetChild(MovableBox child) {
        this.child = child;

    }


    public boolean isToDelete() {
        return toDelete;
    }
}
