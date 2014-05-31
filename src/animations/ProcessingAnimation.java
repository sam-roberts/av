package animations;

import processing.core.PApplet;
import helpers.PublicInformation;

import java.awt.*;

/**
 * Created by Sam on 13/05/2014.
 */
public abstract class ProcessingAnimation {
    PApplet p;
    int opacity;
    PublicInformation info;
    int duration = 2000;
    int startTime;
    boolean hide = false;
    ColourManager colourManager;

    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    Color fill;
    float scale = 1.0f;

    boolean keyTriggered;
    private boolean loop;

    public ProcessingAnimation(PApplet p, PublicInformation info){
        this.p = p;
        opacity=100;
        this.info = info;
        startTime = -1;
        keyTriggered = false;
        colourManager = new ColourManager();
        fill = Color.green;
    }
    public void draw() {
        if (isVisible()){
            drawAnimation();
        }
    }

    public void drawTimed() {
        if (startTime== -1) {
            startTime = p.millis();
        }

        if (isVisible()) {
            if ( (p.millis() - startTime < duration)){
                drawAnimation();
            } else {
                resetTimer();
                resetTimedAnimation();
                if (isLoop()) {
                    keyTriggered=true;
                }
            }
        }
    }
    private void resetTimer() {
        startTime = -1;
        keyTriggered = false;
        hide=false;

    }

    protected abstract void drawAnimation();

    protected abstract void resetTimedAnimation();

    public boolean isVisible() {
        return (opacity > 0 && !hide);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
    public void setTriggered(boolean b) {
        this.keyTriggered=b;
    }

    public boolean isKeyTriggered() {
        return this.keyTriggered;
    }
    public void killAnimation() {
        this.hide = true;
        resetTimer();
        resetTimedAnimation();
    }


    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isLoop() {
        return loop;
    }
}
