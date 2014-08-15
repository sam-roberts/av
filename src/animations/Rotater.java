package animations;

import helpers.Duration;
import helpers.PublicInformation;
import processing.core.PApplet;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by Sam on 31/05/2014.
 */
public class Rotater extends ProcessingAnimation{
    public static final int TYPE_LINEAR = 1;
    public static final int TYPE_ROTARY = 2;
    float rotateDegrees = 0;

    double speed = Duration.QUARTER;
    private int length;
    int xOrigin = 0;
    public int getyOrigin() {
        return yOrigin;
    }

    public int getxOrigin() {
        return xOrigin;
    }

    int yOrigin = 0;

    public Rotater(PApplet p, PublicInformation info, double SPEED) {
        super(p, info);
        this.speed = SPEED;
        this.setFill(Color.green);
    }

    public int getWholeMeasureMs () {
        return (int)(60/(float)(info.getTempo() * getSpeed())*4* 1000) * 4;

    }
    @Override
    protected void drawAnimation() {
        int time = p.millis()% getWholeMeasureMs();

        p.noFill();


        int musicPump = (int) p.map(info.getTempo(),info.TEMPO_MIN,info.TEMPO_MAX,0,255);


        Color c = new Color(musicPump, 255-musicPump,150);
        setFill(c);

        p.stroke(this.getFill().getRGB());

        p.strokeWeight(5);
        //rotateDegrees = (rotateDegrees + 5) % 360f;
        rotateDegrees = p.map(time, 0,getWholeMeasureMs(),0,360);


        p.pushMatrix();
        p.translate(getxOrigin(), getyOrigin());

        p.rotate(p.radians(rotateDegrees-90));
        p.line(0, 0 , getLength(),0);

        p.ellipse(0,0,getLength()*2,getLength()*2);

        p.popMatrix();

        //draw the grid
        p.pushMatrix();
        p.translate(getxOrigin(), getyOrigin());
        p.stroke(64, 64, 64, 30);

        int nSegments = 8;
        float degreePerSegment = 360/nSegments;

        for (int i = 0; i < nSegments; i++) {
            p.line(0,0,getLength(),0);
            p.rotate((float) Math.toRadians(degreePerSegment));

        }
        p.popMatrix();


    }

    @Override
    protected void resetTimedAnimation() {

    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setOrigin(int x, int y) {
        this.xOrigin = x;
        this.yOrigin = y;
    }

    public Point getSecondPoint() {
        int x = (int) (getxOrigin() + getLength() * Math.cos(Math.toRadians(this.rotateDegrees)));
        int y = (int) (getyOrigin() + getLength() * Math.sin(Math.toRadians(this.rotateDegrees)));
       return new Point(x,y);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
