<<<<<<< HEAD
package controller;


import controller.webmanager.Application;
import system.chromecast.Chromecast;
import system.clockDisplay.ClockDisplaySystem;
import system.coffee.Coffee;
import system.engineManager.EngineManagement;
import system.hvac.HvacSystem;
import system.irRemote.IrRemote;
import system.mediaManager.MediaManager;
import system.network.NetworkSystem;
import system.scheduler.Scheduler;
import parcel.Parcel;
import parcel.SystemException;
import system.SystemParent;

import system.hue.HueSystem;
import system.weather.Weather;

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
            case "hvac":
                return new HvacSystem(this);
            case "hue":
                return new HueSystem(this);
            case "scheduler":
                return new Scheduler(this);
            case "weather":
                return new Weather(this);
            case "coffee":
                return new Coffee(this);
            case "network":
                return new NetworkSystem(this);
            case "irRemote":
                return new IrRemote(this);
            case "clock":
                return new ClockDisplaySystem(this);
            case "chromecast":
                return new Chromecast(this);
            case "media":
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
        try {
            Logger.log(p.toString(),Logger.LOG_LEVEL_WEB);
            Parcel response;
            if(runningSystems.containsKey(p.getString("system"))){
                response = runningSystems.get(p.getString("system")).command(p);
                Logger.log(response.toString(),Logger.LOG_LEVEL_WEB);
                return response;
            }
            else{
                throw SystemException.SYSTEM_NOT_FOUND_EXCEPTION(p);
            }
        } catch (SystemException e) {
            Logger.log(e);
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        }
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
=======
package controller;


import parcel.Parcel;
import parcel.SystemException;
import system.SystemParent;

import system.clockDisplay.ClockDisplaySystem;
import system.hue.HueSystem;
import system.hvac.HvacSystem;
import system.weather.Weather;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Will on 9/3/2016.
 * Engine of the program, highest level.
 * Starts evryhting up on boot, and controls the timeing of the check chav cotnrol
 */
public class Engine {
    private HashMap<String, SystemParent> systems;

    HashMap<String, SystemParent> getSystems(){
        return systems;
    }
    public Engine() {
        initialize();
    }


    private void initialize() {
        systems = new HashMap<>();
        systems.put("weather", new Weather(this));
        systems.put("HVAC", new HvacSystem(this));
        //systems.put("clock", new ClockDisplaySystem(this));
        systems.put("lights", new HueSystem(this));
        //systems.put("coffee", new Coffee(this));
        //systems.put("spotify", new Spotify(this));
        for (String id : systems.keySet()) {
            (new Thread(systems.get(id))).start();
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

    public Parcel command(Parcel p){
        try {
            Parcel response = systems.get(p.getString("system")).command(p);
            return response;
        } catch (SystemException e) {
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        } catch (NullPointerException e){
            return Parcel.RESPONSE_PARCEL_ERROR(SystemException.SYSTEM_NOT_FOUND_EXCEPTION(e,p));
        }
    }
}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32
