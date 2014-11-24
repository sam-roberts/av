package animations;

import ddf.minim.analysis.FFT;
import helpers.PublicInformation;
import helpers.Sample;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Created by Sam on 16/08/2014.
 */
public class fftAnimation extends ProcessingAnimation {
    Sample s;
    FFT fft;

    PImage circle;

    float thisMax = 0.0f;
    public fftAnimation(PApplet p, PublicInformation info, Sample source) {
        super(p, info);
        s = source;
        if (s == null) throw new NullPointerException("no sample source for the fft animation");
        fft = new FFT(s.getMyPlayer().bufferSize(), s.getMyPlayer().sampleRate());
        fft.logAverages(22,3);
        fft.linAverages(8);

        circle = info.getCircle();
        p.imageMode(p.CENTER);

    }


    protected void drawAnimation() {
        p.pushMatrix();
        p.translate(0,0,-4);
        fft.forward(s.getMyPlayer().mix);
        // avgWidth() returns the number of frequency bands each average represents
        // we'll use it as the width of our rectangles
        int w = p.getWidth()/fft.avgSize();

        int b = fft.avgSize();
        for(int i = 0; i < b; i++)
        {
            float currentStrength = fft.getBand(i);
            // draw a rectangle for each average, multiply the value by 5 so we can see it better
            if (currentStrength > 0.05f) {
                p.noStroke();
                p.fill(getFill().getRGB());

                float bonusStrength = (i*i)/8.0f + 1;

                float totalStrength = currentStrength * bonusStrength;

                if (totalStrength > thisMax) {
                    thisMax = totalStrength;
                }


                int opacity = (int) p.map(totalStrength, 0, thisMax, 0 ,60);

                float size = p.map(totalStrength, 0, thisMax, 200, 2048);
                p.tint(getFill().getRGB(), opacity);
                p.image(circle, getHackyX(), getHackyY(), size, size);




            }


        }
        p.popMatrix();
    }

    private void oldStyle() {
        p.pushMatrix();
        p.translate(0,0,-1);
        fft.forward(s.getMyPlayer().mix);
        // avgWidth() returns the number of frequency bands each average represents
        // we'll use it as the width of our rectangles
        int w = p.getWidth()/fft.avgSize();
        int previousYPosition = 1;
        int numCircles = 30;
        int size = 2000/numCircles;

        for(int i = 0; i < fft.avgSize(); i+= 8)
        {
            float currentStrength = fft.getAvg(i);
            // draw a rectangle for each average, multiply the value by 5 so we can see it better
            if (currentStrength > 1) {
                p.noStroke();
                p.fill(getFill().getRGB());

                int bonusStrength = (i*i)/2;

                int totalStrength = (int) Math.min(currentStrength * bonusStrength, p.getHeight() - 50);
                int opacity = (int) p.map(totalStrength,0, p.getHeight()-50, 0 ,15);


                for (int j = 0; j < numCircles; j++) {
                    int insideOpacity = (int) p.map(j, 0, numCircles, 0, opacity);
                    p.fill(getFill().getRGB(), insideOpacity);

                    p.ellipse(getHackyX(), getHackyY(), j*size,j*size);

                }



            }


        }
        p.popMatrix();
    }

    @Override
    protected void resetTimedAnimation() {

    }
}
