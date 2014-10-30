package helpers;

/**
 * Created by Sam on 15/08/2014.
 */
public class Quantizer {

    private final static int RESOLUTION = 128;

    public static int getDelay(double wholeMeasureMS, double frequency, double angle) {
        double ratio = angle/360;
        if (ratio > 0.95) {
            ratio = 0;
        }
        return (int)(wholeMeasureMS * ratio / frequency);
    }
}
