package animations;

import helpers.PublicInformation;
import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 13/09/2014.
 */
public class LinkedAnimations extends ProcessingAnimation {

    Map<MovableBox, List<MovableBox>> map;


    public LinkedAnimations(PApplet p, PublicInformation info) {
        super(p, info);
        map = new HashMap<MovableBox, List<MovableBox>>();

    }

    public void addConnection(MovableBox parent, MovableBox child) {
        if (map.containsKey(parent)) {
        } else {
            ArrayList<MovableBox> list = new ArrayList<MovableBox>();
            map.put(parent, list);

        }
        if (child != null) {
            map.get(parent).add(child);
        }

    }


    @Override
    protected void drawAnimation() {
        p.pushMatrix();
        p.translate(0,0,1);
        for (MovableBox parent : map.keySet()) {
            List<MovableBox> list = map.get(parent);
            p.stroke(parent.getFill().getRGB());

            for (MovableBox box: list) {
                p.line(parent.getxLocation(), parent.getyLocation(), box.getxLocation(), box.getyLocation());
            }
        }
        p.popMatrix();
    }

    @Override
    protected void resetTimedAnimation() {

    }
}
