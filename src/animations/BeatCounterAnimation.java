package animations;

import helpers.Duration;
import helpers.PublicInformation;
import processing.core.PApplet;

/**
 * Created by Sam on 13/08/2014.
 */
public class BeatCounterAnimation extends ProcessingAnimation {

    int numMeasures = 1;
    int timer;
    public BeatCounterAnimation(PApplet p, PublicInformation info) {
        super(p, info);
        timer = 0;
    }

    @Override
    protected void drawAnimation() {
        if (timer == 0) {
            timer = p.millis();
        }
        int wholeBarTime = info.getDurationMS(Duration.WHOLE);

        if ( p.millis() - timer >= wholeBarTime) {
            numMeasures++;
            timer = p.millis();
        }
        p.fill(0);
        p.text("new bar every " + wholeBarTime + " Number of whole measures: " + numMeasures, 800,800);
    }

    @Override
    protected void resetTimedAnimation() {

    }
}
