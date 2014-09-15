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
            System.out.println("parent already there");
            map.get(parent).add(child);

        } else {

            //need to check if its a child first

            //if its a clone, its parent must already be a key
            if (parent.isClone()) {
                for (MovableBox key: map.keySet()) {

                    if (map.get(key).contains(parent)) {
                        map.get(key).add(child);
                        if (key.isClone()) {
                            System.out.println("Oh god why is the key a clone");

                        } else {
                            System.out.println("Mapping a node to the original");

                        }
                    }
                }
            } else {
                ArrayList<MovableBox> list = new ArrayList<MovableBox>();
                map.put(parent, list);
                map.get(parent).add(child);
            }



        }



    }


    @Override
    protected void drawAnimation() {
        p.pushMatrix();
        p.translate(0,0,1);
        for (MovableBox parent : map.keySet()) {
            List<MovableBox> list = map.get(parent);
            p.stroke(parent.getFill().getRGB());
            boolean first = true;
            MovableBox previous = null;
            for (MovableBox box: list) {

                if (first) {
                    p.line(parent.getxLocation(), parent.getyLocation(), box.getxLocation(), box.getyLocation());
                    first= false;
                } else {
                    if (previous != null) {
                        p.line(previous.getxLocation(), previous.getyLocation(), box.getxLocation(), box.getyLocation());
                    }


                }
                previous = box;

            }
        }
        p.popMatrix();
    }

    @Override
    protected void resetTimedAnimation() {

    }

    public MovableBox getEndOfLink (MovableBox target) {
        for (MovableBox key : map.keySet()) {
            List<MovableBox> list = map.get(key);
            if (target == key) {
                if (list.size() > 0) {
                    return list.get(list.size()-1);
                } else {
                    return target;
                }
            } else {
                if  (list.contains(target)) {
                    return list.get(list.size()-1);
                }
            }
        }
        return target;

    }
    public void removeConnection(MovableBox target) {
        System.out.println("there are " + map.keySet().size() + " keys");
        for (MovableBox key: map.keySet()) {
            List<MovableBox> list = map.get(key);
            if (list.contains(target)) {
                System.out.println("list is " + list.size());
                list.remove(target);
                System.out.println("list is " + list.size());

                System.out.println("removed object from links");
            } else {
                System.out.println("trying to delete parent node");
            }

        }
    }
}
