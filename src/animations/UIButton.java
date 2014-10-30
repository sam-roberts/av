package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

/**
 * Created by sam on 29/07/2014.
 */
public class UIButton extends MovableBox {


    boolean pressed = false;
    String text;

    public UIButton(PApplet p, PublicInformation info, int xLocation, float yLocation, String text, float width, float height) {
        super(p, info, xLocation, yLocation, width, height);
        this.text = text;
    }

    @Override
    protected void drawAnimation() {


        p.fill(getFill().getRGB(), this.opacity);
        p.noStroke();
        p.pushMatrix();
        p.translate(xLocation, yLocation);

        p.rect(0, 0, this.width, this.height);
        p.fill(255);
        p.textAlign(p.CENTER, p.CENTER);
        p.textSize(22 * info.getRatio());
        p.text(text,getWidth()/2,getWidth()/2);
        p.popMatrix();



    }

    public void turnOn() {
        pressed = true;
        setFill(getInitialFill().darker());
    }

    public void turnOff() {
        setFill(getInitialFill());
        pressed = false;

    }
}
