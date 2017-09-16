package home.controller;

import home.controller.subscriber.Subscriber;
import home.parcel.Parcel;

public class PP {


    /**
     * Generate a basic GET parcel
     * @param system System you want to get
     * @param what the value you want to get
     * @param args other args int the form of {key0, value0, key1, value1...}
     * @return the get parcel
     */
    public static Parcel GetSystemValue(String system, String what, String... args){
        Parcel p = new Parcel();
        p.put(PS.GenericPS.SYSTEM_KEY,system);
        p.put(PS.GenericPS.OP_KEY, PS.GenericPS.GET_OP_KEY);
        p.put(PS.GenericPS.WHAT_KEY, what);

        return addArgs(p, args);
    }

    /**
     * Adds all the arguments to the Parcel
     * @param p
     * @param args
     * @return
     */
    public static Parcel addArgs(Parcel p, String... args){
        for(int i=0;i<args.length/2; i++){
            p.put(args[2*i],args[2*i + 1]);
        }
        return p;
    }


    /**
     * Subscribe to a systems get call
     * @param subscriber who is subsribing
     * @param system what system am I subscribing too
     * @param what what am I subscribing too inside the system
     * @param args other args int the form of {key0, value0, key1, value1...}
     * @return the sub parcel
     */
    public static Parcel SubscribeChange(Subscriber subscriber, String system, String what, String... args){
        Parcel p = new Parcel();

        p.put(PS.GenericPS.OP_KEY, PS.GenericPS.SUB_COMMAND);
        p.put(PS.GenericPS.SUBSCRIBER_KEY, subscriber);
        p.put(PS.GenericPS.SUB_TYPE_KEY, PS.GenericPS.SUB_CHANGE_TYPE);
        p.put(PS.GenericPS.SYSTEM_KEY, system);
        p.put(PS.GenericPS.WHAT_KEY, what);
        return addArgs(p, args);
    }

    /**
     * Subscribe to a systems get call
     * @param subscriber who is subsribing
     * @param system what system am I subscribing too
     * @return the sub parcel
     */
    public static Parcel SubscribeAlert(Subscriber subscriber, String system){
        Parcel p = new Parcel();
        p.put(PS.GenericPS.SUBSCRIBER_KEY, subscriber);
        p.put(PS.GenericPS.SYSTEM_KEY, system);
        p.put(PS.GenericPS.SUB_TYPE_KEY,  PS.GenericPS.SUB_ALERT_TYPE);
        return p;
    }



    public static class ChromecastPP {

        public static Parcel PlayRadioParcel( String castName, String stationID){
            Parcel p = new Parcel();
            p.put(PS.GenericPS.SYSTEM_KEY, PS.CHROMECAST_SYSTEM_NAME);
            p.put(PS.GenericPS.OP_KEY, PS.ChromeCastPS.PLAY_COMMAND);
            p.put(PS.ChromeCastPS.STATION_KEY, stationID);
            p.put(PS.ChromeCastPS.CAST_NAME_KEY, castName);
            return p;
        }

    /*
        public static Parcel MediaControlParcel(String castName, String action){
            Parcel p = new Parcel();
            p.put("system", ChromeCastSystem.systemIdentifier);
            p.put("op", "mediaControl");
            p.put("action", action);
            p.put("castName", castName);
            return p;
        }
        */
    }

    public static class NetworkPP{
        public static Parcel SubscribeToIp(Subscriber subscriber, String device){
            return SubscribeChange(subscriber, PS.NETWORK_SYSTEM_NAME, PS.NetworkSystemStrings.IP_KEY, PS.NetworkSystemStrings.DEVICE_KEY, device);
        }
        public static Parcel GetIp(String device){
            return GetSystemValue(PS.NETWORK_SYSTEM_NAME, PS.NetworkSystemStrings.IP_KEY, PS.NetworkSystemStrings.DEVICE_KEY, device);
        }
        public static Parcel SubscribeToConnected(Subscriber subscriber, String device){
            return SubscribeChange(subscriber, PS.NETWORK_SYSTEM_NAME, PS.NetworkSystemStrings.CONNECTED_KEY, PS.NetworkSystemStrings.DEVICE_KEY, device);
        }
        public static Parcel GetConnected(String device){
            return GetSystemValue(PS.NETWORK_SYSTEM_NAME, PS.NetworkSystemStrings.CONNECTED_KEY, PS.NetworkSystemStrings.DEVICE_KEY, device);
        }
    }


}
