package home.controller;

public class PS {

    //systemNames
    public static final String NETWORK_SYSTEM_NAME = "network";
    public static final String CHROMECAST_SYSTEM_NAME = "chromecast";





    public static final class GenericPS {
        public static final String GET_OP_KEY = "get";
        public static final String SET_OP_KEY = "set";
        public static final String WHAT_KEY = "what";
        public static final String OP_KEY = "op";
        public static final String SYSTEM_KEY = "system";


        //Parents
        public static final String SUB_ALERT_TYPE = "alert";
        public static final String SUB_CHANGE_TYPE = "change";

        public static final String SUBSCRIBER_KEY = "subscriber";

        public static final String HELLO_COMMAND = "hello";
        public static final String DEREGISTER_COMMAND = "deregister";
        public static final String SUB_COMMAND = "subscribe";
        public static final String UPDATE_COMMAND = "update";

        public static final String SUB_TYPE_KEY = "type";
        public static final String SUB_REQUEST_KEY = "request";
        public static final String PAYLOAD_KEY = "payload";


    }

    public static final class NetworkDevices{

        //Network Mac
        public static final String DINOLIGHT_MAC = "b8:27:eb:c2:f5:53";
        public static final String CHROMECAST_AUDIO_BEDROOM_MAC = "a4:77:33:f1:93:76";
        public static final String WILL_PHONE_ANDROID_MAC = "ac:37:43:4b:aa:e5";
        public static final String WILL_PHONE_IPHONE_MAC = "b8:27:eb:c2:f5:53";
        public static final String PHILIPS_HUE_MAC = "00:17:88:23:a2:86";

        //Device Names
        public static final String DINOLIGHT = "DINOLIGHT";
        public static final String CHROMECAST_AUDIO_BEDROOM = "Chromecast-Audio-Bedroom";
        public static final String WILL_PHONE_ANDROID = "will-phone-android";
        public static final String WILL_PHONE_IPHONE = "will-phone-iphone";
        public static final String PHILIPS_HUE = "philips-hue";

    }


    public static final class NetworkSystemStrings{
        public static final String NETWORK_SYSTEM_NAME1 = PS.NETWORK_SYSTEM_NAME;
        //Import Device Names and Mac from NetworkDevices
        public static final String DINOLIGHT = NetworkDevices.DINOLIGHT;
        public static final String CHROMECAST_AUDIO_BEDROOM = NetworkDevices.CHROMECAST_AUDIO_BEDROOM;
        public static final String WILL_PHONE_ANDROID = NetworkDevices.WILL_PHONE_ANDROID;
        public static final String WILL_PHONE_IPHONE = NetworkDevices.WILL_PHONE_IPHONE;
        public static final String PHILIPS_HUE = NetworkDevices.PHILIPS_HUE;

        public static final String DINOLIGHT_MAC = NetworkDevices.DINOLIGHT_MAC;
        public static final String CHROMECAST_AUDIO_BEDROOM_MAC = NetworkDevices.CHROMECAST_AUDIO_BEDROOM_MAC;
        public static final String WILL_PHONE_ANDROID_MAC = NetworkDevices.WILL_PHONE_ANDROID_MAC;
        public static final String WILL_PHONE_IPHONE_MAC = NetworkDevices.WILL_PHONE_IPHONE_MAC;
        public static final String PHILIPS_HUE_MAC = NetworkDevices.PHILIPS_HUE_MAC;

        //Parcel Keys
        public static final String DEVICE_KEY = "device";
        public static final String DEVICES_KEY = "devices";
        public static final String TIMEOUT_KEY = "timeout";
        public static final String CONNECTED_KEY = "connected";
        public static final String IP_KEY = "ip";
        public static final String LAST_PING_TIME_KEY = "LAST_PING_TIME_KEY";
        public static final String MAC_KEY = "mac";

    }

    public static final class ChromeCastPS {
        public static final String CHROMECAST_AUDIO_BEDROOM = NetworkDevices.CHROMECAST_AUDIO_BEDROOM;

        public static final String APP_2_APP_ID = "app2appID";
        public static final String CHROME_CAST_NAME_MAP = "chromeCastNameMap";
        public static final String CHROME_CASTS_KEY = "chromeCasts";

        //ChromeCast App Names
        public static final String MEDIA_APP_NAME = "CC1AD845";
        public static final String YOUTUBE_APP_NAME = "233637DE";
        public static final String BACKDROP_APP_NAME = "E8C28D3C";

        //Process commands
        public static final String CAST_NAME_KEY = "castName";

        //ChromeCastSystem Ops/directives
        public static final String PLAY_OP = "play";
        public static final String PLAY_RADIO_COMMAND = "radio";


        public static final String CLOSE_OP = "close";
        public static final String CONTROL_OP = "control";

        public static final String ACTION_KEY  = "action";

        public static final String PLAY_COMMAND = "play";
        public static final String PAUSE_COMMAND = "pause";
        public static final String VOLUME_COMMAND = "volume";
        public static final String MUTE_COMMAND = "mute";
        public static final String SEEK_COMMAND = "seek";
        public static final String MUTE_VALUE_KEY = "muted";
        public static final String SEEK_TIME_KEY = "time";
        public static final String VOLUME_LEVEL_KEY = "level";


        //Chomecast Sates
        public static final String CHROME_CAST_NAME_KEY = "name";
        public static final String IP_KEY = "ip";
        public static final String CONNECTED_KEY = "connected";
        public static final String STATUS_KEY = "status";
        public static final String PORT_KEY = "port";
        public static final String CHROMECAST_PENDING_PARCELS = "pendingParcels";
        public static final String IS_VIRTUAL_KEY = "isVirtual";


        //Radio Station Info
        public static final String STATION_KEY = "station";
        public static final String RADIO_70S = "70s";
        public static final String RADIO_80S = "80s";
        public static final String RADIO_90S = "90s";
        public static final String RADIO_CLASSIC_ROCK = "classicRock";
        public static final String RADIO_CLASSIC_ROCK_1 = "classicRock1";
        public static final String RADIO_CLASSICAL = "classical";
        public static final String RADIO_WITR = "witr";
        public static final String RADIO_ROCK = "rock";
        public static final String RADIO_NEWS = "news";
        public static final String RADIO_BUSINESS = "business";


        //Other Ops
        public static final String GET_OP_KEY = GenericPS.GET_OP_KEY;
        public static final String SET_OP_KEY = GenericPS.SET_OP_KEY;
        public static final String OP_KEY = GenericPS.OP_KEY;
        public static final String WHAT_KEY = GenericPS.WHAT_KEY;
        //


    }


}
