package helpers;

import java.awt.geom.Line2D;

/**
 * Created by Sam on 31/05/2014.
 */
public class CollisionManager {


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

        System.out.println("distance is " + distance);
        if (distance - circleRadius/2 < 1) {
            return true;
        } else {
            return false;
        }
    }
}
