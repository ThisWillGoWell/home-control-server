package home.system.hue;

import static home.controller.PS.HuePS.*;
import home.parcel.Parcel;
import home.parcel.SystemException;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * Created by Willi on 11/21/2016.
 * Class to manage Hue motion Scenes
 *
 */


abstract class HueMotionScene implements Runnable {
    long startTime;
    long updateInterval;
    protected long lastUpdateTime;
    protected HueSystem system;
    protected Parcel lights;
    protected List<String> activeLights;

    static HueMotionScene HueMotionSceneFactory(Parcel lights, HueSystem system,  Parcel effect) throws SystemException {
        switch (effect.getString(MOTION_SCENE_TYPE_KEY)) {
            case MOTION_SCENE_RAINBOW:
                return new RainbowScene(lights, system, effect);
            case MOTION_SCENE_HUE_SHIFT:
                return new HueShiftScene(lights, system, effect);
            case MOTION_SCENE_RANDOM_LIGHTS:
                return new RandomColors(lights, system, effect);
            case MOTION_SCENE_AMBIANCE_SHIFT:
                return new AmbianceMotionScene(lights, system, effect);
            case MOTION_SCENE_FALMES:
                return new FlamesMotionScene(lights, system, effect);

            default:
                throw SystemException.GENERIC_EXCEPTION("Motion Scene Not Found To Be Made", effect);
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
        this.lights = lights;
        activeLights = new ArrayList<String>();
    }

    void step() throws SystemException {

    }

    @Override
    public void run() {
        while (true) {
            try {
                lastUpdateTime = System.currentTimeMillis();
                step();
                Thread.sleep(updateInterval);
            } catch (InterruptedException | SystemException e) {
                e.printStackTrace();
            }
        }
    }

    private static class RainbowScene extends HueMotionScene {
        private Long cycleTime;
        RainbowScene(Parcel lights, HueSystem system,  Parcel effect) throws SystemException {
            super(lights, system, effect);
            startTime = System.currentTimeMillis();
            cycleTime = effect.getLong(MS_EFFECT_CYCLE_TIME);

            for(String lightID : lights.keySet()){
                if(lights.getParcel(lightID).getBoolean(LIGHT_INFO_COLOR_SUPPORT_KEY)){
                    this.activeLights.add(lightID);
                    system.addLightCommand(HueParcel.LIGHT_UPDATE(lightID, HueParcel.GENERATE_STATE(true)));
                    system.addLightCommand(HueParcel.LIGHT_UPDATE(lightID, HueParcel.GENERATE_STATE(-1, 255, 255)));
                }else{
                    system.addLightCommand(HueParcel.LIGHT_UPDATE(lightID, HueParcel.GENERATE_STATE(false)));
                }
            }
        }

        void step() throws SystemException {
            //Determine
            int count = 0;
            long startHue = (System.currentTimeMillis() % cycleTime) * 65535 / cycleTime;
            for (String light : activeLights) {
                Parcel p = HueParcel.LIGHT_UPDATE(light, HueParcel.GENERATE_STATE((int) ((startHue + (65535 / activeLights.size() * count)) % 65535), -1, -1));
                system.addLightCommand(p);
                count++;
            }

        }
    }

    private static class HueShiftScene extends HueMotionScene {
        HueShiftScene(Parcel lights, HueSystem system, Parcel effect) throws SystemException {
            super(lights, system, effect);
            //Set default
            for(String lightId : lights.keySet()){
                system.addLightCommand(HueParcel.SET_LIGHT_HSV_PARCEL(lightId, 65535, 254, 254));
            }


            step();
        }

        void step() {
            //Determine
            int count = 0;
           // long startHue = (System.currentTimeMillis() % cycleTime) * 65535 / cycleTime;
          //  Parcel p = HueParcel.SET_ALL_LIGHTS_HSB_PARCEL((int) ((startHue + (65535 / lights.size() * count)) % 65535), -1, -1);
        }
    }


    private static class RandomColors extends HueMotionScene {
        private long minFlashTime = 40;
        private long maxFlashTime = 100;

        RandomColors(Parcel lights,HueSystem system, Parcel effect) throws SystemException {
            super(lights, system, effect);

            for (String light : lights.keySet()) {
//                system.addLightCommand(HueParcel.SET_LIGHT_ON_PARCEL(light));
//                system.addLightCommand(HueParcel.SET_LIGHT_HSV_PARCEL(light, 65535, 254, 254));
//                system.addLightCommand(HueParcel.SET_LIGHT_LONG_TRANSTIME_PARCEL(light));

            }
            step();
        }

        void step() throws SystemException {
//            Parcel p = HueParcel.SET_LIGHT_HSV_PARCEL(lights.get((int) (Math.random() * lights.size())), (int) (Math.random() * 65535), -1, -1);
//            p = HueParcel.ADD_TRANS_TIME(p, 0);
//            updateInterval = (long) (Math.random() * (maxFlashTime - minFlashTime) + minFlashTime);
//            system.addLightCommand(p);
        }
    }
    private static class AmbianceMotionScene extends HueMotionScene {
        AmbianceMotionScene(Parcel lights, HueSystem system, Parcel effect) throws SystemException {
            super(lights, system, effect);
        }

    }

    private static class FlamesMotionScene extends HueMotionScene{
        FlamesMotionScene(Parcel lights, HueSystem system, Parcel effect) throws SystemException {
            super(lights, system, effect);
        }

    }

    private static class SceneColorCycle extends HueMotionScene{


        SceneColorCycle(Parcel lights, HueSystem system, Parcel effect) throws SystemException {
            super(lights, system, effect);
            for(String lightID : lights.keySet()){
                if(lights.getParcel(lightID).getBoolean(LIGHT_INFO_COLOR_SUPPORT_KEY)){
                    this.activeLights.add(lightID);
                    system.addLightCommand(HueParcel.LIGHT_UPDATE(lightID, HueParcel.GENERATE_STATE(true)));
                    system.addLightCommand(HueParcel.LIGHT_UPDATE(lightID, HueParcel.GENERATE_STATE(-1, 255, 255)));
                }else{
                    system.addLightCommand(HueParcel.LIGHT_UPDATE(lightID, HueParcel.GENERATE_STATE(false)));
                }
            }

        }


        private class HueFade{
            private int targetHue;
            private int currentSpeed;
            private int currentHue;
            private double stepHueTrans;
            private boolean takeLongWay;
            private int transamount;
            HueFade(String lightId, long updateInterval){
                this.currentHue = ThreadLocalRandom.current().nextInt(0, 360 + 1);
                this.nextHue();
                takeLongWay = true;

            }

            private void nextHue(){
                this.targetHue = ThreadLocalRandom.current().nextInt(0, 360 + 1);
                this.currentSpeed = ThreadLocalRandom.current().nextInt(10000, 30000);
                int numberOfSteps = (int) (currentSpeed / updateInterval);
                if(Math.abs(targetHue - currentHue) > 180)
                this.transamount = targetHue - currentHue / numberOfSteps;
                if(takeLongWay){

                }
            }

            void step(){
                if(currentHue == 1){

                }
            }

        }
    }
}



