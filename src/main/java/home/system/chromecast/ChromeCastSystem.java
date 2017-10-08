package home.system.chromecast;
import home.controller.Engine;

import home.controller.Logger;
import home.controller.PS;

import home.parcel.Parcel;
import home.parcel.ParcelArray;
import home.parcel.StateValue;
import home.parcel.SystemException;

import home.system.SystemParent;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;


import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;


import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import static home.controller.PP.NetworkPP.GetConnected;
import static home.controller.PP.NetworkPP.GetIp;
import static home.controller.PP.NetworkPP.SubscribeToConnected;
import static home.controller.PS.ChromeCastPS.*;
import static home.controller.PS.GenericPS.*;
import static home.controller.PS.NetworkDevices.*;
import static home.controller.PS.NetworkSystemStrings.DEVICE_KEY;



/**
 *
 * Created by Willi on 12/4/2016.
 * ChromeCastSystem Device System
 * Going to have state variable and update() will
 * work on matching the state to the ChromeCasts lib
 * Should help with having diffrent threads (process, update) accessing
 * the chromecasts object
 */
public class ChromeCastSystem extends SystemParent {



    private Parcel state;

    public static final String systemIdentifier = PS.CHROMECAST_SYSTEM_NAME;

    private static final class config{
        //private static final String systems = {}
    }

    /**
     * ChromeCastSystem System Strings
     *
     * @param e Engine this system is tied to
     */
    public ChromeCastSystem(Engine e) {
        super(systemIdentifier, e, 1000);
        state = StateInit();

        printAllChromecastID();
    }

    /**
     * Init gets called after each of the systems are up and running
     * before the frist update of all systems
     * @todo there is a bug here, does not handel disconnect
     */
    public void init() throws SystemException {
        discover();

        /*
        for (String key : state.getParcel(CHROME_CASTS_KEY).keySet()) {
            Parcel p = state.getParcel(CHROME_CASTS_KEY).getParcel(key);
            engine.digestParcel(SubscribeToConnected(this, p.getString(CHROME_CAST_NAME_KEY)));
            Parcel connectedParcel = engine.digestParcel(GetConnected(p.getString(CHROME_CAST_NAME_KEY)));
            if (connectedParcel.success()) {
                if(connectedParcel.getBoolean(PAYLOAD_KEY)){
                    Parcel ipParcel = engine.digestParcel(GetIp(p.getString(CHROME_CAST_NAME_KEY)));
                    if(ipParcel.success()){
                        p.put(IP_KEY, ipParcel.getString(PAYLOAD_KEY));
                        getChromecastPorts(p);
                    }
                }

            }
        }
            */

    }

    @Override
    public void subscriptionAlert(Parcel p ) {
        try {

            Parcel  deviceParcel= state.getParcel(CHROME_CASTS_KEY).getParcel(p.getParcel(SUB_REQUEST_KEY).getString(DEVICE_KEY));
            deviceParcel.put(CONNECTED_KEY, p.getString(PAYLOAD_KEY));
            if(p.getBoolean(PAYLOAD_KEY)){
                Parcel ipParcel = engine.digestParcel(GetIp(deviceParcel.getString(CHROME_CAST_NAME_KEY)));
                if(ipParcel.success()){
                    getChromecastPorts(deviceParcel);
                }
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }

    }


    private synchronized void getChromecastPorts(Parcel castParcel) throws SystemException {
        try {
            ChromeCasts.startDiscovery(InetAddress.getByName(castParcel.getString(IP_KEY)));
            Thread.sleep(4000);
            for(ChromeCast cast: ChromeCasts.get()){
                if(castParcel.getString(CHROME_CAST_NAME_KEY).equals(cast.getTitle()    )){
                    castParcel.put(PORT_KEY, cast.getPort());
                    castParcel.put(CONNECTED_KEY, true);

                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }




    /**
     * Default state of ChromeCastSystem state Object
     * {
     * ChromeCastsKey: [] array of casts to be controller
     * }
     *
     * @return
     */
    private static Parcel StateInit() {
        Parcel p = new Parcel();

        //p.put(CHROME_CAST_NAME_MAP, new StateValue(ChromecastNameMap(), StateValue.READ_PRIVLAGE));
        //p.put("connectedCasts", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(CHROME_CASTS_KEY, new StateValue(Chromecasts(), StateValue.READ_PRIVLAGE));

        return p;

    }

    /**
     * Build the list of chromecast objecsts
     *
     * @return the list of chromecasts to be controlled
     */
    private static Parcel Chromecasts() {
        Parcel p = new Parcel();
        p.put(CHROMECAST_AUDIO_BEDROOM, castContainerParcel(CHROMECAST_AUDIO_BEDROOM));
        p.put(CHROMECAST_BEDROOM, castContainerParcel(CHROMECAST_BEDROOM, "192.168.1.4"));
        p.put(CHROMECAST_LIVINGROOM, castContainerParcel(CHROMECAST_LIVINGROOM, "192.168.1.62   "));
        return p;
    }

    /**
     * Default states for the chromecasts objects
     * holds all the info needed to control the chromecasts
     *
     * @param id
     * @return
     */
    private static Parcel castContainerParcel(String id) {
        return castContainerParcel(id, "");
    }

    private static Parcel castContainerParcel(String id, String ip) {
        Parcel p = new Parcel();
        p.put(IS_VIRTUAL_KEY, false);
        p.put(CHROME_CAST_NAME_KEY, id);
        p.put(IP_KEY, ip);
        p.put(CONNECTED_KEY, false);
        p.put(STATUS_KEY, "");
        p.put(CHROMECAST_PENDING_PARCELS, new ParcelArray());
        p.put(PORT_KEY, 0);
        return p;
    }

    /**
     * Default states for the chromecasts objects
     * holds all the info needed to control the chromecasts
     *
     * @param id the id of the chomecast
     * @return
     */
    private static Parcel virturalCastContainerParcel(String id) {
        Parcel p = new Parcel();
        p.put(IS_VIRTUAL_KEY, true);
        p.put(CHROME_CAST_NAME_KEY, id);
        p.put(IP_KEY, "");
        p.put(CONNECTED_KEY, false);
        p.put(STATUS_KEY, null);
        p.put(CHROMECAST_PENDING_PARCELS, new ParcelArray());
        p.put(PORT_KEY, 0);
        return p;
    }



    /**
     * This is where the parcels get passed into to process though the pendingParcels() array
     * How each of each of the chromecasts are controlled
     *
     * @param cast the cast obejct being controlled
     * @param p    the parcel to act
     * @throws SystemException if parcel to process is invalid
     * @throws IOException     If something else goes wrong?
     */
    private void handelParcel(ChromeCast cast, Parcel p) throws SystemException, IOException {
        switch (p.getString(OP_KEY)) {
            case CLOSE_OP:
                closeApp(cast);
                break;
            case PLAY_COMMAND:
                switch (p.getString(WHAT_KEY)) {
                    case PLAY_RADIO_COMMAND:
                        playRadio(cast, p);
                }
                break;
            case CONTROL_OP:
                controlCast(cast, p.getString(ACTION_KEY), p);
                break;
        }
    }

    private static void controlCast(ChromeCast cast, String action, Parcel p) throws IOException, SystemException {
        switch (action) {
            default:
                throw SystemException.GENERIC_EXCEPTION("Invlaid Action", p);
            case PLAY_COMMAND:
                cast.play();
                break;
            case PAUSE_COMMAND:
                cast.pause();
                break;
            case VOLUME_COMMAND:
                cast.setVolume(p.getDouble(VOLUME_LEVEL_KEY).floatValue());
                break;
            case MUTE_COMMAND:
                cast.setMuted(p.getBoolean(MUTE_VALUE_KEY));
                break;
            case SEEK_COMMAND:
                cast.seek(p.getDouble(SEEK_TIME_KEY));
                break;
        }

    }

    /**
     * Close the app on the current cast?
     * No idea how it behaves on the chromecast audios
     *
     * @param cast to be set to backdrop
     * @throws SystemException if parcel is malformed
     * @throws IOException
     */
    private void closeApp(ChromeCast cast) throws SystemException, IOException {
        if (cast.isAppAvailable(BACKDROP_APP_NAME)) {
            cast.launchApp(BACKDROP_APP_NAME);
        }
    }

    /**
     * Play radio on the current chromecast
     *
     * @param cast
     * @param p
     * @throws SystemException
     */
    private void playRadio(ChromeCast cast, Parcel p) throws SystemException {
        try {
            Parcel stationInfo = radioStation.radioStations().getParcel(p.getString("stationID"));
            String appId = MEDIA_APP_NAME;
            if (cast.isAppAvailable(appId) && !cast.getStatus().isAppRunning(appId)) {
                cast.launchApp(appId);
            }
            cast.load(stationInfo.getString("url"));
        } catch (Exception e) {
            throw SystemException.GENERIC_EXCEPTION(e);
        }
    }


    private synchronized void updateCast(Parcel castParcel) throws SystemException {

        if(castParcel.getInteger(PORT_KEY) == 0){
            getChromecastPorts(castParcel);
        }
        Logger.log(castParcel.getString(IP_KEY) + castParcel.getInteger(PORT_KEY), Logger.LOG_LEVEL_DEBUG);
        ChromeCast cast = new ChromeCast(castParcel.getString(IP_KEY), castParcel.getInteger(PORT_KEY));
        cast.setAutoReconnect(false);
        if (!cast.isConnected()) {
            try {
                cast.connect();
                handelParcel(cast, (Parcel) castParcel.getParcelArray(CHROMECAST_PENDING_PARCELS).remove(0));
                cast.disconnect();

            } catch (IOException e) {
                Logger.log(this, e);
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public void update() {
        try {
            for (String key : state.getParcel(CHROME_CASTS_KEY).keySet()) {
                Parcel castParcel = state.getParcel(CHROME_CASTS_KEY).getParcel(key);
                //@TODO put all these into update queue
                if (castParcel.getParcelArray(CHROMECAST_PENDING_PARCELS).size() != 0) {
                    updateCast(castParcel);
                }
            }
        } catch (SystemException e) {
            Logger.log(this, e);
        }
    }

    private static void printAllChromecastID() {

        try {
            ChromeCasts.startDiscovery();
            Thread.sleep(3000);
            for (ChromeCast cast : ChromeCasts.get()) {
                cast.connect();
                System.out.println(cast.getName());
                System.out.println(cast.getAddress());
                System.out.println(cast.getTitle());
                System.out.println(cast.getModel());
                cast.disconnect();
            }

            ChromeCasts.stopDiscovery();

        } catch (InterruptedException | GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized Parcel discover() {
        try {
            ChromeCasts.startDiscovery();
            for (int i = 0; i < 4; i++) {
                if (ChromeCasts.get().size() == 0) {
                    //Logger.log(this, "No cast DEVICES_KEY found during discovery", Logger.LOG_LEVEL_DEBUG);
                    Thread.sleep(3000);
                }
            }

            for (ChromeCast cast : ChromeCasts.get()) {
                //Logger.log(this, "Found Cast: " + cast.getName() + "@ " + cast.getAddress() + ":" + cast.getPort(), Logger.LOG_LEVEL_DEBUG);
                if (state.getParcel(CHROME_CAST_NAME_MAP).contains(cast.getTitle())) {
                    state.getParcel(CHROME_CASTS_KEY).getParcel(state.getParcel(CHROME_CAST_NAME_MAP).getString(cast.getName())).put("ip", cast.getAddress());
                    state.getParcel(CHROME_CASTS_KEY).getParcel(state.getParcel(CHROME_CAST_NAME_MAP).getString(cast.getName())).put("port", cast.getPort());
                }
            }
            ChromeCasts.stopDiscovery();
            return Parcel.RESPONSE_PARCEL("discover Success");
        } catch (Exception e) {
            Logger.log(this, e);
            return Parcel.RESPONSE_PARCEL_ERROR(SystemException.GENERIC_EXCEPTION(e));
        }

    }

    /**
     * Since we need may need to connect before we can use the cast object
     * we dont want it to block the thread that handles the request
     *
     * @param p Parcel to be handeled
     * @return response Parcel
     * @throws SystemException
     */
    public Parcel process(Parcel p) throws SystemException {
        if (p.contains(CAST_NAME_KEY)) {
            state.getParcel(CHROME_CASTS_KEY).getParcel(p.getString(CAST_NAME_KEY)).getParcelArray(CHROMECAST_PENDING_PARCELS).add(p);
            return Parcel.RESPONSE_PARCEL("Parcel Queued to cast pending parcels");
        } else {
            switch (p.getString(OP_KEY)) {
                case GET_OP_KEY:
                    switch (p.getString(WHAT_KEY)) {
                        case "state":
                            return Parcel.RESPONSE_PARCEL(state);
                        default:
                            if (state.contains(p.getString("what"))) {
                                StateValue sp = state.getStateParcel(p.getString("what"));
                                if (sp.canRead()) {
                                    return Parcel.RESPONSE_PARCEL(sp.getValue());
                                }
                                throw SystemException.ACCESS_DENIED(p);
                            }
                            throw SystemException.WHAT_NOT_SUPPORTED(p);
                    }
                case "set":
                    switch (p.getString("what")) {
                        default:
                            StateValue sp = state.getStateParcel(p.getString("what"));
                            if (sp.canWrite()) {
                                sp.update(p.get("to"));
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                    }
                case "discover":
                    //updateQueue.add(p);
                    return Parcel.RESPONSE_PARCEL("Starting Discover on next update");
                default:
                    throw SystemException.OP_NOT_SUPPORTED(p);
            }

        }
    }

    public static void main(String args[]) throws InterruptedException {
        printAllChromecastID();

    }

    private static void test() {
        try {
            ChromeCasts.startDiscovery();
            Thread.sleep((1700));
            for (ChromeCast cast : ChromeCasts.get()) {
                cast.connect();
            }
        } catch (IOException | InterruptedException | GeneralSecurityException e) {
            e.printStackTrace();

        }
    }


    private static void test2() {
        try {


            ParcelArray parcels = new ParcelArray();

            ChromeCastSystem system = new ChromeCastSystem(null);
            Parcel p = new Parcel();
            p.put("op", "play");
            p.put("what", "radio");
            p.put("stationID", "rock");
            p.put("castName", "kitchen");
            parcels.add(p);

            p = new Parcel();
            p.put("op", "control");
            p.put("action", "pause");
            p.put("castName", "kitchen");
            parcels.add(p);

            p = new Parcel();
            p.put("op", "control");
            p.put("action", "play");
            p.put("castName", "kitchen");
            parcels.add(p);

            p = new Parcel();
            p.put("op", "control");
            p.put("action", "pause");
            p.put("castName", "kitchen");
            parcels.add(p);


            for (Parcel cur : parcels.getParcelArray()) {
                system.process(cur);
                system.update();
                Thread.sleep(5000);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }

}