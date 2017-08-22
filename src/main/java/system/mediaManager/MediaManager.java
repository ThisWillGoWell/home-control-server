package system.mediaManager;

import controller.Engine;
import parcel.Parcel;
import parcel.ParcelArray;
import parcel.SystemException;
import system.SystemParent;

import system.irRemote.IrRemoteParcels;

/**
 * Created by Will on 3/16/2017.
 *  This system is responsible for manageing the
 *  audio and audio systems in the app
 *  Should just be a bunch of ops really
 *
 *  I think that useally no outside system  talk directly with
 *  chomecast or IrRemote
 **/


public class MediaManager extends SystemParent{
    Parcel state;
    public static final String systemIdentifier = "media";

    public MediaManager(Engine e) {
        super(systemIdentifier, e);
        state = defaultMediaManagerState();

    }

    private static Parcel defaultMediaManagerState(){
        Parcel p = new Parcel();
        Parcel ops = new Parcel();

        ParcelArray op1Parcels = new ParcelArray();
        op1Parcels.add(IrRemoteParcels.setPreset("tvRoom", "bedroomChromecast" ));
        //op1Parcels.add(ChromecastParcels.playRadioParcel("bedroom", "news"));
        ops.put("playNewsUpstairs", op1Parcels);
        p.put("ops", ops);
        return p;
    }



    @Override
    public Parcel process(Parcel p) throws SystemException {
        return sendParcelArray(state.getParcel("ops").getParcelArray(p.getString("op")));
    }

    private Parcel sendParcelArray(ParcelArray pa) throws SystemException {
        ParcelArray returnParcels = new ParcelArray();
        for(Parcel p: pa.getParcelArray()){
            returnParcels.add(engine.digestParcel(p));
        }
        Parcel p = new Parcel();
        p.put("sent", pa);
        p.put("got", returnParcels);
        return Parcel.RESPONSE_PARCEL(p);
    }
}
