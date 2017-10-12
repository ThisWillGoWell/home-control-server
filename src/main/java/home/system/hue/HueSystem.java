
package home.system.hue;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.listener.PHSceneListener;
import com.philips.lighting.hue.sdk.*;
import com.philips.lighting.hue.sdk.heartbeat.PHHeartbeatManager;
import com.philips.lighting.hue.sdk.utilities.PHUtilities;
import com.philips.lighting.model.*;
import home.controller.Engine;
import home.controller.webmanager.Application;
import home.parcel.Parcel;
import home.parcel.ParcelArray;
import home.parcel.StateValue;
import home.parcel.SystemException;
import home.system.SystemParent;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static home.controller.PS.HuePS.*;
import static home.controller.PS.GenericPS.*;

import org.apache.log4j.Logger;



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
    private ConcurrentLinkedQueue<Parcel> lightCommands;
    private long lastSendTime;
    private Parcel state;
    private boolean connected = false;
    private PHAccessPoint accessPoint;
    private HttpClient httpClient;

    private static Logger log = Logger.getLogger(HueSystem.class);
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
        p.put(GROUP_LIGHTS_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(LIGHT_INFO_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put(MOTION_SCENE_LIST_KEY, new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));

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

    private static Parcel motionSceneBuilder(Parcel lights, HueSystem system, Parcel effect) throws SystemException {
        Parcel p = new Parcel();
        Thread t = new Thread( HueMotionScene.HueMotionSceneFactory(lights,system, effect));
        t.start();
        p.put(MOTION_SCENE_TYPE_KEY, effect);
        p.put(MOTION_SCENE_LIGHTS_KEY, lights);
        p.put(MOTION_SCENE_THREAD_KEY, t);
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
                p.put(LIGHT_STATE_BRIGHTNESS_KEY, phLightState.getBrightness());
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
                p.put(LIGHT_STATE_BRIGHTNESS_KEY, phLightState.getBrightness());
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
        if(p.containsKey(LIGHT_STATE_BRIGHTNESS_KEY))
            newState.setBrightness(p.getInteger(LIGHT_STATE_BRIGHTNESS_KEY));
        if(p.containsKey(LIGHT_STATE_POWER_KEY))
            newState.setOn(p.getBoolean(LIGHT_STATE_POWER_KEY));
        if(p.containsKey(LIGHT_STATE_TRANS_TIME))
            newState.setHue(p.getInteger(LIGHT_STATE_TRANS_TIME));
        if(p.contains(LIGHT_STATE_XY_KEY)){
            newState.setX(p.getParcelArray(LIGHT_STATE_XY_KEY).getDouble(0).floatValue());
            newState.setY(p.getParcelArray(LIGHT_STATE_XY_KEY).getDouble(1).floatValue());
        }
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
            System.out.println(map + "  "+ list);
        }

        @Override
        public void onScenesReceived(List<PHScene> list) {
            System.out.println( list);

        }

        @Override
        public void onSceneReceived(PHScene phScene) {
            System.out.println(phScene.toString());
        }
    };

    private Parcel phLightToParcel(PHLight light){
        Parcel p = new Parcel();
        p.put(LIGHT_KEY, light.getIdentifier());
        p.put(LIGHT_INFO_TYPE_KEY, light.getLightType().name());
        p.put(LIGHT_INFO_COLOR_SUPPORT_KEY, light.supportsColor());
        p.put(LIGHT_INFO_CT_SUPPORT_KEY, light.supportsCT());
        p.put(LIGHT_INFO_BRIGHTNESS_SUPPORT_KEY, light.supportsBrightness());
        return p;
    }

    /*
    Hue System must update once every 10 ms for motion animations
     */
    public HueSystem( Engine e)
    {
        super(systemIdentifier, e, 10);

        phHueSDK = PHHueSDK.getInstance();
        state = LIGHT_DEFAULT_STATE();


        lightCommands = new ConcurrentLinkedQueue<>();
        SystemParent system = this;
         httpClient = HttpClientBuilder.create().build();

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
                //Logger.log(system, "Bridge Connected: " + username, Logger.LOG_LEVEL_INFO);
                phHueSDK.setSelectedBridge(b);
                PHHeartbeatManager heartbeatManager = PHHeartbeatManager.getInstance();

                heartbeatManager.enableLightsHeartbeat(b, 500);
                bridge = b;
                cache = b.getResourceCache();
                try {
                    populateFromCache();
                    process(Parcel.PROCESS_JSONSTR("{ \"op\":\"set\", \"system\" : \"hue\", \"what\" : \"group\",\"group\":\"Bedroom\", \"to\" : \"motion\", \"motionSceneType\":\"rainbow\", \"cycleTime\": 5000, \"updateInterval\": 500}"));
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
                //Logger.log(system, "Auth Required", Logger.LOG_LEVEL_INFO);
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
                //Logger.log(system, "Bridge Connection lost", Logger.LOG_LEVEL_INFO);
                connected = false;
            }

            @Override
            public void onError(int code, final String message) {
                // Here you can handle events such as Bridge Not Responding, Authentication Failed and Bridge Not Found
                //Logger.log(system, "Bridge Error " + code + ": " + message, Logger.LOG_LEVEL_ERROR);
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
            state.getParcel(LIGHT_INFO_KEY).put(light.getIdentifier(), phLightToParcel(light));
        }
        for(PHGroup group : cache.getAllGroups()){
            state.getParcel(GROUP_2_ID_KEY).put(group.getName(), group.getIdentifier());
            state.getParcel(ID_2_GROUP_KEY).put(group.getIdentifier(), group.getName());
            ParcelArray lights = new ParcelArray();
            lights.addAll(group.getLightIdentifiers());
            state.getParcel(GROUP_LIGHTS_KEY).put(group.getIdentifier(), lights);

        }

        for(PHScene scene : bridge.getResourceCache().getAllScenes()) {
            state.getParcel(SCENE_2_ID_KEY).put(scene.getName(), scene.getSceneIdentifier());
            state.getParcel(ID_2_SCENE_KEY).put(scene.getName(), scene.getSceneIdentifier());
        }
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
     * Kill any motion that effects a single light
     * @param lightId the light that is being updated
     * @throws SystemException when something goes wrong
     */
    private void killAnyMotionForLight(String lightId) throws SystemException {
        for(String motionScene: state.getParcel(MOTION_SCENE_LIST_KEY).keySet()){
            if(state.getParcel(MOTION_SCENE_LIST_KEY).getParcel(MOTION_SCENE_LIGHTS_KEY).contains(lightId)){
                state.getParcel(MOTION_SCENE_LIST_KEY).getParcel(motionScene).getThread(MOTION_SCENE_THREAD_KEY).interrupt();
                state.getParcel(MOTION_SCENE_LIST_KEY).remove(motionScene);
                return;
            }
        }
    }

    /**
     * Kill any motion scene that effects any lights in a group that was just updated
     * @param groupID the group id that is being updated
     * @throws SystemException if something is wrong
     */
    private void killAnyMotionForGroup(String groupID) throws SystemException {
        for(String motionScene: state.getParcel(MOTION_SCENE_LIST_KEY).keySet()){
            for(String light : state.getParcel(MOTION_SCENE_LIST_KEY).getParcel(motionScene).getParcel(MOTION_SCENE_LIGHTS_KEY).keySet())
                if(state.getParcel(GROUP_LIGHTS_KEY).getParcelArray(groupID).contains(light)){
                    state.getParcel(MOTION_SCENE_LIST_KEY).getParcel(motionScene).getThread(MOTION_SCENE_THREAD_KEY).interrupt();
                    state.getParcel(MOTION_SCENE_LIST_KEY).remove(motionScene);
                    return;
                }
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

        String groupID = state.getParcel(GROUP_2_ID_KEY).getString(group);
        killAnyMotionForGroup(groupID);

        switch (mode){
            case MODE_OFF:
                lightCommands.add(HueParcel.GROUP_UPDATE(groupID, HueParcel.GENERATE_STATE(false)));
                break;
            case MODE_ON:
                lightCommands.add(HueParcel.GROUP_UPDATE(groupID, HueParcel.GENERATE_STATE(false)));
                break;
            case MODE_CUSTOM:
                lightCommands.add(HueParcel.GROUP_UPDATE(groupID, p));
                break;
            case MODE_MOTION:
                Parcel lights = new Parcel();
                for(Object lightID : state.getParcel(GROUP_LIGHTS_KEY).getParcelArray(groupID)){
                    lights.put(lightID +"" , state.getParcel(LIGHT_INFO_KEY).getParcel(lightID + ""));
                }
                state.getParcel(MOTION_SCENE_LIST_KEY).put(p.getString(MOTION_SCENE_TYPE_KEY)+ "-" + group, motionSceneBuilder(lights, this, p ));
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

    /**
     * Light Commands
     * Set light given a light Id
     * @param p
     * @throws SystemException
     */
    private Parcel setLight( Parcel p) throws SystemException {
        p.put(PH_LIGHT_STATE_KEY, p.getParcel("to"));
        lightCommands.add(p);
        return Parcel.RESPONSE_PARCEL("Added State to light commands");
    }

    @Override
    public void update() throws SystemException {
       // System.out.println("Lights:");
        try {
            if(lightCommands.size() != 0){
                if(!phHueSDK.isAccessPointConnected(accessPoint)){
                    phHueSDK.connect(accessPoint);
                }
                if(phHueSDK.isAccessPointConnected(accessPoint)) {
                    if (state.getInteger(SEND_PERIOD_KEY) + lastSendTime < System.currentTimeMillis()) {
                        if (lightCommands.size() > 0) {
                            Parcel p = lightCommands.remove();
                            if (connected) {
                                switch (p.getString(TYPE_KEY)) {
                                    case ALL_LIGHT_UPDATE_COMMAND:
                                        bridge.setLightStateForDefaultGroup(phLightStateFromParcel(p.getParcel(PH_LIGHT_STATE_KEY)));
                                        break;
                                    case SCENE_UPDATE_LIGHT_COMMAND:
                                        bridge.activateScene(p.getString(SCENE_ID_KEY), "0", null);
                                        break;
                                    case LIGHT_UPDATE_LIGHT_COMMAND:
                                        bridge.updateLightState(p.getString(LIGHT_KEY), phLightStateFromParcel(p.getParcel(PH_LIGHT_STATE_KEY)), null);
                                        break;
                                    case GROUP_UPDATE_LIGHT_COMMAND:
                                        bridge.setLightStateForGroup(p.getString(GROUP_KEY), phLightStateFromParcel(p.getParcel(PH_LIGHT_STATE_KEY)));
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

    private Parcel getLightStatesPostRequest(String sceneId) throws IOException, SystemException {
        String url = String.format("http://%s/api/%s/scenes/%s/", HUE_IP, HUE_USERNAME, sceneId);
        HttpGet request = null;
        request = new HttpGet(url);
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = httpClient.execute(request, responseHandler);
        return Parcel.PROCESS_JSONSTR(response).getParcel("lightstates");

    }

    void addLightCommand(Parcel p) {
        this.lightCommands.add(p);
    }



    public static void main(String[] args)
    {
        Application.disableHTTPLogging();
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
    }






}