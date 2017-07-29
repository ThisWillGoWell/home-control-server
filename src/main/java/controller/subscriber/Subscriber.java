<<<<<<< HEAD:src/main/java/controller/subscriber/Subscriber.java
package controller.subscriber;

import parcel.Parcel;

/**
 * Created by Willi on 12/30/2016.
 * Subscriber, someone who can reieve alerts
 */
public interface Subscriber {
    void subscriptionAlert(Parcel p);
    long subscriptionID = 0;
}
=======
package controller;

import parcel.Parcel;

/**
 * Created by Willi on 12/30/2016.
 */
public interface Subscriber {
    void subscriptionAlert(Parcel p);
    long subscriptionID = 0;
}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/controller/Subscriber.java
