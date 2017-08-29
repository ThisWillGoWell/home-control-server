package home.system.hue;

import com.philips.lighting.model.PHBridgeResourcesCache;
import com.philips.lighting.model.PHLight;
import com.philips.lighting.model.PHLightState;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Willi on 11/18/2016.
 */
public class HueEventListener {

    private String ROOM_POWER = "";
    private String BATHROOM = "";
    private HashMap<String, Command> responses;
    private HueSystem system;
    HueEventListener(HueSystem system){
        responses = new HashMap<String, Command>();
        this.system = system;
        //Add Listeners
        //Lister on FanWhite
        responses.put(ROOM_POWER, new FanWhiteListener());

        //Listener on Bathroom
        responses.put(BATHROOM, new BathroomListener());


    }


    void processLightEvent(HashMap<String, PHLightState> lastState, PHBridgeResourcesCache cache)
    {
        for(PHLight light : cache.getAllLights()){
            if(responses.keySet().contains(light.getUniqueId())){
                if(!light.getLastKnownLightState().equals(lastState.get(light.getUniqueId()))){
                    //The state has changed, s
                    responses.get(light.getUniqueId()).execute(light.getLastKnownLightState(), lastState.get(light.getUniqueId()));

                }
            }
        }

    }

    interface Command{
        void execute(PHLightState currentState, PHLightState lastState);
    }

    class FanWhiteListener implements Command{
        public void execute(PHLightState currentState, PHLightState lastState){
            //1 Check if power was what changed
            if(currentState.isOn() != lastState.isOn()){
                //did it turn on?
               // if(currentState.isReachable())
                    //system.set("allLights", "off", null);
               // else
                   // system.set("allLights", "on", null);
            }
        }
    }

    class BathroomListener implements Command{
        private long DIM_START_NIGHT = 23;
        private int DIM_END_MORNING = 6;

        @Override
        public void execute(PHLightState currentState, PHLightState lastState) {
            if(currentState.isOn() != lastState.isOn()){
                //check the time
                if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > DIM_START_NIGHT || Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < DIM_END_MORNING)
                {
                    HashMap command = new HashMap<String, Object>();
                    //Red Orange Color
                    command.put("H",32);
                    command.put("S",70);
                    command.put("V", 88);
                    //system.set("bathroom", "HSV", command);
                }
            }
        }
    }



}
