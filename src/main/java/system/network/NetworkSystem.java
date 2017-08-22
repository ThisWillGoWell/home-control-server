package system.network;


import controller.Engine;
import controller.PP;
import controller.PS.NetworkSystemStrings;
import controller.webmanager.Application;
import parcel.Parcel;
import parcel.ParcelArray;
import parcel.SystemException;

import sun.nio.ch.Net;
import system.SystemParent;



/**
 * Created by Willi on 9/26/2016.
 * dig +short myip.opendns.com @resolver1.opendns.com
 */
public class NetworkSystem extends SystemParent{


    private static class SS{
        private static final String passwordKey = "password";

        private static final String windowsArpCommand = "arp -a";
        private static final String windowsPingCommand = "ping %s -n 1 -w 1";

    }


    public static final String systemIdentifier = NetworkSystemStrings.NETWORK_SYSTEM_NAME1;


    private static Parcel DEFAULT_SYSTEM_STATE() throws SystemException {
        Parcel p = new Parcel();
        p.put(NetworkSystemStrings.DEVICES_KEY, DEVICESS());
        p.put(NetworkSystemStrings.TIMEOUT_KEY,5000);

        ParcelArray macs = new ParcelArray();
        for(String key: p.getParcel(NetworkSystemStrings.DEVICES_KEY).keySet())
        {
            macs.add(p.getParcel(NetworkSystemStrings.DEVICES_KEY).getParcel(key).getString(NetworkSystemStrings.MAC_KEY));
        }
        p.put("macs", macs);
        return p;
    }

    private static Parcel DEVICE_PARCEL(String mac){
        return DEVICE_PARCEL(mac, "");
    }

    private static Parcel DEVICE_PARCEL(String mac, String ip){
        Parcel p = new Parcel();
        p.put(NetworkSystemStrings.CONNECTED_KEY, false);
        p.put(NetworkSystemStrings.MAC_KEY, mac);
        p.put(NetworkSystemStrings.IP_KEY,ip);
        p.put(NetworkSystemStrings.LAST_PING_TIME_KEY,0);
        return p;

    }

    private static Parcel DEVICESS() {
        Parcel p = new Parcel();
        p.put(NetworkSystemStrings.DINOLIGHT, DEVICE_PARCEL(NetworkSystemStrings.DINOLIGHT_MAC));
        p.put(NetworkSystemStrings.CHROMECAST_AUDIO_BEDROOM, DEVICE_PARCEL(NetworkSystemStrings.CHROMECAST_AUDIO_BEDROOM_MAC));
        p.put(NetworkSystemStrings.PHILIPS_HUE, DEVICE_PARCEL(NetworkSystemStrings.PHILIPS_HUE_MAC));
        p.put(NetworkSystemStrings.WILL_PHONE_ANDROID, DEVICE_PARCEL(NetworkSystemStrings.WILL_PHONE_ANDROID_MAC, "192.168.1.9"));
        p.put(NetworkSystemStrings.WILL_PHONE_IPHONE, DEVICE_PARCEL(NetworkSystemStrings.WILL_PHONE_IPHONE_MAC));
        return p;
    }

    private Parcel state;

    public NetworkSystem(Engine e) throws SystemException {
        super(systemIdentifier, e, 1000);
        state = DEFAULT_SYSTEM_STATE();

    }

    @Override
    public synchronized Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")){
            case "get":
                switch (p.getString("what")){
                    case NetworkSystemStrings.CONNECTED_KEY:
                        return Parcel.RESPONSE_PARCEL(isConnected(p.getString(NetworkSystemStrings.DEVICE_KEY)));
                    case NetworkSystemStrings.IP_KEY:
                        return Parcel.RESPONSE_PARCEL(getIP(p.getString(NetworkSystemStrings.DEVICE_KEY)));

                    default:
                        throw SystemException.WHAT_NOT_SUPPORTED(p);
                }

            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }
    }

    private boolean  isConnected(String name) throws SystemException {
        return state.getParcel(NetworkSystemStrings.DEVICES_KEY).getParcel(name).getBoolean(NetworkSystemStrings.CONNECTED_KEY);
    }

    private String getIP(String name) throws SystemException {
        return state.getParcel(NetworkSystemStrings.DEVICES_KEY).getParcel(name).getString(NetworkSystemStrings.IP_KEY);
    }

    /*
    ########################################################################
                            Update Code below
    #######################################################################
     */

    @Override
    public synchronized void update() {
        try {
            Parcel arpTable = null;
            String pingCommand = "";
            if (Application.isWindows()) {
                arpTable = processArpWindows(Application.executeCommand(SS.windowsArpCommand));
                pingCommand = SS.windowsPingCommand;
            }else{
                //TODO UNIX COMMANDS
            }


            if (arpTable != null) {
                for (String key : state.getParcel(NetworkSystemStrings.DEVICES_KEY).keySet()) {
                    Parcel deviceParcel = state.getParcel(NetworkSystemStrings.DEVICES_KEY).getParcel(key);
                    String mac = deviceParcel.getString(NetworkSystemStrings.MAC_KEY);
                    if (!deviceParcel.getBoolean(NetworkSystemStrings.CONNECTED_KEY) && arpTable.contains(mac)) {
                        deviceParcel.put(NetworkSystemStrings.IP_KEY, arpTable.get(mac));
                    }

                    if(!deviceParcel.getString(NetworkSystemStrings.IP_KEY).equals("")){
                        String pingResult = Application.executeCommand(String.format(pingCommand, deviceParcel.getString(NetworkSystemStrings.IP_KEY)));
                        boolean pingRespnse;
                        if(Application.isWindows()){
                            pingRespnse = processPingResultWindows(pingResult);
                        }else{
                            pingRespnse = processPingResultUnix(pingResult);
                        }
                        if(pingRespnse){
                            deviceParcel.put(NetworkSystemStrings.LAST_PING_TIME_KEY, System.currentTimeMillis());
                        }

                        deviceParcel.put(NetworkSystemStrings.CONNECTED_KEY,
                                System.currentTimeMillis() - deviceParcel.getLong(NetworkSystemStrings.LAST_PING_TIME_KEY) < state.getLong(NetworkSystemStrings.TIMEOUT_KEY) );

                    }else{
                        deviceParcel.put(NetworkSystemStrings.CONNECTED_KEY, false);
                    }
                }
            }


            //System.out.println(state.getParcel(PS.DEVICES_KEY).getParcel("will-phone-android"));
        } catch (SystemException e) {
            e.printStackTrace();
        }

    }

    private Parcel processArpWindows(String arpTable){
        String lines[] = arpTable.split("\n");
        Parcel p = new Parcel();
        for(int i=2; i< lines.length;i++){
            String device[] = lines[i].split("\\s+");
            p.put(device[2].replace('-',':'), device[1]);
        }
        return p;
    }

    private boolean processPingResultWindows(String pingResult){
        return !pingResult.contains("100% loss");
    }

    private boolean processPingResultUnix(String pingResult){
        return !pingResult.contains("100% packet loss");
    }

    public static void main(String args[]) throws SystemException {
        SystemParent network = new NetworkSystem(null);
        network.start();


        class dummpSystem extends SystemParent{

            public dummpSystem() {
                super("dummy");
            }

            @Override
            public Parcel process(Parcel p) throws SystemException {
                System.out.println(p);
                return null;

            }
            @Override
            public void subscriptionAlert(Parcel p){
                System.out.println(p);
            }

        }
        SystemParent system2 = new dummpSystem();
        Parcel subPar = new Parcel();
        subPar.put("op", "subscribe");
        subPar.put("subscriber", system2);
        subPar.put("type", "change");
        subPar.put("what",NetworkSystemStrings.CONNECTED_KEY);
        subPar.put("device","will-phone-android");
        System.out.println(network.command(PP.NetworkPP.GetConnected(NetworkSystemStrings.WILL_PHONE_ANDROID)));
        System.out.println(network.command(PP.NetworkPP.SubscribeToConnected(system2, NetworkSystemStrings.WILL_PHONE_ANDROID)));
        subPar.put("op", "get");
        subPar.put("what", "ip");
        subPar.put("device", NetworkSystemStrings.CHROMECAST_AUDIO_BEDROOM);
        System.out.println(network.command(subPar));
        while(true){

        }
    }
}
