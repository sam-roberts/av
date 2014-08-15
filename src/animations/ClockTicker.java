package animations;

import helpers.Observer;
import helpers.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 14/08/2014.
 */
public class ClockTicker implements Subject {

    private List<Observer> observers;

    public ClockTicker() {
        this.observers = new ArrayList<Observer>();
    }

    @Override
    public void register(Observer obj) {
        if (obj == null) {
            throw new NullPointerException("Null observer being added");
        }
        observers.add(obj);
    }

    @Override
    public void unregister(Observer obj) {
        observers.remove(obj);
    }

    @Override
    public void setObservedList(List<Observer> list) {
        this.observers = list;
    }

    @Override
    public void notifyObservers() {
        for (Observer o: observers) {
            o.update();
        }
    }

    @Override
    public Object getUpdate(Observer obj) {
        return null;
    }
}
