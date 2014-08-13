package animations;

import helpers.Duration;
import helpers.PublicInformation;
import processing.core.PApplet;

/**
 * Created by sam on 12/08/2014.
 */
public class CountdownAnimation extends ProcessingAnimation
{
    int count;
    public CountdownAnimation(PApplet p, PublicInformation info) {
        super(p, info);
        setDuration(info.getMsCount(Duration.EIGHTH));
        count = 0;
    }

    @Override
    protected void drawAnimation() {
        //p.fill(0);
        //p.text("TESTING A COUNTDOWN ANIMATION" + count + " update every " + this.getDuration(), 500,500);
    }

    @Override
    protected void resetTimedAnimation() {
        count = count + 1;
        setDuration(info.getMsCount(Duration.EIGHTH));

    }
}
