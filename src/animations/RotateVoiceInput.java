package animations;

import ddf.minim.AudioInput;
import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;

/**
 * Created by Sam on 14/05/2014.
 */
public class RotateVoiceInput extends ProcessingAnimation{

    float rotateDegrees = 0;
    final int speed = 2;
    AudioInput in;
    public RotateVoiceInput(PApplet p, PublicInformation info) {
        super(p,info);
        in = info.getMinim().getLineIn();

    }

    @Override
    protected void drawAnimation() {
        p.strokeWeight(5);
        this.in = info.getMinim().getLineIn();
        if (in != null) {
            rotateDegrees = (rotateDegrees + speed) % 360;
            System.out.println("buffer size" + in.bufferSize() + " monitoring? " + in.isMonitoring());

            for (int i =0; i < in.bufferSize() -1; i++) {
                p.stroke(Color.GREEN.getRGB());

                //p.pushMatrix();
                //p.translate(p.getWidth() / 2, p.getHeight() / 2);
                //p.rotate(p.radians(rotateDegrees));
                float leftStrength = in.left.get(i);
                float rightStrength = in.right.get(i);
                if (leftStrength != 0 || rightStrength != 0) {
                    System.out.println("strengths: " + leftStrength + " " + rightStrength);
                }

                p.line(i, 0 + leftStrength*50 + 200,  i+1, in.left.get(i+1)*50 + 200);
                p.stroke(Color.ORANGE.getRGB());


                p.line(i, 0 + in.right.get(i)*50 + 400,  i+1, in.right.get(i+1)*50 + 400);


                //p.popMatrix();
            }
        }
    }

    @Override
    protected void resetTimedAnimation() {

    }

    public void setLineIn (AudioInput in ) {
        this.in = in;
    }

}
