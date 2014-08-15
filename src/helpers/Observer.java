package helpers;

/**
 * Created by Sam on 14/08/2014.
 */
public interface Observer {
    public void update();

    public void setSubject(Subject sub);
}
