package home.system.hue;

import com.philips.lighting.model.PHLight;
import home.parcel.Parcel;
import home.parcel.SystemException;

import java.util.ArrayList;

/**
 * Created by Willi on 11/21/2016.
 * Class to manage Hue motion Scenes
 *
 */


class HueMotionScene {
    long startTime;
    long updateInterval ;
    protected long lastUpdateTime;
    HueSystem system;
    ArrayList<String> lights;

    /*
    Keep track of all the lights involved with the pattern
     */
    HueMotionScene(HueSystem system, long updateInterval){
        lights = new ArrayList<>();
        this.updateInterval = updateInterval;
        this.system = system;
        this.lastUpdateTime = System.currentTimeMillis();
        this.startTime = lastUpdateTime;
        try {
            system.process(Parcel.SET_PARCEL("","allLights","off"));
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    long getUpdateTime(){
        return updateInterval;
    }

    /*
    on update, check if they need to be ran again
    if yes, do one "step" of the program
    @NOTE: if the system laggs at all, then steps will be missed ie missed steps are missed
     */
    void update() throws SystemException {
        if (lastUpdateTime + updateInterval <= System.currentTimeMillis()) {
            lastUpdateTime = System.currentTimeMillis();
            step();
        }
    }

    void step() throws SystemException {

    }
}

class RainbowScene extends HueMotionScene{
    private long cycleTime;

    RainbowScene(HueSystem system) throws SystemException {
        super(system, 500);
        startTime =System.currentTimeMillis();
        cycleTime = 10000;
        //Determine order;

        Parcel name2Light = null;
        lights.add("tv");
        lights.add("strip");
        lights.add("lamp");
        lights.add("door");
        lights.add("bathroom");

        system.process(Parcel.SET_PARCEL("","allLights","off"));

        for(String light:lights) {
            system.process(HueParcel.SET_LIGHT_ON_PARCEL(light));
            system.process(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535,254,254));

        }
        system.process(HueParcel.SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL());
        step();
    }

    void step() throws SystemException {
        //Determine
        int count = 0;
        long startHue = (System.currentTimeMillis() % cycleTime) *65535/cycleTime;
        for(String light : lights){
            Parcel p = HueParcel.SET_LIGHT_HSV_PARCEL(light, (int) ((startHue +  (65535/lights.size() * count)) % 65535), -1,-1);
            system.process(p);
            count++;
        }

    }
}
/*
class HueShiftScene extends HueMotionScene{
    private long cycleTime;

   HueShiftScene(HueSystem system){
        super(system, 500);
        startTime =System.currentTimeMillis();
        cycleTime = 10000;
        //Determine order;

        Parcel lights = null;
        lights.add("tv");
        lights.add("strip");
        lights.add("lamp");
        lights.add("door");
        lights.add("bathroom");

        system.process(Parcel.SET_PARCEL("","allLights","off"));

        for(String light:lights) {
            system.process(HueParcel.SET_LIGHT_ON_PARCEL(light));
            system.process(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535,254,254));
        }
        system.process(HueParcel.SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL());

        step();
    }

    void step(){
        //Determine
        int count = 0;
        long startHue = (System.currentTimeMillis() % cycleTime) *65535/cycleTime;
        Parcel p = HueParcel.SET_ALL_LIGHTS_HSV_PARCEL((int) ((startHue +  (65535/lights.size() * count)) % 65535), -1,-1);
        system.process(p);

    }
}*/


class RandomColors extends HueMotionScene{
    private long minFlashTime = 40;
    private long maxFlashTime = 100;
    RandomColors(HueSystem system) throws SystemException {
        super(system, 500);
        startTime =System.currentTimeMillis();
        lights.add("tv");
        lights.add("strip");
        lights.add("lamp");
        lights.add("door");
        lights.add("bathroom");

        system.process(Parcel.SET_PARCEL("","allLights","off"));

        for(String light:lights) {
            system.process(HueParcel.SET_LIGHT_ON_PARCEL(light));
            system.process(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535,254,254));
        }
        system.process(HueParcel.SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL());
        step();
    }

    void step() throws SystemException {
        Parcel p = HueParcel.SET_LIGHT_HSV_PARCEL(lights.get((int) (Math.random() * lights.size())), (int) (Math.random()*65535), -1,-1);
        p = HueParcel.ADD_TRANS_TIME(p, 0);
        updateInterval = (long) (Math.random() * (maxFlashTime-minFlashTime) + minFlashTime);
        system.process(p);
    }
}

