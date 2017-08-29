package home.system.socketSystem;

import home.controller.webmanager.SocketSession;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.system.SystemParent;

/**
 * Created by Will on 3/19/2017.
 * Socket Systems allow for a websocket connection to act as a system
 * All Parcels sent to the SocketSystem are converted to JSON String
 * and then sends it
 *
 * The SocketSystem works closeyl with the controlWebsocket.py
 * to establish a connection and syc states
 * The idea is you will be able to do basic get and set,
 * where this guy will then work with controlwebsocket to sync states1
 *
 * Socket Stream bby
 *
 * Needs to be able to register with the engine, so now systems
 * are self registering. hmmm
 * acutally, not hard, need to just do in in.
 */
public class SocketSystem extends SystemParent{
    private SocketSession socketSession;

    public SocketSystem(SocketSession s, Parcel initParcel) throws SystemException {
        super(initParcel.getString("system_name"));
        this.socketSession = s;

    }


    @Override
    public Parcel process(Parcel p) throws SystemException {
        return null;
    }
}
