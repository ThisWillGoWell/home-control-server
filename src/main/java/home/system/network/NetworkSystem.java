package home.system.network;


import home.controller.Engine;
import home.controller.PP;
import home.controller.PS.NetworkSystemStrings;
import home.controller.webmanager.Application;
import home.parcel.Parcel;
import home.parcel.ParcelArray;
import home.parcel.SystemException;

import home.system.SystemParent;
import static home.controller.PS.NetworkSystemStrings.*;
import static home.controller.PS.NetworkDevices.*;
import static home.controller.PS.NETWORK_SYSTEM_NAME;



/**
 * Created by Willi on 9/26/2016.
 * dig +short myip.opendns.com @resolver1.opendns.com
 */
public class NetworkSystem extends SystemParent{


    private static class SS{
        private static final String passwordKey = "password";

        private static final String windowsArpCommand = "arp -a";
        private static final String windowsPingCommand = "ping %s -n 1 -w 1";
        private static final String UNIX_ARP_COMMAND = "arp -a";
        private static final String UNIX_PING_COMMAND = "ping %s -W 1 -c 1";
    }


    public static final String systemIdentifier = NETWORK_SYSTEM_NAME;


    private static Parcel DEFAULT_SYSTEM_STATE() throws SystemException {
        Parcel p = new Parcel();
        p.put(DEVICES_KEY, DEVICESS());
        p.put(TIMEOUT_KEY,5000);

        ParcelArray macs = new ParcelArray();
        for(String key: p.getParcel(DEVICES_KEY).keySet())
        {
            macs.add(p.getParcel(DEVICES_KEY).getParcel(key).getString(MAC_KEY));
        }
        p.put("macs", macs);
        return p;
    }

    private static Parcel DEVICE_PARCEL(String mac){
        return DEVICE_PARCEL(mac, "");
    }

    private static Parcel DEVICE_PARCEL(String mac, String ip){
        Parcel p = new Parcel();
        p.put(CONNECTED_KEY, false);
        p.put(MAC_KEY, mac);
        p.put(IP_KEY,ip);
        p.put(LAST_PING_TIME_KEY,0);
        return p;

    }

    private static Parcel DEVICESS() {
        Parcel p = new Parcel();
        p.put(DINOLIGHT, DEVICE_PARCEL(DINOLIGHT_MAC));
        p.put(CHROMECAST_AUDIO_BEDROOM, DEVICE_PARCEL(CHROMECAST_AUDIO_BEDROOM_MAC));
        p.put(PHILIPS_HUE, DEVICE_PARCEL(PHILIPS_HUE_MAC));
        p.put(WILL_PHONE_ANDROID, DEVICE_PARCEL(WILL_PHONE_ANDROID_MAC, "192.168.1.9"));
        p.put(WILL_PHONE_IPHONE, DEVICE_PARCEL(WILL_PHONE_IPHONE_MAC));
        p.put(CHROMECAST_BEDROOM, DEVICE_PARCEL(CHROMECAST_BEDROOM_MAC, "192.168.1.4"));
        p.put(CHROMECAST_LIVINGROOM, DEVICE_PARCEL(CHROMECAST_LIVINGROOM_MAC));
        return p;
    }

    private Parcel state;

    public NetworkSystem(Engine e) throws SystemException {
        super(systemIdentifier, e, 500);
        state = DEFAULT_SYSTEM_STATE();

    }

    @Override
    public synchronized Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")){
            case "get":
                switch (p.getString("what")){
                    case CONNECTED_KEY:
                        return Parcel.RESPONSE_PARCEL(isConnected(p.getString(DEVICE_KEY)));
                    case IP_KEY:
                        return Parcel.RESPONSE_PARCEL(getIP(p.getString(DEVICE_KEY)));

                    default:
                        throw SystemException.WHAT_NOT_SUPPORTED(p);
                }

            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }
    }

    private boolean  isConnected(String name) throws SystemException {
        return state.getParcel(DEVICES_KEY).getParcel(name).getBoolean(CONNECTED_KEY);
    }

    private String getIP(String name) throws SystemException {
        return state.getParcel(DEVICES_KEY).getParcel(name).getString(IP_KEY);
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
                arpTable = processArpUnix(Application.executeCommand(SS.UNIX_ARP_COMMAND));
                pingCommand = SS.UNIX_PING_COMMAND;
            }


            if (arpTable != null) {
                for (String key : state.getParcel(DEVICES_KEY).keySet()) {
                    Parcel deviceParcel = state.getParcel(DEVICES_KEY).getParcel(key);
                    String mac = deviceParcel.getString(MAC_KEY);
                    if (!deviceParcel.getBoolean(CONNECTED_KEY) && arpTable.contains(mac)) {
                        deviceParcel.put(IP_KEY, arpTable.get(mac));
                    }

                    if(!deviceParcel.getString(IP_KEY).equals("")){
                        String pingResult = Application.executeCommand(String.format(pingCommand, deviceParcel.getString(IP_KEY)));
                        boolean pingRespnse;
                        if(Application.isWindows()){
                            pingRespnse = processPingResultWindows(pingResult);
                        }else{
                            pingRespnse = processPingResultUnix(pingResult);
                        }
                        if(pingRespnse){
                            deviceParcel.put(LAST_PING_TIME_KEY, System.currentTimeMillis());
                        }

                        deviceParcel.put(CONNECTED_KEY,
                                System.currentTimeMillis() - deviceParcel.getLong(LAST_PING_TIME_KEY) < state.getLong(TIMEOUT_KEY) );

                    }else{
                        deviceParcel.put(CONNECTED_KEY, false);
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

    private static Parcel processArpUnix(String arpTable){
        String lines[] = arpTable.split("\n");
        Parcel p = new Parcel();
        for(int i=0; i< lines.length;i++){
            String device[] = lines[i].split(" ");
            p.put(device[3], device[1].replace("(","").replace(")", ""));
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
        System.out.println(processArpUnix("HomeRouter (192.168.1.1) at 2c:4d:54:db:f5:38 [ether] on eth0\n" +
                "Chromecast-Audio (192.168.1.15) at a4:77:33:f1:93:76 [ether] on eth0\n" +
                "Liveware (192.168.1.5) at 88:d7:f6:7f:40:e1 [ether] on eth0"));


        class dummpSystem extends SystemParent{
            private long lastTime = 0;
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
                System.out.println(System.currentTimeMillis() - lastTime);
                lastTime = System.currentTimeMillis();
                 System.out.println(p);
            }

        }
        SystemParent system2 = new dummpSystem();
        Parcel subPar = new Parcel();
        subPar.put("op", "subscribe");
        subPar.put("subscriber", system2);
        subPar.put("type", "change");
        subPar.put("what",CONNECTED_KEY);
        subPar.put("device","will-phone-android");
        System.out.println(network.command(PP.NetworkPP.GetConnected(WILL_PHONE_ANDROID)));
        System.out.println(network.command(PP.NetworkPP.SubscribeToConnected(system2, WILL_PHONE_ANDROID)));
        subPar.put("op", "get");
        subPar.put("what", "ip");
        subPar.put("device", CHROMECAST_AUDIO_BEDROOM);
        System.out.println(network.command(subPar));
        while(true){

        }
    }
}
