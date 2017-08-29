
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private    PHAccessPoint accessPoint;
    private static Parcel LIGHT_DEAFULT_STATE(){
        Parcel p = new Parcel();
        p.put("hueUsername", new StateValue("iixA66asLRYI-jOBsmrwjIhpu7VYkTl1R1CitgZa", StateValue.READ_PRIVLAGE));
        p.put("hueIP", new StateValue(HUE_IP, StateValue.READ_PRIVLAGE));

        Parcel m = new Parcel();
        m.put("00:17:88:01:10:56:a4:5a-0b", "bathroom");
        m.put("00:17:88:01:10:56:a4:0d-0b", "lamp");
        m.put("00:17:88:01:10:56:a4:2c-0b", "lamp");
        m.put("00:17:88:01:01:21:6b:1c-0b", "strip");
        m.put("00:17:88:01:00:f7:1a:02-0b", "fanColor");
        m.put("00:17:88:01:10:2d:97:e3-0b", "closet");
        m.put("00:17:88:01:10:2f:88:27-0b", "fanWhite2");
        p.put("id2Name", new StateValue(m, StateValue.READ_PRIVLAGE));

        p.put("name2Light", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put("mode", new StateValue("", StateValue.READ_WRITE_PRIVLAGE));
        p.put("liveMode", new StateValue(false, StateValue.READ_WRITE_PRIVLAGE));
        p.put("name2Scene", new StateValue(new Parcel(), StateValue.READ_WRITE_PRIVLAGE));
        p.put("sendLatency", new StateValue(40, StateValue.READ_WRITE_PRIVLAGE));
        return p;
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
        state = LIGHT_DEAFULT_STATE();
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
                allLights = cache.getAllLights();
                //Populate
                populateName2Light();
                populateName2Scene();
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
        try {
            accessPoint.setIpAddress(state.getString("hueIP"));
            accessPoint.setUsername(state.getString("hueUsername"));
        } catch (SystemException e1) {
            e1.printStackTrace();
        }

        phHueSDK.getNotificationManager().registerSDKListener(sdkListener);
        phHueSDK.connect(accessPoint);
        phHueSDK.setAppName("Home Control");
        phHueSDK.setDeviceName("server");
    }


    /*
    Loop thogh all lights in the ID2Name list and match them to a PLight object
     */
    private void populateName2Light(){
        try {
            for(String id : state.getParcel("id2Name").keySet()){
                for(PHLight light : allLights){
                    try {
                        state.getParcel("name2Light").put(state.getParcel("id2Name").getString(light.getUniqueId()), light);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }


    /*
    Makes dealing with hue scens alot easier because I can just call them by their
    systemIdentifier and get their sceneID
     */
    private void populateName2Scene(){
        for(PHScene scene : bridge.getResourceCache().getAllScenes()){
            try {
                state.getParcel("name2Scene").put(scene.getName(),scene.getSceneIdentifier());
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
    }



    public Parcel process(Parcel p) {
        try {
            switch (p.getString("op")){
                case "get":
                    switch (p.getString("what")) {
                        case "state":
                            return Parcel.RESPONSE_PARCEL(state);
                        default:
                            if(state.contains(p.getString("what"))) {
                                StateValue sp = state.getStateParcel(p.getString("what"));
                                if (sp.canRead()) {
                                    return Parcel.RESPONSE_PARCEL(sp.getValue());
                                }
                                throw SystemException.ACCESS_DENIED(p);
                            }
                            throw SystemException.WHAT_NOT_SUPPORTED(p);
                    }
                case "set":
                    switch (p.getString("what")) {
                        case "mode":
                            setMode(p);
                            break;
                        case "allLights":
                            setAllLights(p);
                            break;
                        case "colorLights":
                            setAllColorLights(p);
                            break;
                        case "light":
                            setLight(p);
                            break;
                        default:
                            StateValue sp = state.getStateParcel(p.getString("what"));
                            if (sp.canWrite()) {
                                sp.update(p.get("to"));
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                    }
                    return Parcel.RESPONSE_PARCEL("setSucess");
                default:
                    throw SystemException.OP_NOT_SUPPORTED(p);
            }
        } catch (SystemException e) {
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        }

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
                currentMotionScene = new HueShiftScene(this);
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


    /*
    Set a PHlight using strings
    @TODO dont crash on invalid input
     */
    private void setLight(PHLight light, Parcel p) throws SystemException {
        PHLightState newState = new PHLightState();
        switch (p.getString("to")) {
            case "state":
                if(p.containsKey("H"))
                    newState.setHue(p.getInteger("H"));
                if(p.containsKey("S"))
                    newState.setSaturation(p.getInteger("S"));
                if(p.containsKey("V"))
                    newState.setBrightness(p.getInteger("V"));
                if(p.containsKey("power"))
                    newState.setOn(p.getBoolean("power"));
                if(p.containsKey("transTime"))
                    newState.setHue(p.getInteger("transTime"));
                break;
            case "HSV":
                if(p.containsKey("H"))
                    newState.setHue(p.getInteger("H"));
                if(p.containsKey("S"))
                    newState.setSaturation(p.getInteger("S"));
                if(p.containsKey("V"))
                    newState.setBrightness(p.getInteger("V"));
                break;

            case "off":
                newState.setOn(false);
                break;

            case "on":
                newState.setOn(true);
                break;

            case "RGB":
                float xy[] = PHUtilities.calculateXYFromRGB(p.getInteger("R"),p.getInteger("G"),p.getInteger("B"), light.getModelNumber());
                newState.setX(xy[0]);
                newState.setY(xy[1]);
            break;
            default:
                throw SystemException.TO_NOT_SUPPORTED(p);
        }
        lightCommands.add(HueParcel.LIGHT_UPDATE(light,newState));
    }

    private void setLight(Parcel p) throws SystemException {
        setLight((PHLight) state.getParcel("name2Light").get(p.getString("light")), p);
    }

    /*
    Sets all the colored light to something
     */
    private void setAllColorLights(Parcel p) throws SystemException {
        for(PHLight light : allLights){
            if(light.getLightType().equals(PHLight.PHLightType.COLOR_LIGHT))
            {
                setLight(light, p);
            }
        }
    }

    /*
    Set all the lights to something
    @Todo use groups to have it all change at once
    @todo dont break on improper input
    @todo return int error
     */
    private void setAllLights(Parcel p) throws SystemException {
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
    @Override
    public void update()
    {
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
                    if (state.getInteger("sendLatency") + lastSendTime < System.currentTimeMillis()) {
                        if (lightCommands.size() > 0) {
                            Parcel p = lightCommands.remove(0);
                            if (connected) {
                                switch (p.getString("type")) {
                                    case "allLightUpdate":
                                        bridge.setLightStateForDefaultGroup((PHLightState) p.get("lightState"));
                                        break;
                                    case "sceneUpdate":
                                        bridge.activateScene(p.getString("sceneID"), "0", sceneListener);
                                        break;
                                    case "lightUpdate":
                                        bridge.updateLightState((PHLight) p.get("light"), (PHLightState) p.get("lightState"), lightLister);
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

        for(PHScene s: system.cache.getAllScenes()){
          //  System.out.println(s.getName());
        }


        while(true) {
            try {
                system.update();
                Thread.sleep(100);
                for(PHLight light:system.allLights){
                   // System.out.print(light.getLastKnownLightState());
                }
              // System.out.println();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //system.setAllLightsRGB(69,47,42);
        //system.allOn();

    }




}