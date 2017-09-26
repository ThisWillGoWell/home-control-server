package home.system.hue;

import static home.controller.PS.HuePS.*;
import home.parcel.Parcel;
import home.parcel.ParcelArray;
import home.parcel.SystemException;

import java.util.ArrayList;

/**
 * Created by Willi on 11/21/2016.
 * Class to manage Hue motion Scenes
 *
 */


class HueMotionScene implements Runnable {
    long startTime;
    long updateInterval;
    protected long lastUpdateTime;
    private HueSystem system;
    private Parcel lights;

    static HueMotionScene HueMotionSceneFactory(Parcel lights, HueSystem system,  Parcel effect) {
        switch (effect.getString(MOTION_SCENE_TYPE_KEY)) {
            case MOTION_SCENE_RAINBOW:
                return new RainbowScene(lights, system, effect);
            case MOTION_SCENE_HUE_SHIFT:
                return new HueShiftScene(lights, system, effect);
            case MOTION_SCENE_RANDOM_LIGHTS:
                return new RandomColors(lights, system, effect);
            case MOTION_SCENE_AMBANCE_SHIFT:
                return new
            case MOTION_SCENE_FALMES:
                return new
        }
    }

    /*
    Keep track of all the lights involved with the pattern
     */
    HueMotionScene(Parcel lights, HueSystem system, Parcel effect) throws SystemException {
        this.updateInterval = effect.getLong(MS_EFFECT_UPDATE_INTERVAL);
        this.system = system;
        this.lastUpdateTime = System.currentTimeMillis();
        this.startTime = lastUpdateTime;
        try {
            system.process(Parcel.SET_PARCEL("", "allLights", "off"));
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    long getUpdateTime() {
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

    @Override
    public void run() {
        while (true) {
            lastUpdateTime = System.currentTimeMillis();
            try {
                step();
                Thread.sleep(updateInterval);
            } catch (InterruptedException | SystemException e) {
                e.printStackTrace();
            }
        }
    }

    private class RainbowScene extends HueMotionScene {
        private long cycleTime;

        /**
         * This gets called after the lights have been set to their default value
         *
         * @param system
         * @param lights
         * @throws SystemException
         */
        RainbowScene(HueSystem system, Parcel lights, Parcel effect) throws SystemException {
            super(lights, system, effect);
            startTime = System.currentTimeMillis();
            cycleTime = effect.getLong(MS_EFFECT_CYCLE_TIME);
            //Determine order;

            for (String light : lights) {
                system.process(HueParcel.SET_LIGHT_ON_PARCEL(light));
                system.process(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535, 254, 254));
            }
            system.process(HueParcel.SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL());
            step();
        }

        void step() throws SystemException {
            //Determine
            int count = 0;
            long startHue = (System.currentTimeMillis() % cycleTime) * 65535 / cycleTime;
            for (String light : lights) {
                Parcel p = HueParcel.SET_LIGHT_HSV_PARCEL(light, (int) ((startHue + (65535 / lights.size() * count)) % 65535), -1, -1);
                system.process(p);
                count++;
            }

        }
    }

    private class HueShiftScene extends HueMotionScene {
        private long cycleTime;

        HueShiftScene(Parcel lights, HueSystem system, Parcel effect) {
            super(system, 500);
            startTime = System.currentTimeMillis();
            cycleTime = 10000;
            //Determine order;

            Parcel lights = null;
            lights.add("tv");
            lights.add("strip");
            lights.add("lamp");
            lights.add("door");
            lights.add("bathroom");

            system.process(Parcel.SET_PARCEL("", "allLights", "off"));

            for (String light : lights) {
                system.process(HueParcel.SET_LIGHT_ON_PARCEL(light));
                system.process(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535, 254, 254));
            }
            system.process(HueParcel.SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL());

            step();
        }

        void step() {
            //Determine
            int count = 0;
            long startHue = (System.currentTimeMillis() % cycleTime) * 65535 / cycleTime;
            Parcel p = HueParcel.SET_ALL_LIGHTS_HSV_PARCEL((int) ((startHue + (65535 / lights.size() * count)) % 65535), -1, -1);
            system.process(p);

        }
    }


    private class RandomColors extends HueMotionScene {
        private long minFlashTime = 40;
        private long maxFlashTime = 100;

        RandomColors(HueSystem system) throws SystemException {
            super(system, 500);
            startTime = System.currentTimeMillis();
            lights.add("tv");
            lights.add("strip");
            lights.add("lamp");
            lights.add("door");
            lights.add("bathroom");

            system.process(Parcel.SET_PARCEL("", "allLights", "off"));

            for (String light : lights) {
                system.process(HueParcel.SET_LIGHT_ON_PARCEL(light));
                system.process(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535, 254, 254));
            }
            system.process(HueParcel.SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL());
            step();
        }

        void step() throws SystemException {
            Parcel p = HueParcel.SET_LIGHT_HSV_PARCEL(lights.get((int) (Math.random() * lights.size())), (int) (Math.random() * 65535), -1, -1);
            p = HueParcel.ADD_TRANS_TIME(p, 0);
            updateInterval = (long) (Math.random() * (maxFlashTime - minFlashTime) + minFlashTime);
            system.process(p);
        }
    }
}



