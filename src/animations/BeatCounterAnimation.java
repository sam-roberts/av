package animations;

import helpers.Duration;
import helpers.PublicInformation;
import processing.core.PApplet;

/**
 * Created by Sam on 13/08/2014.
 */
public class BeatCounterAnimation extends ProcessingAnimation {

    int numMeasures = 1;
    int numQuarters = 1;
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
        double quarterTime = wholeBarTime/4;
        if (p.millis() - timer >= quarterTime * numQuarters) {
            numQuarters++;
        }
        if ( p.millis() - timer >= wholeBarTime) {
            numMeasures++;
            timer = p.millis();
            numQuarters=1;
        }
        p.fill(0,40);
        p.text(numMeasures + " | " + numQuarters + " Tempo: " + info.getTempo(), 150,20);
    }

    @Override
    protected void resetTimedAnimation() {

    }
}
