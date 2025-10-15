package g61258.dev3.oxono.utils;

/**
 * An Observer object in the Observer/Observable pattern.
 * Essentially presents an update method which permits the Observable
 * to notify it of any changes.
 *
 */
public interface Observer {

    /**
     * This method is called whenever the observed object has changed.
     */
    void update();
}

