
package home.system.hue;

import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;
import home.parcel.Parcel;

import static home.controller.PS.HuePS.*;

/**
 * Created by Willi on 12/27/2016.
 */
public class HueParcel{

    /**
    *
    * Power
    *
    */

    public static Parcel NAME_TO_LIGHT_PARCEL(){
        Parcel p = Parcel.GET_PARCEL("lights","name2Light");
        return p;
    }
    public static Parcel SET_LIGHT_ON_PARCEL(String light){
        Parcel p = Parcel.SET_PARCEL("lights","light", "on");
        p.put("light", light);
        return p;
    }

    public static Parcel SET_ALL_LIGHTS_ON_PARCEL(){
        Parcel p = Parcel.SET_PARCEL("lights","allLights", "on");
        return p;
    }


    public static Parcel SET_ALL_LIGHTS_OFF_PARCEL(){
        Parcel p = Parcel.SET_PARCEL("lights","allLights", "off");
        return p;
    }

    /**
     * HSV
     */

    public static Parcel SET_LIGHT_HSV_PARCEL(String light, int H, int S, int V){
        Parcel p = Parcel.SET_PARCEL("lights","light", "HSV");
        p.put("light",light);
        if(H != -1)
            p.put("H", H);
        if(S != -1)
            p.put("S", S);
        if(V != -1)
            p.put("V", V);
        return p;
    }

    public static Parcel SET_ALL_LIGHTS_HSV_PARCEL( int H, int S, int V){
        Parcel p = Parcel.SET_PARCEL("lights","allLights", "HSV");
        if(H != -1)
            p.put("H", H);
        if(S != -1)
            p.put("S", S);
        if(V != -1)
            p.put("V", V);
        return p;
    }


    /**
     *
     * Trans Time
     *
     */

  static Parcel ADD_TRANS_TIME(Parcel p, int transTime){
    p.put("transTime", transTime);
      return p;
  }

    static Parcel SET_ALL_LIGHTS_LONG_TRANSTIME_PARCEL()
    {
        Parcel p = Parcel.SET_PARCEL("lights","allLights", "longTransTime");
        return p;
    }

    static Parcel SET_LIGHT_LONG_TRANSTIME_PARCEL(String light)
    {
        Parcel p = Parcel.SET_PARCEL("lights","light", "longTransTime");
        p.put("light",light);
        return p;
    }

    static Parcel SET_ALL_LIGHTS_NO_TRANSTIME_PARCEL()
    {
        Parcel p = Parcel.SET_PARCEL("lights","allLights", "noTransTime");
        return p;
    }


    static Parcel SET_LIGHT_NO_TRANSTIME_PARCEL(String light)
    {
        Parcel p = Parcel.SET_PARCEL("lights","light", "noTransTime");
        p.put("light",light);
        return p;
    }


    static Parcel LIGHT_UPDATE(PHLight light, PHLightState state){
        Parcel p = new Parcel();
        p.put("type", "lightUpdate");
        p.put("light", light);
        p.put("lightState", state);
        return p;
    }
    static Parcel ALL_LIGHT_UPDATE(PHLightState state){
        Parcel p = new Parcel();
        p.put("type", "allLightUpdate");
        p.put("allLights", true);
        p.put("lightState", state);
        return p;
    }



    static Parcel SCENE_UPDATE(String sceneId){
        Parcel p = new Parcel();
        p.put(TYPE_KEY, SCENE_UPDATE_LIGHT_COMMAND);
        p.put(SCENE_ID_KEY, sceneId);
        return p;
    }

    static Parcel GROUP_UPDATE(String groupID, PHLightState state){
        Parcel p = new Parcel();
        p.put(TYPE_KEY, GROUP_UPDATE_LIGHT_COMMAND);
        p.put(GROUP_KEY, groupID);
        p.put(PH_LIGHT_STATE_KEY, state);
        return p;
    }

    static Parcel GENERATE_STATE(boolean power){
        Parcel p = new Parcel();
        p.put(LIGHT_STATE_POWER_KEY, false);
        return p;
    }




}