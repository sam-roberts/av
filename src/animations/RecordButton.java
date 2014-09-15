package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;

/**
 * Created by sam on 29/07/2014.
 */
public class RecordButton extends MovableBox {


    boolean isRecording = false;
    String text = "RECORD";
    public RecordButton(PApplet p, PublicInformation info, int xLocation, int yLocation, int width, int height) {
        super(p, info, xLocation, yLocation, width, height);
    }

    @Override
    protected void drawAnimation() {

        /*
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

        */
        if (getSound() != null) {
            if (getSound().getGain() < 0) {
                //System.out.println("this shit has gain" + getSound().getGain());
                //System.out.println("width was " + this.width);

                this.opacity = (int) p.map(getSound().getGain(), -12, 0, 50, 255);
                this.width = (int) p.map(this.opacity, 50, 255, this.width / 2, this.width * 2);
                this.height = (int) p.map(this.opacity, 50, 255, this.height / 2, this.height * 2);
            } else {
                this.opacity = 255;

            }
        }

        p.fill(getFill().getRGB(), this.opacity);
        p.noStroke();
        p.pushMatrix();
        p.translate(xLocation, yLocation);

        p.ellipse(0, 0, this.width, this.height);
        p.fill(255);
        p.text(text,0 - width/4,0);
        p.popMatrix();

        if (sound != null && sound.getSimpleFilename() != null) {
            p.text(sound.getSimpleFilename(), xLocation,yLocation-20);
        }


    }

    public void startRecording() {
        isRecording = true;
        text="recording...";
        setFill(Color.red);
        return;
    }

    public void stopRecording() {
        isRecording = false;
        text="RECORD";
        return;
    }
}
