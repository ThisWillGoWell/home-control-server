
package home.controller;

import home.controller.webmanager.Application;
import home.parcel.ParcelArray;
import home.system.chromecast.ChromeCastSystem;
import home.system.clockDisplay.ClockDisplaySystem;
import home.system.coffee.Coffee;
import home.system.engineManager.EngineManagement;
import home.system.hvac.HvacSystem;
import home.system.irRemote.IrRemote;
import home.system.mediaManager.MediaManager;
import home.system.network.NetworkSystem;
import home.system.scheduler.Scheduler;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.system.SystemParent;

import home.system.hue.HueSystem;
import home.system.weather.Weather;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Will on 9/3/2016.
 * Engine of the program, highest level.
 * Starts everything up on boot, and controls the timingg of the check chav control
 *
 * todo: this:
 * Now the engine is also a system parent, which is what he manages
 * This allows him to have things like the update functions
 * I do question if he should be able to do this or sperate it
 * itto a ssytem and leave the management engine part alone
 * Should you really be able to spawn an engine indside an engine?
 * No
 *
 * So need to make a engine subsystem
 * also need a plaece to define type->System
 *
 */

public class Engine{
    private HashMap<String, SystemParent> runningSystems;

    public Engine() {
        initialize();
    }



    /**
     * Read the intial config json and starts up the system
     * {systemIdentifier:{type:sytemType,init:[{initalConfigParcesl},.., {}] }, name2:{...}}
     * ToDo: change into using a multiInput Parcel Parser
     */
    public void initialize() {
        runningSystems = new HashMap<>();
        runningSystems.put("engineManagement", new EngineManagement(this));

        try {
            Parcel initParcel = Parcel.PROCESS_JSONSTR(Application.readFile("resources/init.json", Charset.defaultCharset()));
            for(String systemType: initParcel.keySet()){
                Parcel p = initParcel.getParcel(systemType);
                SystemParent s = makeSystem(systemType);
                runningSystems.put(systemType,s);
                try {
                    if(p.contains("init")){
                        for(Parcel startParcels: p.getParcelArray("init").getParcelArray()) {
                            runningSystems.get(systemType).process(startParcels);
                        }
                    }
                } catch (SystemException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException | SystemException e) {
            e.printStackTrace();
        }

        for (String id : runningSystems.keySet()) {
            runningSystems.get(id).start();
        }


    }

    /**
     * Will make a system based on the type
     * ToDO use some sort of map to prevent this
     * @param type type of system being created
     * @return A new System of type
     * @throws SystemException generic
     */
    private SystemParent makeSystem(String type) throws SystemException {
        switch (type) {
            case HvacSystem.systemIdentifier:
                return new HvacSystem(this);
            case HueSystem.systemIdentifier:
                return new HueSystem(this);
            case Scheduler.systemIdentifier:
                return new Scheduler(this);
            case Weather.systemIdentifier:
                return new Weather(this);
            case Coffee.systemIdentifier:
                return new Coffee(this);
            case NetworkSystem.systemIdentifier:
                return new NetworkSystem(this);
            case IrRemote.systemIdentifier:
                return new IrRemote(this);
            case ClockDisplaySystem.systemIdentifier:
                return new ClockDisplaySystem(this);
            case ChromeCastSystem.systemIdentifier:
                return new ChromeCastSystem(this);
            case MediaManager.systemIdentifier:
                return new MediaManager(this);

            default:
                throw SystemException.ENGINE_EXCEPTION("State Type not decoded:" + type);
        }
    }


    public static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss\t").format(Calendar.getInstance().getTime());
    }


    public static String time() {
        return new SimpleDateFormat("h:mm").format(Calendar.getInstance().getTime());
    }

    public static boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public Parcel digestParcel(Parcel p){
        ParcelArray pa = new ParcelArray();
        pa.add(p);
        try {
            return digestParcels(pa).getParcel(0);
        } catch (SystemException e) {
           return Parcel.RESPONSE_PARCEL_ERROR(e);
        }
    }

    public ParcelArray digestParcels(ParcelArray pa){
        //Logger.log(p.toString(),Logger.LOG_LEVEL_WEB);
        ParcelArray responses = new ParcelArray();
        try {
            for (Parcel p : pa.getParcelArray()) {
                try {
                    if (runningSystems.containsKey(p.getString("system"))) {
                        //Logger.log(response.toString(),Logger.LOG_LEVEL_WEB);
                        responses.add(runningSystems.get(p.getString("system")).command(p));
                    } else {
                        throw SystemException.SYSTEM_NOT_FOUND_EXCEPTION(p);
                    }
                } catch (SystemException e) {
                    Logger.log(e);
                    responses.add(Parcel.RESPONSE_PARCEL_ERROR(e));
                }
            }
        } catch (SystemException e) {
            responses.add(Parcel.RESPONSE_PARCEL_ERROR(e));
        }
        return responses;
    }



    public Parcel addSystem(Parcel p) throws SystemException {
        if(runningSystems.containsKey(p.getString("systemIdentifier"))){
            throw SystemException.ENGINE_EXCEPTION("System already Running: " + p.toString());
        }else {
            SystemParent system = makeSystem(p.getString("type"));
            system.init();
            runningSystems.put(p.getString("systemIdentifier"), system);
            return Parcel.RESPONSE_PARCEL("System Started");
        }
    }



    public Parcel stopSystem(Parcel p) throws SystemException {
        if(runningSystems.containsKey(p.getString("systemIdentifier"))){
            SystemParent system = runningSystems.remove(p.getString("systemIdentifier"));
            system.stop();
            return Parcel.RESPONSE_PARCEL("System Stop Success");
        }
        throw SystemException.ENGINE_EXCEPTION("System does not exist");

    }
}
