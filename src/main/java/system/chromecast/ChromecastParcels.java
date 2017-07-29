package system.chromecast;

import parcel.Parcel;

/**
 * Created by Will on 3/20/2017.
 */
public class ChromecastParcels {

    public static Parcel playRadioParcel( String castName, String stationID){
        Parcel p = new Parcel();
        p.put("system", Chromecast.systemIdentifier);
        p.put("op", "playRadio");
        p.put("stationID", stationID);
        p.put("castName", castName);
        return p;
    }


    public static Parcel mediaControlParcel(String castName, String action){
        Parcel p = new Parcel();
        p.put("system", Chromecast.systemIdentifier);
        p.put("op", "mediaControl");
        p.put("action", action);
        p.put("castName", castName);
        return p;
    }
}
