
package home.system.hue;

import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import home.parcel.Parcel;

import static home.controller.PS.HuePS.*;

/**
 * Created by Willi on 12/27/2016.
 */
public class HueParcel {

    /**
     * HSB
     */
    public static Parcel SET_LIGHT_HSV_PARCEL(String light, int H, int S, int B) {
        Parcel p = new Parcel();
        p.put(TYPE_KEY, LIGHT_UPDATE_LIGHT_COMMAND);
        p.put(LIGHT_KEY, light);
        if (H != -1)
            p.put(LIGHT_STATE_HUE_KEY, H);
        if (S != -1)
            p.put(LIGHT_STATE_SATURATION_KEY, S);
        if (B != -1)
            p.put(LIGHT_STATE_BRIGHTNESS_KEY, B);
        return p;
    }


    static Parcel SET_LIGHT_NO_TRANSTIME_PARCEL(String light) {
        Parcel p = Parcel.SET_PARCEL("lights", "light", "noTransTime");
        p.put("light", light);
        return p;
    }


    static Parcel LIGHT_UPDATE(String light, Parcel state) {
        Parcel p = new Parcel();
        p.put(TYPE_KEY, LIGHT_UPDATE_LIGHT_COMMAND);
        p.put(LIGHT_KEY, light);
        p.put(PH_LIGHT_STATE_KEY, state);
        return p;
    }

    static Parcel ALL_LIGHT_UPDATE(Parcel state) {
        Parcel p = new Parcel();
        p.put("type", "allLightUpdate");
        p.put("allLights", true);
        p.put("lightState", state);
        return p;
    }


    static Parcel SCENE_UPDATE(String sceneId) {
        Parcel p = new Parcel();
        p.put(TYPE_KEY, SCENE_UPDATE_LIGHT_COMMAND);
        p.put(SCENE_ID_KEY, sceneId);
        return p;
    }

    static Parcel GROUP_UPDATE(String groupID, Parcel state) {
        Parcel p = new Parcel();
        p.put(TYPE_KEY, GROUP_UPDATE_LIGHT_COMMAND);
        p.put(GROUP_KEY, groupID);
        p.put(PH_LIGHT_STATE_KEY, state);
        return p;
    }

    static Parcel GENERATE_STATE(int H, int S, int B){
        Parcel p = new Parcel();
        if (H != -1)
            p.put(LIGHT_STATE_HUE_KEY, H);
        if (S != -1)
            p.put(LIGHT_STATE_SATURATION_KEY, S);
        if (B != -1)
            p.put(LIGHT_STATE_BRIGHTNESS_KEY, B);
        return p;
    }


    static Parcel GENERATE_STATE(boolean power) {
        Parcel p = new Parcel();
        p.put(LIGHT_STATE_POWER_KEY, power);
        return p;
    }


}