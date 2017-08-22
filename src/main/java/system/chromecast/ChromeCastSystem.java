package system.chromecast;
import controller.Engine;
import controller.Logger;
import controller.PP;
import controller.PS;
import controller.PS.ChromeCastPS;
import parcel.Parcel;
import parcel.ParcelArray;
import parcel.StateValue;
import parcel.SystemException;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;
import system.SystemParent;


import java.io.IOException;
import java.security.GeneralSecurityException;


import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
 *
 * Created by Willi on 12/4/2016.
 * ChromeCastSystem Device System
 * Going to have state variable and update() will
 * work on matching the state to the ChromeCasts lib
 * Should help with having diffrent threads (process, update) accessing
 * the chromecasts obejct
 */
public class ChromeCastSystem extends SystemParent{

    
    private Parcel state;

    public static final String systemIdentifier = PS.CHROMECAST_SYSTEM_NAME;


    /**
     * ChromeCastSystem System Strings
     * @param e Engine this system is tied to
     */
    public ChromeCastSystem(Engine e) {
        super(systemIdentifier, e, 1000);
        state = StateInit();
    }

    /**
     * Init gets called after each of the systems are up and running
     * before the frist update of all systems
     */
    public void init() throws SystemException {
        for (Parcel p : state.getParcelArray(ChromeCastPS.CHROME_CASTS_KEY).getParcelArray()){
            engine.digestParcel(PP.NetworkPP.SubscribeToIp(this, p.getString(ChromeCastPS.CHROME_CAST_NAME_KEY)));
            Parcel ipParcel =  engine.digestParcel(PP.NetworkPP.GetIp( p.getString(ChromeCastPS.CHROME_CAST_NAME_KEY)));
            if(ipParcel.success()){
                p.put(ChromeCastPS.IP_KEY, ipParcel.toPayload());
            }

        }
    }

    @Override
    public void subscriptionAlert(Parcel p){
        if(p.success()){

        }
    }


    /**
     * Default state of ChromeCastSystem state Object
     * {
     *     ChromeCastsKey: [] array of casts to be controller
     * }
     * @return
     */
    private static Parcel StateInit(){
        Parcel p = new Parcel();

        //p.put(ChromeCastPS.CHROME_CAST_NAME_MAP, new StateValue(ChromecastNameMap(), StateValue.READ_PRIVLAGE));
        //p.put("connectedCasts", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(ChromeCastPS.CHROME_CASTS_KEY, new StateValue(Chromecasts(), StateValue.READ_PRIVLAGE));

        return p;

    }

    /**
     * Build the list of chromecast objecsts
     * @return the list of chromecasts to be controlled
     */
    private static ParcelArray Chromecasts(){
        ParcelArray p = new ParcelArray();
        p.add(castContainerParcel(ChromeCastPS.CHROMECAST_AUDIO_BEDROOM));
        return p;
    }

    /**
     * Default states for the chromecasts objects
     * holds all the info needed to control the chromecasts
     * @param id
     * @return
     */
    private static Parcel castContainerParcel(String id){
        Parcel p = new Parcel();
        p.put(ChromeCastPS.CHROME_CAST_NAME_KEY, id);
        p.put(ChromeCastPS.IP_KEY,"");
        p.put(ChromeCastPS.CONNECTED_KEY, false);
        p.put(ChromeCastPS.STATUS_KEY, null);
        p.put(ChromeCastPS.CHROMECAST_PENDING_PARCELS, new ParcelArray());
        p.put(ChromeCastPS.PORT_KEY, 0);
        return p;
    }




    /**
     * This is where the parcels get passed into to process though the pendingParcels() array
     * How each of each of the chromecasts are controlled
     * @param cast  the cast obejct being controlled
     * @param p the parcel to act
     * @throws SystemException if parcel to process is invalid
     * @throws IOException If something else goes wrong?
     */
    private void handelParcel(ChromeCast cast, Parcel p) throws SystemException, IOException {
        switch (p.getString(ChromeCastPS.OP_KEY)){
            case ChromeCastPS.CLOSE_OP:
                closeApp(cast);
                break;
            case ChromeCastPS.PLAY_COMMAND:
                switch (ChromeCastPS.WHAT_KEY) {
                    case ChromeCastPS.PLAY_RADIO_COMMAND:
                        playRadio(cast, p);
                }
                break;
            case ChromeCastPS.CONTROL_OP:
                switch (p.getString("action")) {
                    default:
                        throw SystemException.GENERIC_EXCEPTION("Invlaid Action", p);

                    case ChromeCastPS.PLAY_COMMAND:
                        cast.play();
                        break;
                    case ChromeCastPS.PAUSE_COMMAND:
                        cast.pause();
                        break;
                    case ChromeCastPS.VOLUME_COMMAND:
                        cast.setVolume(p.getDouble(ChromeCastPS.VOLUME_LEVEL_KEY).floatValue());
                        break;
                    case ChromeCastPS.MUTE_COMMAND:
                        cast.setMuted(p.getBoolean(ChromeCastPS.MUTE_VALUE_KEY));
                        break;
                    case ChromeCastPS.SEEK_COMMAND:
                        cast.seek(p.getDouble(PS.ChromeCastPS.SEEK_TIME_KEY));
                        break;

                }
                break;
        }
    }

    /**
     * Close the app on the current cast?
     * No idea how it behaves on the chromecast audios
     * @param cast to be set to backdrop
     * @throws SystemException if parcel is malformed
     * @throws IOException
     */
    private void closeApp(ChromeCast cast) throws SystemException, IOException {
        if(cast.isAppAvailable(ChromeCastPS.BACKDROP_APP_NAME)){
            cast.launchApp(ChromeCastPS.BACKDROP_APP_NAME);
        }
    }

    /**
     * Play radio on the current chromecast
     * @param cast
     * @param p
     * @throws SystemException
     */
    private void playRadio(ChromeCast cast, Parcel p) throws SystemException {
        try {
            Parcel stationInfo = radioStation.radioStations().getParcel(p.getString("stationID"));
            String appId = ChromeCastPS.MEDIA_APP_NAME;
            if (cast.isAppAvailable(appId) && !cast.getStatus().isAppRunning(appId)) {
                cast.launchApp(appId);
            }
            cast.load(stationInfo.getString("url"));
        }
        catch (Exception e){
            throw SystemException.GENERIC_EXCEPTION(e);
        }
    }


    private synchronized void updateCast(Parcel castParcel) throws SystemException {
        ChromeCast cast = new ChromeCast(castParcel.getString("ip"), castParcel.getInteger("port"));
        cast.setAutoReconnect(false);
        if(!cast.isConnected()) {
            try {
                cast.connect();
                handelParcel(cast, (Parcel) castParcel.getParcelArray("pendingParcels").remove(0));
                cast.disconnect();

            } catch (IOException | GeneralSecurityException e) {
                Logger.log(this, e);
            }
        }
    }

    public void update() {
        try {
            for(String castName : state.getParcel(ChromeCastPS.CHROME_CASTS_KEY).keySet()){
                //@TODO put all these into update queue
                Parcel castParcel = state.getParcel(ChromeCastPS.CHROME_CASTS_KEY).getParcel(castName);
                if(castParcel.getParcelArray(ChromeCastPS.CHROMECAST_PENDING_PARCELS).size() != 0){
                    updateCast(castParcel);
               }
            }
        }
        catch (SystemException e) {
            Logger.log(this, e);
        }
    }

    private void printAllChromecastID(){

        try {
            ChromeCasts.startDiscovery();
            Thread.sleep(3000);
            for (ChromeCast cast : ChromeCasts.get()) {
                cast.connect();
                System.out.println(cast.getName());
                System.out.println(cast.getAddress());
                cast.disconnect();
            }

            ChromeCasts.stopDiscovery();

        } catch (InterruptedException | GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }


    }

    private synchronized Parcel discover(){
        try {
            ChromeCasts.startDiscovery();
            for(int i=0;i<4;i++){
                if(ChromeCasts.get().size() == 0){
                    Logger.log(this, "No cast DEVICES_KEY found during discovery", Logger.LOG_LEVEL_DEBUG);
                    Thread.sleep(3000);

                }
            }

            for (ChromeCast cast : ChromeCasts.get()) {
                Logger.log(this, "Found Cast: " + cast.getName() + "@ " + cast.getAddress() + ":" + cast.getPort(), Logger.LOG_LEVEL_DEBUG);
                if (state.getParcel(ChromeCastPS.CHROME_CAST_NAME_MAP).contains(cast.getName())) {
                    state.getParcel(ChromeCastPS.CHROME_CASTS_KEY).getParcel(state.getParcel(ChromeCastPS.CHROME_CAST_NAME_MAP).getString(cast.getName())).put("ip", cast.getAddress());
                    state.getParcel(ChromeCastPS.CHROME_CASTS_KEY).getParcel(state.getParcel(ChromeCastPS.CHROME_CAST_NAME_MAP).getString(cast.getName())).put("port", cast.getPort());
                }
            }
            ChromeCasts.stopDiscovery();
            return Parcel.RESPONSE_PARCEL("discover Success");
        }
        catch (Exception e) {
            Logger.log(this, e);
            return Parcel.RESPONSE_PARCEL_ERROR(SystemException.GENERIC_EXCEPTION(e));
        }

    }

    /**
     * Since we need may need to connect before we can use the cast object
     * we dont want it to block the thread that handles the request
     * @param p Parcel to be handeled
     * @return response Parcel
     * @throws SystemException
     */
    public Parcel process(Parcel p) throws SystemException {
            if(p.contains(ChromeCastPS.CAST_NAME_KEY)){
                state.getParcel(ChromeCastPS.CHROME_CASTS_KEY).getParcel(p.getString(ChromeCastPS.CAST_NAME_KEY)).getParcelArray(ChromeCastPS.CHROMECAST_PENDING_PARCELS).add(p);
                return Parcel.RESPONSE_PARCEL("Parcel Queued to cast pending parcels");
            }
            else{
                switch (p.getString(ChromeCastPS.OP_KEY)) {
                    case ChromeCastPS.GET_OP_KEY:
                        switch (p.getString(ChromeCastPS.WHAT_KEY)) {
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
      test3();
    }

    private static void test(){
        try {
            ChromeCasts.startDiscovery();
            Thread.sleep((1700));
            for(ChromeCast cast : ChromeCasts.get()) {
                cast.connect();
            }
        } catch (IOException | InterruptedException | GeneralSecurityException e) {
            e.printStackTrace();

        }
    }



    private static void test2(){
        try {


            ParcelArray parcels = new ParcelArray();

            ChromeCastSystem system = new ChromeCastSystem( null);
            Parcel p = new Parcel();
            p.put("op", "play");
            p.put("what", "radio");
            p.put("stationID", "rock");
            p.put("castName", "kitchen");
            parcels.add(p);

            p=new Parcel();
            p.put("op", "control");
            p.put("action", "pause");
            p.put("castName", "kitchen");
            parcels.add(p);

            p = new Parcel();
            p.put("op", "control");
            p.put("action", "play");
            p.put("castName", "kitchen");
            parcels.add(p);

            p=new Parcel();
            p.put("op", "control");
            p.put("action", "pause");
            p.put("castName", "kitchen");
            parcels.add(p);


            for(Parcel cur :parcels.getParcelArray()){
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

    public static void test3() throws InterruptedException {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create();

            // Add a service listener
            jmdns.addServiceListener("_http._tcp.local.", new SampleListener());

            // Wait a bit
            Thread.sleep(30000);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



}
