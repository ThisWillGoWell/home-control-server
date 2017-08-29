
package home.controller.subscriber;

import home.parcel.Parcel;

/**
 * Created by Willi on 12/30/2016.
 * Subscriber, someone who can reieve alerts
 */
public interface Subscriber {
    void subscriptionAlert(Parcel p);
    long subscriptionID = 0;
}
