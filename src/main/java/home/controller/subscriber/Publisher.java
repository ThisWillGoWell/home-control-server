package home.controller.subscriber;

import home.parcel.Parcel;
import home.parcel.SystemException;

/**
 * Created by Will on 3/4/2017.
 * Publisher is someone who can Publish to a subscriber or broadcast
 */
public interface Publisher {
    String getPublisherIdentifer();
    Parcel process(Parcel p) throws SystemException;

}
