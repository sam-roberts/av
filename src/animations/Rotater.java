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
    float xOrigin = 0;
    public float getyOrigin() {
        return yOrigin;
    }

    public float getxOrigin() {
        return xOrigin;
    }

    float yOrigin = 0;

    SpeedSlider slider;

    private final double initialSpeed;
    public Rotater(PApplet p, PublicInformation info, double SPEED) {
        super(p, info);
        this.speed = SPEED;
        this.setFill(Color.green);
        this.slider = new SpeedSlider(p, info, 0,0, SPEED);
        initialSpeed = SPEED;

    }

    public int getWholeMeasureMs () {
        return (int)(60/(float)(info.getTempo() * getSpeed())*4* 1000) * 4;

    }
    @Override
    protected void drawAnimation() {
        int time = p.millis()% getWholeMeasureMs();



        int musicPump = (int) p.map(info.getTempo(),info.TEMPO_MIN,info.TEMPO_MAX,0,255);


        int cRed = 128, cGreen = 128, cBlue = 128;
        if (info.getTempo() > 120) {
            cRed = (int) p.map(info.getTempo(),120,info.TEMPO_MAX, 128, ColourManager.MENU_RED.getRed());
            cGreen = (int) p.map(info.getTempo(),120,info.TEMPO_MAX, 128, ColourManager.MENU_RED.getGreen());
            cBlue = (int) p.map(info.getTempo(),120,info.TEMPO_MAX, 128, ColourManager.MENU_RED.getBlue());

        } else {
            cRed = (int) p.map(info.getTempo(),info.TEMPO_MIN,120, ColourManager.MENU_BLUE.getRed(), 128);
            cGreen = (int) p.map(info.getTempo(),info.TEMPO_MIN,120, ColourManager.MENU_BLUE.getGreen(), 128);
            cBlue = (int) p.map(info.getTempo(),info.TEMPO_MIN, 120, ColourManager.MENU_BLUE.getBlue(), 128);
        }


        //Color c = new Color(musicPump, 255-musicPump,150);
        Color c = new Color(cRed, cGreen, cBlue);


        drawGrid();

        //rotateDegrees = (rotateDegrees + 5) % 360f;
        rotateDegrees = p.map(time, 0,getWholeMeasureMs(),0,360);


        p.pushMatrix();
        p.translate(getxOrigin(), getyOrigin(),0);
        p.stroke(120);
        p.rotate(p.radians(rotateDegrees-90));
        p.strokeWeight(5);

        p.line(0, 0, getLength(), 0);
        p.stroke(c.getRGB());
        p.strokeWeight(2);

        p.fill(255);
        p.ellipse(0,0,getLength()*2,getLength()*2);

        p.popMatrix();

        slider.draw();


    }

    private void drawGrid() {
        //draw the grid
        p.pushMatrix();
        p.translate(getxOrigin(), getyOrigin());
        p.strokeWeight(5);
        p.stroke(235);

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

    public SpeedSlider getSlider() {
        return slider;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void setOrigin(float x, float y) {
        this.xOrigin = x;
        this.yOrigin = y;

        getSlider().setxLocation(x);

        if (y < p.getHeight()/2) {
            getSlider().setyLocation(y - getLength() - 50 * info.getRatio());
        } else {
            getSlider().setyLocation(y + getLength() + 40 * info.getRatio());
        }
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


    public Point2D.Float getPositionFromPercentage(float anglePercent, float volumePercent) {

        float angleFromDegree = p.map(anglePercent, 0.0f, 100, 0, (float) (2 * Math.PI));
        float distanceFromCenter = p.map(volumePercent, 0.0f, 100, 0, getLength()-5);

        float x = (float) (getxOrigin() + distanceFromCenter * Math.cos((angleFromDegree - Math.PI/2)));
        float y = (float) (getyOrigin() + distanceFromCenter * Math.sin((angleFromDegree - Math.PI/2)));
        Point2D.Float point = new Point2D.Float(x,y);

        return point;
    }

    public double getInitialSpeed() {
        return initialSpeed;
    }
}
