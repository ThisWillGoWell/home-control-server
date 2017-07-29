package system.irRemote;

import parcel.Parcel;

/**
 * Created by Will on 3/20/2017.
 */
public class IrRemoteParcels {

    public static Parcel setPreset(String remote,String preset){
        Parcel p = new Parcel();
        p.put("remote", remote);
        p.put("system", IrRemote.systemIdentifier);
        p.put("op", "set");
        p.put("what", "preset");
        p.put("to",preset);
        return p;
    }

}
