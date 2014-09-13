package helpers;

/**
 * Created by Sam on 13/09/2014.
 */
public class DoubleClick {
    private static final int TOLLERANCE = 300;
    Object target = null;
    private int time = 0;
    public void setClick(Object clickTarget, int time) {
        if (clickTarget != null) {
            this.time = time;
            target = clickTarget;
        }

    }
    public boolean isDoubleClick(Object clickedTarget, int currentTime) {
        return  (target == clickedTarget && (currentTime -  time < TOLLERANCE));
    }

    public void reset() {
        target = null;
        time = 0;
    }
}
