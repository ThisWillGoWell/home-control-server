package home.controller;

public class PS {

    //systemNames
    public static final String
        NETWORK_SYSTEM_NAME     = "network",
        CHROMECAST_SYSTEM_NAME  = "chromecast",
        IR_REMOTE_SYSTEM_NAME   = "irRemote",
        CONDITIONAL_SYSTEM_NAME = "conditional",
        HOME_STATE_SYSTEM_NAME  = "homeState";





    public static final class GenericPS {
        public static final String
            GET_OP_KEY          = "get",
            SET_OP_KEY          = "set",
            WHAT_KEY            = "what",
            OP_KEY              = "op",
            SYSTEM_KEY          = "system",
            STATE_KEY           = "state",
            TO_KEY              = "to",
        //Parent,
            SUB_ALERT_TYPE      = "alert",
            SUB_CHANGE_TYPE     = "change",
            SUBSCRIBER_KEY      = "subscriber",
            HELLO_COMMAND       = "hello",
            DEREGISTER_COMMAND  = "deregister",
            SUB_COMMAND         = "subscribe",
            UPDATE_COMMAND      = "update",
            SUB_TYPE_KEY        = "type",
            SUB_REQUEST_KEY     = "request",
            PAYLOAD_KEY         = "payload";
    }

    public static final class ClockPS{
        public static final String
            CLOCK_DISPLAY_MUTE = "imageMute",
            CLOCK_DISPLAY_BRIGHTNESS = "brightness";
    }


    public static final class NetworkDevices{
        //Network Mac
        public static final String
            DINOLIGHT_MAC                   = "b8:27:eb:c2:f5:53",
            CHROMECAST_AUDIO_BEDROOM_MAC    = "a4:77:33:f1:93:76",
            WILL_PHONE_ANDROID_MAC          = "ac:37:43:4b:aa:e5",
            WILL_PHONE_IPHONE_MAC           = "b8:27:eb:c2:f5:53",
            PHILIPS_HUE_MAC                 = "00:17:88:23:a2:86",
            CHROMECAST_BEDROOM_MAC          = "f4:f5:d8:3a:31:a2",
            CHROMECAST_LIVINGROOM_MAC       = "a4:77:33:03:19:D0",

            //Device Names
            DINOLIGHT                       = "DINOLIGHT",
            CHROMECAST_AUDIO_BEDROOM        = "Chromecast-Audio-Bedroom",
            WILL_PHONE_ANDROID              = "will-phone-android",
            WILL_PHONE_IPHONE               = "will-phone-iphone",
            PHILIPS_HUE                     = "philips-hue",
            CHROMECAST_BEDROOM              = "Bedroom",
            CHROMECAST_LIVINGROOM           = "Chromecast-Livingroom";
    }

    public static final class NetworkSystemStrings{
        public static final String
        //Parcel Keys
        DEVICE_KEY          = "device",
        DEVICES_KEY         = "devices",
        TIMEOUT_KEY         = "timeout",
        CONNECTED_KEY       = "connected",
        IP_KEY              = "ip",
        LAST_PING_TIME_KEY  = "LAST_PING_TIME_KEY",
        MAC_KEY             = "mac";
    }

    public static final class ChromeCastPS {
        public static final String
            APP_2_APP_ID            = "app2appID",
            CHROME_CAST_NAME_MAP    = "chromeCastNameMap",
            CHROME_CASTS_KEY        = "chromeCasts",
            //ChromeCast App Names
            MEDIA_APP_NAME          = "CC1AD845",
            YOUTUBE_APP_NAME        = "233637DE",
            BACKDROP_APP_NAME       = "E8C28D3C",
            //Process commands
            CAST_NAME_KEY           = "castName",
            //ChromeCastSystem Ops/directives
            PLAY_RADIO_COMMAND  = "radio",
            CLOSE_OP            = "close",
            CONTROL_OP          = "control",
            ACTION_KEY          = "action",
            //actions
            PLAY_COMMAND        = "play",
            PAUSE_COMMAND       = "pause",
            VOLUME_COMMAND      = "volume",
            MUTE_COMMAND        = "mute",
            SEEK_COMMAND        = "seek",
            MUTE_VALUE_KEY      = "muted",
            SEEK_TIME_KEY       = "time",
            VOLUME_LEVEL_KEY    = "level",
            //Chomecast Sates
            CHROME_CAST_NAME_KEY        = "name",
            IP_KEY                      = "ip",
            CONNECTED_KEY               = "connected",
            STATUS_KEY                  = "status",
            PORT_KEY                    = "port",
            CHROMECAST_PENDING_PARCELS  = "pendingParcels",
            IS_VIRTUAL_KEY              = "isVirtual",
            //Radio Station Info
            STATION_KEY             = "station",
            RADIO_70S               = "70s",
            RADIO_80S               = "80s",
            RADIO_90S               = "90s",
            RADIO_CLASSIC_ROCK      = "classicRock",
            RADIO_CLASSIC_ROCK_1    = "classicRock1",
            RADIO_CLASSICAL         = "classical",
            RADIO_WITR              = "witr",
            RADIO_ROCK              = "rock",
            RADIO_MORNING_NEWS      = "morningNews",
            RADIO_NEWS              = "news",
            RADIO_BUSINESS          = "business";
    }

    public static final class HuePS{
        public static final String
            HUE_USERNAME    = "iixA66asLRYI-jOBsmrwjIhpu7VYkTl1R1CitgZa",
            LIGHT_2_ID_KEY  = "light2Id",
            SCENE_2_ID_KEY  = "scene2Id",
            GROUP_2_ID_KEY  = "group2Id",
            ID_2_LIGHT_KEY  = "id2Light",
            ID_2_GROUP_KEY  = "id2Scene",
            ID_2_SCENE_KEY  = "id2Group",
            SEND_PERIOD_KEY = "sendFrequency",
            LIGHT_SCENE_KEY = "lightScenes",
            MOTION_SCENE_LIST_KEY = "activeMotionScenes",
            GROUP_LIGHTS_KEY= "groupLights",
            LIGHT_INFO_KEY= "lightInfo",
            //Inner Light Info
            LIGHT_INFO_TYPE_KEY  = "lightType",
            LIGHT_INFO_COLOR_SUPPORT_KEY = "supportsColor",
            LIGHT_INFO_CT_SUPPORT_KEY = "supportsCt",
            LIGHT_INFO_BRIGHTNESS_SUPPORT_KEY = "supportsBrightness",


            //motion scene
            MOTION_SCENE_TYPE_KEY = "motionSceneType",
            MOTION_SCENE_THREAD_KEY = "runningThread",
            MOTION_SCENE_LIGHTS_KEY = "lights",

            //motionSceneNames
            MOTION_SCENE_RAINBOW = "rainbow",
            MOTION_SCENE_HUE_SHIFT = "hueShift",
            MOTION_SCENE_RANDOM_LIGHTS = "randomColors",
            MOTION_SCENE_AMBIANCE_SHIFT = "amabanceShift",
            MOTION_SCENE_FALMES         ="flames",

        //motion scene effct paramets
            MS_EFFECT_UPDATE_INTERVAL = "updateInterval",
            MS_EFFECT_CYCLE_TIME      = "cycleTime",
            //


            //light scene stuff
            MOTION_LIGHT_SCENE_KEY          = "motionScene",
            INIT_LIGHT_SCENE_KEY            = "initValue",
            CAN_OVERRIDE_LIGHT_SCENE_KEY    = "canOverride",
            LIGHT_SCENE_NAME_KEY            ="sceneName",
            LIGHT_SCENES_KEY                = "scenes",
            ACTIVE_LIGHT_SCENE_KEY          = "activeScene",
            UNKNOWN_LIGHT_SCENE             = "unknown",
            // Some inner stuffs now
            GROUP_KEY       = "group",
            MODE_KEY        = "mode",
            LIGHT_KEY       = "light",
            ALL_LIGHTS_KEY = "allLights",
            MODE_OFF        = "off",
            MODE_ON         = "on",
            MODE_CUSTOM     = "custom",
            MODE_MOTION     = "motion",

            LIGHT_STATE_HUE_KEY         = "hue",
            LIGHT_STATE_SATURATION_KEY  = "sat",
            LIGHT_STATE_BRIGHTNESS_KEY = "bri",
            LIGHT_STATE_POWER_KEY       = "on",
            LIGHT_STATE_TRANS_TIME      = "transTime",
            LIGHT_STATE_COLOR_TEMP      = "ct",
            LIGHT_STATE_COLOR_MODE     = "colormode",
            LIGHT_STATE_XY_KEY           ="xy",
            LIGHT_STATE_X_KEY           ="x",
            LIGHT_STATE_Y_KEY           ="y",


            //internal lightCommands command
            TYPE_KEY                    = "type",
            SCENE_ID_KEY                = "sceneId",
            GROUP_UPDATE_LIGHT_COMMAND  = "groupUpdate",
            LIGHT_UPDATE_LIGHT_COMMAND  = "individualLightUpdate",
            SCENE_UPDATE_LIGHT_COMMAND  = "sceneLightUpdate",
            ALL_LIGHT_UPDATE_COMMAND    = "allLightUpdate",
            PH_LIGHT_STATE_KEY          = "phLightState";
    }

    public static class RemotePS{
        public static final String
            VOLUME_UP_REMOTE_KEY   = "volumeUp",
            VOLUME_DOWN_REMOTE_KEY = "volumeDown",
            POWER_REMOTE_KEY       = "power",
            AUDIO_INPUT_SELECT   = "audioIn",
            TV_INPUT_SELECT     = "tvIn",
            AUX_INPUT_SELECT    = "auxIn",
            MUTE_REMOTE_KEY            = "mute",

            ENABLE_IR_REMOTE_STATE = "enableIR",
            REMOTE_NAME = "remoteName",
            REMOTE_CODE = "remoteCode",
            POWER_REMOTE_STATE = "power",
            VOLUME_REMOTE_STATE = "volume",
            INPUT_REMOTE_STATE = "input",

            BEDROOM_CHROMECAST_REMOTES_PRESET   = "bedroomChromecast",
            BEDROOM_STANDARD_REMOTES_PRESET     = "bedroomStanard",
            BEDROOM_OFF_REMOTES_PRESET          = "bedroomOff",

            KEY_CODES_KEY = "keyCodes",
            REMOTE_STATES_KEY = "remoteStates",
            REMOTE_STATE_KEY = "remoteState",
            PRESETS_KEY     = "presets",
            PRESS_OP        ="press",
            PRESET_UPDATE   = "preset";
    }

    public static class IFTTT{
        public static final String
                CONDITIONAL_PARCEL_KEY = "conditionalParcel",
                CONDITIONAL_KEY = "conditional",
                CONDITIONAL_RESULT_KEY = "result",
                CONDITIONAL_IF = "if",
                COGNITIONAL_SWITCH = "switch",
                CONDITIONAL_DO_KEY = "doThis",
                CONDITIONAL_VALUE_KEY = "conditionalValue";
    }
    public static class HomeState{
        public static final String
                IS_WILL_HERE_KEY = "isWillHere";
    }
}

