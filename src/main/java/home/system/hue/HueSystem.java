
package home.system.hue;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.sdk.*;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.*;
import home.controller.Engine;
import home.controller.Logger;
import home.parcel.Parcel;
import home.parcel.StateValue;
import home.parcel.SystemException;
import home.system.SystemParent;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.philips.lighting.model.PHLight.PHLightColorMode.COLORMODE_CT;
import static home.controller.PS.HuePS.*;
import static home.controller.PS.GenericPS.*;

/**
 * Created by Willi on 9/26/2016.
 * USes the HUE SDK to talk to the hue system
 * @Todo alot, mostly check, handle not found stuffs
 */

public class HueSystem extends SystemParent{
    private PHHueSDK phHueSDK;
    public static final String systemIdentifier = "hue";

    private static final String HUE_IP = "192.168.1.11";
    private List<PHLight> allLights;
    private PHBridge bridge;
    private PHSDKListener sdkListener;
    private PHLightListener lightLister;
    private PHBridgeResourcesCache cache;
    private HueMotionScene currentMotionScene;
    private ArrayList<Parcel> lightCommands;
    private long lastSendTime;
    private Parcel state;
    private boolean connected = false;
    private PHAccessPoint accessPoint;

    //Secrete Strings
    //Light Update Commands

    private static Parcel LIGHT_DEFAULT_STATE(){
        Parcel p = new Parcel();
        p.put(ID_2_LIGHT_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(LIGHT_2_ID_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(SCENE_2_ID_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(ID_2_SCENE_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(GROUP_2_ID_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(ID_2_GROUP_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(LIGHT_SCENE_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(SEND_PERIOD_KEY, new StateValue(40, StateValue.READ_WRITE_PRIVLAGE));

        return p;
    }

    /**
     * Builder for a scene assioated with an single light id
     * @param name
     * @param motionScene
     * @param initialValue
     * @return
     */
    private static Parcel LIGHT_SCENE_STATE_BUILDER(String name, boolean motionScene, Parcel initialValue){
        Parcel p = new Parcel();
        p.put(MOTION_LIGHT_SCENE_KEY, motionScene);
        p.put(LIGHT_SCENE_NAME_KEY, name);
        p.put(INIT_LIGHT_SCENE_KEY, initialValue);
        return p;
    }

    private static Parcel SCENE_BUILDER(){
        Parcel p = new Parcel();
        p.put(ACTIVE_LIGHT_SCENE_KEY, UNKNOWN_LIGHT_SCENE);
        p.put(LIGHT_SCENES_KEY, new Parcel());
        return p;
    }

    private static Parcel phLightStateToParcel(PHLightState phLightState){
        Parcel p = new Parcel();
        p.put(LIGHT_STATE_POWER_KEY, phLightState.isOn());
        p.put(LIGHT_STATE_COLOR_MODE, phLightState.getColorMode().toString());

        switch (phLightState.getColorMode()){
            case COLORMODE_CT:
                p.put(LIGHT_STATE_COLOR_TEMP, phLightState.getCt());
                break;
            case COLORMODE_HUE_SATURATION:
                p.put(LIGHT_STATE_HUE_KEY, phLightState.getHue());
                p.put(LIGHT_STATE_SATURATION_KEY, phLightState.getSaturation());
                p.put(LIGHT_STATE_VALUE_KEY, phLightState.getBrightness());
                break;
            case COLORMODE_XY:
                p.put(LIGHT_STATE_X_KEY, phLightState.getX());
                p.put(LIGHT_STATE_Y_KEY, phLightState.getY());
                break;
            case COLORMODE_UNKNOWN:
            case COLORMODE_NONE:
                p.put(LIGHT_STATE_COLOR_TEMP, phLightState.getCt());
                p.put(LIGHT_STATE_HUE_KEY, phLightState.getHue());
                p.put(LIGHT_STATE_SATURATION_KEY, phLightState.getSaturation());
                p.put(LIGHT_STATE_VALUE_KEY, phLightState.getBrightness());
                p.put(LIGHT_STATE_X_KEY, phLightState.getX());
                p.put(LIGHT_STATE_Y_KEY, phLightState.getY());
        }
        return p;
    }


    /*
    Set a PHlight using strings
    @TODO dont crash on invalid input
    */
    private static PHLightState phLightStateFromParcel(Parcel p) throws SystemException {
        PHLightState newState = new PHLightState();
        if(p.containsKey(LIGHT_STATE_HUE_KEY))
            newState.setHue(p.getInteger(LIGHT_STATE_HUE_KEY));
        if(p.containsKey(LIGHT_STATE_SATURATION_KEY))
            newState.setSaturation(p.getInteger(LIGHT_STATE_SATURATION_KEY));
        if(p.containsKey(LIGHT_STATE_VALUE_KEY))
            newState.setBrightness(p.getInteger(LIGHT_STATE_VALUE_KEY));
        if(p.containsKey(LIGHT_STATE_POWER_KEY))
            newState.setOn(p.getBoolean(LIGHT_STATE_POWER_KEY));
        if(p.containsKey(LIGHT_STATE_TRANS_TIME))
            newState.setHue(p.getInteger(LIGHT_STATE_TRANS_TIME));
        if(p.contains(LIGHT_STATE_X_KEY))
            newState.setX(p.getDouble(LIGHT_STATE_X_KEY).floatValue());
        if(p.contains(LIGHT_STATE_Y_KEY))
            newState.setY(p.getDouble(LIGHT_STATE_Y_KEY).floatValue());
        if(p.contains(LIGHT_STATE_COLOR_TEMP))
            newState.setCt(p.getInteger(LIGHT_STATE_COLOR_TEMP));
        if(p.contains(LIGHT_STATE_COLOR_MODE))
            newState.setColorMode(PHLight.PHLightColorMode.valueOf(p.getString(LIGHT_STATE_COLOR_MODE)));
        return newState;
    }


    private PHSceneListener sceneListener = new PHSceneListener() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError(int i, String s) {
        }

        @Override
        public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

        }

        @Override
        public void onScenesReceived(List<PHScene> list) {

        }

        @Override
        public void onSceneReceived(PHScene phScene) {

        }
    };

    /*
    Hue System must update once every 10 ms for motion animations
     */
    public HueSystem( Engine e)
    {
        super(systemIdentifier, e, 10);
        phHueSDK = PHHueSDK.getInstance();
        state = LIGHT_DEFAULT_STATE();
        lightCommands = new ArrayList<>();
        SystemParent system = this;
        sdkListener = new PHSDKListener() {
            @Override
            public void onAccessPointsFound(List accessPoint) {
                // Handle your bridge search results here.  Typically if multiple results are returned you will want to display them in a list
                // and let the user select their bridge.   If one is found you may opt to connect automatically to that bridge.

            }

            @Override
            public void onCacheUpdated(List cacheNotificationsList, PHBridge bridge) {
                // Here you receive notifications that the BridgeResource Cache was updated. Use the PHMessageType to
                // check which cache was updated, e.g.
                if (cacheNotificationsList.contains(PHMessageType.LIGHTS_CACHE_UPDATED)) {
                 //   System.out.println("Lights Cache Updated ");
                    //processLightChange();

                }
            }

            @Override
            public void onBridgeConnected(PHBridge b, String username) {
                connected = true;
                Logger.log(system, "Bridge Connected: " + username, Logger.LOG_LEVEL_INFO);
                phHueSDK.setSelectedBridge(b);
                PHHeartbeatManager heartbeatManager = PHHeartbeatManager.getInstance();

                heartbeatManager.enableLightsHeartbeat(b, 500);
                bridge = b;
                cache = b.getResourceCache();
                try {
                    populateFromCache();
                } catch (SystemException e1) {
                    e1.printStackTrace();
                }
                // Here it is recommended to set your connected bridge in your sdk object (as above) and start the heartbeat.
                // At this point you are connected to a bridge so you should pass control to your main program/activity.
                // The username is generated randomly by the bridge.
                // Also it is recommended you store the connected IP Address/ Username in your app here.  This will allow easy automatic connection on subsequent use.
            }

            @Override
            public void onAuthenticationRequired(PHAccessPoint accessPoint) {
                Logger.log(system, "Auth Required", Logger.LOG_LEVEL_INFO);
                phHueSDK.startPushlinkAuthentication(accessPoint);
                // Arriving here indicates that Pushlinking is required (to prove the User has physical access to the bridge).  Typically here
                // you will display a pushlink image (with a timer) indicating to to the user they need to push the button on their bridge within 30 seconds.
            }

            @Override
            public void onConnectionResumed(PHBridge bridge) {
                connected = true;
            }

            @Override
            public void onConnectionLost(PHAccessPoint accessPoint) {
                // Here you would handle the loss of connection to your bridge.
                Logger.log(system, "Bridge Connection lost", Logger.LOG_LEVEL_INFO);
                connected = false;
            }

            @Override
            public void onError(int code, final String message) {
                // Here you can handle events such as Bridge Not Responding, Authentication Failed and Bridge Not Found
                Logger.log(system, "Bridge Error " + code + ": " + message, Logger.LOG_LEVEL_ERROR);
                System.out.println(message);
            }

            @Override
            public void onParsingErrors(List parsingErrorsList) {
                // Any JSON parsing errors are returned here.  Typically your program should never return these.
            }


        };
        lightLister = new PHLightListener() {
            @Override
            public void onReceivingLightDetails(PHLight phLight) {

            }

            @Override
            public void onReceivingLights(List<PHBridgeResource> list) {

            }

            @Override
            public void onSearchComplete() {

            }

            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onStateUpdate(Map<String, String> map, List<PHHueError> list) {

            }
        };

        accessPoint = new PHAccessPoint();
        accessPoint.setIpAddress(HUE_IP);
        accessPoint.setUsername(HUE_USERNAME);

        phHueSDK.getNotificationManager().registerSDKListener(sdkListener);
        phHueSDK.connect(accessPoint);
        phHueSDK.setAppName("Home Control");
        phHueSDK.setDeviceName("server");
    }

    /*
    Populate the State Parcel using the cache
     */
    private void populateFromCache() throws SystemException {
        for (PHLight light : cache.getAllLights()) {
            state.getParcel(LIGHT_2_ID_KEY).put(light.getName(), light.getIdentifier());
            state.getParcel(ID_2_LIGHT_KEY).put(light.getIdentifier(), light.getName());
            state.getParcel(LIGHT_SCENE_KEY).put(light.getIdentifier(), SCENE_BUILDER());
        }
        for(PHGroup group : cache.getAllGroups()){
            state.getParcel(GROUP_2_ID_KEY).put(group.getName(), group.getIdentifier());
            state.getParcel(ID_2_GROUP_KEY).put(group.getIdentifier(), group.getName());
        }

        for(PHScene scene : bridge.getResourceCache().getAllScenes()){
            state.getParcel(SCENE_2_ID_KEY).put(scene.getName(),scene.getSceneIdentifier());
            state.getParcel(ID_2_SCENE_KEY).put(scene.getName(),scene.getSceneIdentifier());
            for(String lightId: scene.getLightIdentifiers()){
                Map<String, PHLightState> lightState = scene.getLightStates();
                Parcel sceneLightState = new Parcel();
                if(lightState != null){
                    sceneLightState = phLightStateToParcel(lightState.get(lightId));
                }
                else{
                    sceneLightState.put(LIGHT_STATE_POWER_KEY, false);
                }

                state.getParcel(LIGHT_SCENE_KEY).getParcel(lightId).getParcel(LIGHT_SCENES_KEY).put(scene.getSceneIdentifier(), LIGHT_SCENE_STATE_BUILDER(scene.getName(), false, sceneLightState));
            }

        }
        System.out.println(state);
    }

    /**
     * Process method of the System
     * @param p: the Parcel to be operated on
     * @return Response Parcel
     * @throws SystemException
     */
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString(OP_KEY)){
            case GET_OP_KEY:
                switch (p.getString(WHAT_KEY)) {
                    case STATE_KEY:
                        return Parcel.RESPONSE_PARCEL(state);
                    default:
                        if(state.contains(p.getString(WHAT_KEY))) {
                            StateValue sp = state.getStateParcel(p.getString(WHAT_KEY));
                            if (sp.canRead()) {
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                        }
                        throw SystemException.WHAT_NOT_SUPPORTED(p);
                }
            case SET_OP_KEY:
                switch (p.getString(WHAT_KEY)) {
                    case ALL_LIGHTS_KEY:
                        return setAllLights(p);
                    case GROUP_KEY:
                        return setGroup(p);
                    case LIGHT_KEY:
                        return setLight(p);
                }
                throw SystemException.WHAT_NOT_SUPPORTED(p);
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }

    }

    /**
     * Set the room, extension of the process method
     * Adds the correct parcel onto the light commands
     * Must have
     *  room: what room
     *  to: what mode
     *
     * @param p Parcel sent to the process method
     * @return Response Parcel
     * @throws SystemException
     */
    private Parcel setGroup(Parcel p) throws SystemException {
        String group = p.getString(GROUP_KEY);

        String mode = p.getString(TO_KEY);
        switch (mode){
            case MODE_OFF:
                lightCommands.add(HueParcel.GROUP_UPDATE(state.getParcel(GROUP_2_ID_KEY).getString(group),
                        phLightStateFromParcel(HueParcel.GENERATE_STATE(false))));
                break;
            case MODE_ON:
                lightCommands.add(HueParcel.GROUP_UPDATE(state.getParcel(GROUP_2_ID_KEY).getString(group),
                        phLightStateFromParcel(HueParcel.GENERATE_STATE(false))));
                break;
            case MODE_CUSTOM:
                lightCommands.add(HueParcel.GROUP_UPDATE(state.getParcel(GROUP_2_ID_KEY).getString(group),
                        phLightStateFromParcel(p)));
                break;
            default:
                String sceneId = group.toLowerCase() + "-" + mode.toLowerCase();
                if(state.getParcel(SCENE_2_ID_KEY).contains(sceneId)){
                    lightCommands.add(HueParcel.SCENE_UPDATE(state.getParcel(SCENE_2_ID_KEY).getString(sceneId)));
                }else{
                    throw SystemException.TO_NOT_SUPPORTED(p);
                }
        }
        return Parcel.RESPONSE_PARCEL("group set");
    }

    private Parcel setAllLights(Parcel p) throws SystemException{
        lightCommands.add(HueParcel.ALL_LIGHT_UPDATE(phLightStateFromParcel(p)));
        return null;
    }



    /*
    Useing the String "to" do the required actions to set the program in that mode
     */
    private void setMode(Parcel p) throws SystemException {
        switch (p.getString("to")){
            case "Off":
                allOff();
                currentMotionScene = null;
                state.getStateParcel("liveMode").update(false);
                state.getStateParcel("mode").update(p.getString("to"));
                break;

            case "Bright":
            case "Dim":
            case "Standard":
                currentMotionScene = null;
                allOn();
                state.getStateParcel("liveMode").update(false);
                lightCommands.add(HueParcel.SCENE_UPDATE(state.getParcel("name2Scene").getString(p.getString("to"))));
                state.getStateParcel("mode").update(p.getString("to"));
                break;

            case "Rainbow":
                state.getStateParcel("liveMode").update(true);
                state.getStateParcel("mode").update("rainbow");
                currentMotionScene = new RainbowScene(this);
                break;

            case "HueShift":
                state.getStateParcel("liveMode").update(true);
                state.getStateParcel("mode").update("hueShift");
                //currentMotionScene = new HueShiftScene(this);
                break;
            case "RandomColors":
                state.getStateParcel("liveMode").update(true);
                state.getStateParcel("mode").update("randomColors");
                currentMotionScene = new RandomColors(this);
                break;

            default:
                throw SystemException.TO_NOT_SUPPORTED(p);
        }
    }


    /**
     * Light Commands
     * Set light given a light Id
     * @param light
     * @param p
     * @throws SystemException
     */
    private void setLight(PHLight light, Parcel p) throws SystemException {
        lightCommands.add(HueParcel.LIGHT_UPDATE(light,phLightStateFromParcel(p.getParcel("to"))));
    }


    private Parcel setLight(Parcel p) throws SystemException {
        setLight((PHLight) state.getParcel("name2Light").get(p.getString("light")), p);
        return Parcel.RESPONSE_PARCEL("success");
    }



    /*
    Set all the lights to something
    @Todo use groups to have it all change at once
    @todo dont break on improper input
    @todo return int error
     */
    private void setAllLightss(Parcel p) throws SystemException {
        PHLightState state = new PHLightState();
        switch (p.getString("to")){
            case "on":
                state.setOn(true);
                break;
            case "off":
                state.setOn(false);
                break;
            case "HSV":

                if(p.containsKey("H"))
                    state.setHue(p.getInteger("H"));
                if(p.containsKey("S"))
                    state.setSaturation(p.getInteger("S"));
                if(p.containsKey("V"))
                    state.setBrightness(p.getInteger("V"));
                break;
            case "RGB":
                float xy[] = PHUtilities.calculateXYFromRGB(p.getInteger("R"),p.getInteger("G"),p.getInteger("B"), allLights.get(0).getModelNumber());
                state.setX(xy[0]);
                state.setY(xy[1]);
                break;
            case "longTransTime":
                state = new PHLightState();
                state.setTransitionTime(7);
                break;

            case "noTransTime":
                state.setTransitionTime(0);
                break;
        }
        lightCommands.add(HueParcel.ALL_LIGHT_UPDATE(state));
    }

    /*
    Turn allvthe lights off
     */
    private void allOff()
    {
        PHLightState lightState = new PHLightState();
        lightState.setOn(false);
        lightCommands.add(HueParcel.ALL_LIGHT_UPDATE(lightState));
    }

    private void allOn()
    {
        PHLightState lightState = new PHLightState();
        lightState.setOn(true);
        lightCommands.add(HueParcel.ALL_LIGHT_UPDATE(lightState));
    }

    /*
    On update, check if there needs to be anything changed for the current mode
    let the mode handel the acutal change
     */

    private void setAllRoom(){

    }

    @Override
    public void update() throws SystemException {
       // System.out.println("Lights:");
        if (currentMotionScene != null) {
            currentMotionScene.update();
        }
        try {
            if(lightCommands.size() != 0){
                if(!phHueSDK.isAccessPointConnected(accessPoint)){
                    phHueSDK.connect(accessPoint);
                }
                if(phHueSDK.isAccessPointConnected(accessPoint)) {
                    if (state.getInteger(SEND_PERIOD_KEY) + lastSendTime < System.currentTimeMillis()) {
                        if (lightCommands.size() > 0) {
                            Parcel p = lightCommands.remove(0);
                            if (connected) {
                                switch (p.getString(TYPE_KEY)) {
                                    case ALL_LIGHT_UPDATE_COMMAND:
                                        bridge.setLightStateForDefaultGroup((PHLightState) p.get(PH_LIGHT_STATE_KEY));
                                        break;
                                    case SCENE_UPDATE_LIGHT_COMMAND:
                                        bridge.activateScene(p.getString(SCENE_ID_KEY), "0", sceneListener);
                                        break;
                                    case LIGHT_UPDATE_LIGHT_COMMAND   :
                                        bridge.updateLightState((PHLight) p.get("light"), (PHLightState) p.get(PH_LIGHT_STATE_KEY), lightLister);
                                        break;
                                    case GROUP_UPDATE_LIGHT_COMMAND:
                                        bridge.setLightStateForGroup(p.getString(GROUP_KEY), (PHLightState) p.get(PH_LIGHT_STATE_KEY));
                                        break;
                                }
                            }

                            lastSendTime = System.currentTimeMillis();
                        }

                    }
                }else{
                    System.out.println("phHueSDK Disconnected");
                }
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    /*
    private void processLightChange()
    {

        //Process light change

        //UpdateCache
        updateCache();

    }

    private void updateCache()
    {
        for(PHLight light : allLights)
        {

            lastState.put(light.getUniqueId(), light.getLastKnownLightState());
        }
    }
    */




    public static void main(String[] args)
    {
        HueSystem system = new HueSystem(null);
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //system.setAllLightsRGB(255,0,255);

        for(PHGroup s: system.cache.getAllGroups()){
            for(PHLight l : system.cache.getAllLights())
                System.out.println(l.getUniqueId());
            System.out.println(s.getName());
        }


        while(true) {
            try {
                system.update();
                Thread.sleep(100);
               // for(PHLight light:system.allLights){
                   // System.out.print(light.getLastKnownLightState());
               // }
              // System.out.println();

            } catch (InterruptedException | SystemException e) {
                e.printStackTrace();
            }
        }

        //system.setAllLightsRGB(69,47,42);
        //system.allOn();

    }

    public static void testGroups(){

    }





}