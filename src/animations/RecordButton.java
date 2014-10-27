package animations;

import helpers.PublicInformation;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.awt.*;

/**
 * Created by sam on 29/07/2014.
 */
public class RecordButton extends MovableBox {


    boolean isRecording = false;
    String text = "RECORD";

    PShape microphone;

    float scaleFactor = 0.15f;
    float micWidth;
    float micHeight;
    public RecordButton(PApplet p, PublicInformation info, int xLocation, int yLocation, int width, int height) {
        super(p, info, xLocation, yLocation, width, height);

        microphone = p.loadShape(info.getRootDirectory() + "images\\mic.svg");
        microphone.setFill(Color.WHITE.getRGB());
        microphone.setStroke(Color.WHITE.getRGB());

        micWidth = microphone.getWidth() * scaleFactor;
        micHeight = microphone.getHeight() * scaleFactor;

        initialFill = info.getColourManager().getRandomColor("record");
        setFill(initialFill);
    }

    @Override
    protected void drawAnimation() {

        p.fill(getFill().getRGB(), this.opacity);
        p.noStroke();
        p.pushMatrix();
        p.translate(xLocation, yLocation);


        p.rect(0, 0, this.width, this.height);


        p.shape(microphone, 15,(getHeight() / 2.0f) - (micWidth / 4.0f),micWidth, micHeight);
        p.fill(255);
        p.textSize(30);
        p.text(text, micWidth+120,getHeight()/2);

        p.popMatrix();



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
        setFill(initialFill);
        return;
    }
}
