package system.chromecast;
import controller.Engine;
import controller.Logger;
import parcel.Parcel;
import parcel.ParcelArray;
import parcel.StateValue;
import parcel.SystemException;
import su.litvak.chromecast.api.v2.Application;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;
import su.litvak.chromecast.api.v2.Status;
import system.SystemParent;

import java.io.IOException;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by Willi on 12/4/2016.
 * Chromecast Device System
 * Going to have state variable and update() will
 * work on matching the state to the ChromeCasts lib
 * Should help with having diffrent threads (process, update) accessing
 * the chromecasts obejct
 */
public class Chromecast extends SystemParent{

    private Parcel state;
    BlockingQueue<Parcel> parcelQueue =new LinkedBlockingQueue<>();
    public static final String systemIdentifier = "chromecast";




    public Chromecast(Engine e) {
        super(systemIdentifier, e, 1000);
        state = StateInit();
    }

    private static Parcel StateInit(){
        Parcel p = new Parcel();
        p.put("app2appId", new StateValue(APP2APPID(), StateValue.READ_PRIVLAGE));
        p.put("chromecastNameMap", new StateValue(ChromecastNameMap(), StateValue.READ_PRIVLAGE));
        //p.put("connectedCasts", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put("chromeCasts", new StateValue(ChromecastStates(), StateValue.READ_PRIVLAGE));
        return p;

    }

    private static Parcel ChromecastNameMap(){
        Parcel p = new Parcel();

        //p.put("bedroom", "Chromecast-b793ae93f154fec6f865745b1c370202");
        //p.put("Chromecast-b793ae93f154fec6f865745b1c370202", "bedroom");

        //p.put("Chromecast-Audio-431665ca9c0421a896478832fb8256e4", "kitchen");
        //p.put("kitchen", "Chromecast-Audio-431665ca9c0421a896478832fb8256e4");

        p.put("Chromecast-Audio-ffaf991f20fac3e6622ed671f0b595ee","computer");
        p.put("computer", "Chromecast-Audio-ffaf991f20fac3e6622ed671f0b595ee");

        p.put("Google-Cast-Group-21EE1B0941604ECBBC54147269B673B8","home");
        p.put("home", "Google-Cast-Group-21EE1B0941604ECBBC54147269B673B8");

        // p.put("Google-Cast-Group-529489EF4F0C44C6A9F2EF9527FE4FCC","livingroom");
        //p.put("livingroom", "Google-Cast-Group-529489EF4F0C44C6A9F2EF9527FE4FCC");

        //p.put("Google-Cast-Group-21EE1B0941604ECBBC54147269B673B8","kitchen-outside");
        //p.put("kitchen-outside", "Google-Cast-Group-21EE1B0941604ECBBC54147269B673B8");

        return p;
    }

    private static Parcel APP2APPID(){
        Parcel p = new Parcel();
        p.put("meida-player", "CC1AD845");
        p.put("You-Tube", "233637DE");
        p.put("Backdrop", "E8C28D3C");
        p.put("projectM", "58CF25DA");

        return p;
    }

    private static Parcel ChromecastStates() {
        Parcel p = new Parcel();
        p.put("kitchen", castContainerParcel("Chromecast-Audio-431665ca9c0421a896478832fb8256e4"));
        p.put("bedroom", castContainerParcel("Chromecast-b793ae93f154fec6f865745b1c370202"));
        p.put("home", castContainerParcel("Google-Cast-Group-E3A504728DD84732A329ED77D9FDE09A"));
        p.put("computer", castContainerParcel("Chromecast-Audio-ffaf991f20fac3e6622ed671f0b595ee"));
        return p;
    }

    private static Parcel castContainerParcel(String id){
        Parcel p = new Parcel();
        p.put("id", id);
        p.put("ip","");
        p.put("connected", false);
        p.put("status", null);
        p.put("pendingParcels", new ParcelArray());
        p.put("status", new Parcel());
        p.put("port",00);
        return p;
    }
    public void init(){
        discover();
    }

    private void handelParcel(ChromeCast cast, Parcel p) throws SystemException, IOException {
        switch (p.getString("op")){
            case "close":
                closeApp(cast);
                break;
            case "play":
                switch (p.getString("what")) {
                    case "radio":
                        playRadio(cast, p);
                }
                break;
            case "control":
                switch (p.getString("action")) {
                    default:
                        throw SystemException.GENERIC_EXCEPTION("Invlaid Action", p);

                    case "play":
                        cast.play();
                        break;
                    case "pause":
                        cast.pause();
                        break;
                    case "volume":
                        cast.setVolume(p.getDouble("level").floatValue() );
                        break;
                    case "mute":
                        cast.setMuted(p.getBoolean("muted"));
                        break;
                    case "seek":
                        cast.seek(p.getDouble("time"));
                        break;

                }
                break;
        }
    }

    private void closeApp(ChromeCast cast) throws SystemException, IOException {
        if(cast.isAppAvailable(state.getParcel("app2appId").getString("Backdrop"))){
            cast.launchApp(state.getParcel("app2appId").getString("Backdrop"));

        }
    }

    private void playRadio(ChromeCast cast, Parcel p) throws SystemException {
        try {
            Parcel stationInfo = radioStation.radioStations().getParcel(p.getString("stationID"));
            String appId = state.getParcel("app2appId").getString(stationInfo.getString("app"));
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
                discover();
            }
        }
    }

    public void update() {
        try {
            for(String castName : state.getParcel("chromeCasts").keySet()){
                Parcel castParcel = state.getParcel("chromeCasts").getParcel(castName);
                if(castParcel.getParcelArray("pendingParcels").size() != 0){
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
                    Logger.log(this, "No cast devices found during discovery", Logger.LOG_LEVEL_DEBUG);
                    Thread.sleep(3000);

                }
            }

            for (ChromeCast cast : ChromeCasts.get()) {
                Logger.log(this, "Found Cast: " + cast.getName() + "@ " + cast.getAddress() + ":" + cast.getPort(), Logger.LOG_LEVEL_DEBUG);
                if (state.getParcel("chromecastNameMap").contains(cast.getName())) {
                    state.getParcel("chromeCasts").getParcel(state.getParcel("chromecastNameMap").getString(cast.getName())).put("ip", cast.getAddress());
                    state.getParcel("chromeCasts").getParcel(state.getParcel("chromecastNameMap").getString(cast.getName())).put("port", cast.getPort());
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
            if(p.contains("castName")){
                state.getParcel("chromeCasts").getParcel(p.getString("castName")).getParcelArray("pendingParcels").add(p);
                return Parcel.RESPONSE_PARCEL("Parcel Queued to cast pending parcels");
            }
            else{
                switch (p.getString("op")) {
                    case "get":
                        switch (p.getString("what")) {
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
                        return Parcel.RESPONSE_PARCEL("Starting Discover on next update");
                    default:
                        throw SystemException.OP_NOT_SUPPORTED(p);
                }

            }
    }

    public static void main(String args[]){
      test2();
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

            Chromecast system = new Chromecast( null);
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


}
