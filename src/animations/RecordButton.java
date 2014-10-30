package animations;

import ddf.minim.analysis.FFT;
import helpers.PublicInformation;
import processing.core.PApplet;
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

    FFT fft;




    int count;
    boolean countdownFinished = false;
    int startTime;
    public RecordButton(PApplet p, PublicInformation info, float xLocation, int yLocation, float width, float height) {
        super(p, info, xLocation, yLocation, width, height);

        microphone = p.loadShape(info.getRootDirectory() + "images/record.svg");
        microphone.setFill(Color.WHITE.getRGB());
        microphone.setStroke(Color.WHITE.getRGB());

        micWidth = microphone.getWidth() * scaleFactor * info.getRatio();
        micHeight = microphone.getHeight() * scaleFactor * info.getRatio();

        initialFill = info.getColourManager().getRandomColor("record");
        setFill(initialFill);
        count = 3;
        fft = info.getInputFFT();

    }

    @Override
    protected void drawAnimation() {

        if (isRecording) {


            p.pushMatrix();
            p.translate(p.getWidth() / 2.0f, p.getHeight() / 2.0f);

            p.fill(0);
            p.textSize((int)(72 * info.getRatio()));
            if (count != 0) {
                System.out.println("write the countdown");


                p.text(count, 0, 0);
                if (p.millis() - startTime >= 1000) {
                    count--;
                    startTime = p.millis();
                }
                p.textSize(22 * info.getRatio());
                text = "Recording in..." + count;
            } else {
                p.fill(Color.red.getRGB());
                p.text("Recording", 0, 0);
                p.textSize(30 * info.getRatio());

                text = "Recording";
                countdownFinished=true;
            }

            p.popMatrix();


            drawVisual();

        }

        p.fill(getFill().getRGB(), this.opacity);
        p.noStroke();
        p.pushMatrix();
        p.translate(xLocation, yLocation);


        p.rect(0, 0, this.initialWidth, this.initialHeight);


        p.shape(microphone, 50 * info.getRatio(),(getHeight() / 2.0f),micWidth, micHeight);
        p.fill(255);
        p.text(text, micWidth+(120 * info.getRatio()),getHeight()/2);

        p.popMatrix();





    }

    private void drawVisual() {


        //logarithmic visualiser
        //fill(255);
        // perform a forward FFT on the samples in jingle's mix buffer
        // note that if jingle were a MONO file, this would be the same as using jingle.left or jingle.right
        fft.forward(info.getInput().mix);
        int w = (fft.specSize()/128);

        p.noStroke();
        for(int i = 0; i < fft.avgSize(); i++)
        {
            // draw a rectangle for each average, multiply the value by 5 so we can see it better
            p.rect(i*w + p.getWidth()/2.0f - 200, p.getHeight() - 400, i*w + w + p.getWidth()/2.0f - 200, p.getHeight() - fft.getAvg(i)*5 - 400);
        }




    }

    public boolean isCountdownFinished() {
        return countdownFinished;
    }

    public void setCountdownFinished(boolean countdownFinished) {
        this.countdownFinished = countdownFinished;
    }

    public void startRecording() {
        isRecording = true;
        text="recording...";
        startTime = p.millis();

        setFill(Color.red);
    }

    public void stopRecording() {
        isRecording = false;
        text="RECORD";
        setFill(initialFill);
        count = 3;
        countdownFinished = false;
    }
}
