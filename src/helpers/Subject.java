package helpers;

import java.util.List;

/**
 * Created by Sam on 14/08/2014.
 */
public interface Subject {
    public void register(Observer obj);
    public void unregister(Observer obj);

    public void setObservedList(List<Observer> list);

    //method to notify observers of change
    public void notifyObservers();

    //method to get updates from subject
    public Object getUpdate(Observer obj);

}
