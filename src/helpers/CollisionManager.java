package helpers;

import animations.MovableBox;
import animations.Rotater;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Created by Sam on 31/05/2014.
 */
public class CollisionManager {

    public static final int CUSHION = 10;
    public static boolean isLineInsideCircle(float l1x, float l1y, float l2x, float l2y, float p1x, float p1y, float circleRadius) {
        //y = mx + b
        float gradientLine = Float.MAX_VALUE;
        if (l2x - l1x !=0) {
            gradientLine = (l2y - l1y) / (l2x - l1x);
        }

        //y - y1 = m(x-x1)

        // y - l1y = gradientLine(x-l1x)
        float b = l1y - (gradientLine * l1x);

        //float distance = (float) (Math.abs(p1y - gradientLine*p1x - b) / Math.sqrt(Math.pow(gradientLine,2) + 1));
        double distance = Line2D.ptSegDist(l1x,l1y,l2x,l2y,p1x,p1y);

        return distance - circleRadius / 2 < CUSHION;
    }

    /**
     * Distance from point 1 to point 2
     * @param p1x
     * @param p1y
     * @param p2x
     * @param p2y
     * @return
     */
    public static double  distancePoints(float p1x, float p1y, float p2x, float p2y) {
        Point2D.Float p1 = new Point.Float(p1x,p1y);
        Point2D.Float p2 = new Point2D.Float(p1x,p1y);
        return p1.distance(p2);


    }

    public static double getAngle(float p1x, float p1y, float p2x, float p2y) {

        double angle = Math.toDegrees(Math.atan2(p2y - p1y, p2x - p1x));
        angle = angle - 90;
        if (angle < 0 ) {
            angle += 360;
        }
        return ((angle) % 360);

    }
    public static boolean isPointInsideRectangle(Point2D p1, float topLeftX, float topLeftY, float width, float height) {
        Rectangle2D.Float rect = new Rectangle2D.Float(topLeftX, topLeftY, width, height);
        return rect.contains(p1);
    }
    public static boolean isPointInsideCircle(MovableBox point, Rotater circle) {
        int xdistance = (int) Math.pow(point.getxLocation() - circle.getxOrigin(), 2);
        int ydistance = (int) Math.pow(point.getyLocation() - circle.getyOrigin(), 2);
        int radius = (int) Math.pow(circle.getLength(), 2);
        return (xdistance + ydistance) < radius;
    }
}
