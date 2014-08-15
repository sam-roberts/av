package helpers;

import animations.MovableBox;
import animations.ProcessingAnimation;
import animations.Rotater;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Created by Sam on 31/05/2014.
 */
public class CollisionManager {

    public static final int CUSHION = 10;
    public static boolean isLineInsideCircle(int l1x, int l1y, int l2x, int l2y, int p1x, int p1y, int circleRadius) {
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

        if (distance - circleRadius/2 < CUSHION) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Distance from point 1 to point 2
     * @param p1x
     * @param p1y
     * @param p2x
     * @param p2y
     * @return
     */
    public static double  distancePoints(int p1x, int p1y, int p2x, int p2y) {
        Point2D p1 = new Point(p1x,p1y);
        Point2D p2 = new Point(p1x,p1y);
        return p1.distance(p2);


    }

    public static double getAngle(int p1x, int p1y, int p2x, int p2y) {

        double angle = Math.toDegrees(Math.atan2(p2y - p1y, p2x - p1x));
        angle = angle - 90;
        if (angle < 0 ) {
            angle += 360;
        }
        return ((angle) % 360);

    }

    public static boolean isPointInsideCircle(MovableBox point, Rotater circle) {
        int xdistance = (int) Math.pow(point.getxLocation() - circle.getxOrigin(), 2);
        int ydistance = (int) Math.pow(point.getyLocation() - circle.getyOrigin(), 2);
        int radius = (int) Math.pow(circle.getLength(), 2);
        if ( (xdistance + ydistance) < radius ) {
            return true;
        } else {
            return false;
        }
    }
}
