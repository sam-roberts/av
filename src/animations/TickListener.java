package animations;

import helpers.Observer;
import helpers.Sample;
import helpers.Subject;

/**
 * Created by Sam on 14/08/2014.
 */
public class TickListener implements Observer {
    Subject sub;
    Sample sample;

    public TickListener(Sample sample) {
        this.sample = sample;
    }

    @Override
    public void update() {
        System.out.println("tick listener hello");
    }

    @Override
    public void setSubject(Subject sub) {
        this.sub = sub;
    }
}
