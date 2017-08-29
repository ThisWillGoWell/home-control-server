package home.system.irRemote;

import home.controller.Engine;
import home.parcel.Parcel;
import home.parcel.StateValue;
import home.parcel.SystemException;
import home.system.SystemParent;

/**
 * Created by Will on 2/22/2017.
 * irsend -#10 SEND_ONCE sony_receiver KEY_POWER
 *
 */
public class IrRemote  extends SystemParent{
    public static final String systemIdentifier = "irRemote";
    Parcel state;


    public IrRemote(Engine e) {
        super(systemIdentifier, e);
        state = DEAFULT_IRREMOTE_STATE();

    }
    static Parcel DEAFULT_IRREMOTE_STATE(){

        Parcel keys = new Parcel();
        keys.put("volumeUp", "KEY_VOLUMEUP");
        keys.put("volumeDown", "KEY_VOLUMEUDOWN");
        keys.put("power", "KEY_POWER");
        keys.put("volumeUp", "KEY_VOLUMEUP");
        keys.put("autioIn", "KEY_AUDIO");
        keys.put("tvIn", "KEY_TV");
        keys.put("auxIn", "KEY_AUX");
        keys.put("mute", "KEY_MUTE");


        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put("enableIR", false);
        sonyReceiver.put("systemIdentifier", "receiverRoom");
        sonyReceiver.put("remoteCode", "sony_receiver");
        sonyReceiver.put("power", false);
        sonyReceiver.put("volume", 0.0);
        sonyReceiver.put("input", "auxIn");

        Parcel samsungTv = new Parcel();
        samsungTv.put("enableIR", false);
        samsungTv.put("systemIdentifier", "tvRoom");
        samsungTv.put("remoteCode", "samsung_tv");
        samsungTv.put("power", false);
        samsungTv.put("volume", 0.0);

        Parcel denonReciever = new Parcel();
        denonReciever.put("enableIR", false);
        denonReciever.put("systemIdentifier", "receiverLivingRoom");
        denonReciever.put("remoteCode", "samsung_tv");
        denonReciever.put("power", false);
        denonReciever.put("volume", 0.0);
        denonReciever.put("input", "computer");

        Parcel projector = new Parcel();
        projector.put("enableIR", false);
        projector.put("systemIdentifier", "projector");
        projector.put("remoteCode", "samsung_tv");
        projector.put("power", false);
        projector.put("input", "hdmi1");


        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);
        remotes.put("receiverLivingRoom", denonReciever);
        remotes.put("projector", projector);



        Parcel presets = new Parcel();
        presets.put("bedroomChromecast", bedroomChromecastPreset());
        presets.put("bedroomStandard", bedroomStandardPreset());
        presets.put("bedroomOff", bedroomOffPreset());


        Parcel p = new Parcel();
        p.put("keyCodes", new StateValue(keys, StateValue.READ_PRIVLAGE));
        p.put("remoteStates", new StateValue(remotes, StateValue.READ_WRITE_PRIVLAGE));
        p.put("presets", presets);
        return p;
    }


    private static Parcel bedroomChromecastPreset(){

        Parcel samsungTv = new Parcel();
        samsungTv.put("power", true);
        samsungTv.put("volume", 0.0);

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put("power", true);
        sonyReceiver.put("volume", 10.0);
        sonyReceiver.put("input", "tvIn");

        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);

        return remotes;
    }

    private static Parcel bedroomStandardPreset(){

        Parcel samsungTv = new Parcel();
        samsungTv.put("power", true);
        samsungTv.put("volume", 0.0);

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put("power", true);
        sonyReceiver.put("volume", 10.0);
        sonyReceiver.put("input", "auxIn");

        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);

        return remotes;
    }

    private static Parcel bedroomOffPreset(){

        Parcel samsungTv = new Parcel();
        samsungTv.put("power", false);

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put("power", false);

        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);

        return remotes;
    }

    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")){
            case "get":
                switch (p.getString("what")) {
                    case "state":
                        return Parcel.RESPONSE_PARCEL(state);

                    case "remoteState":
                        return Parcel.RESPONSE_PARCEL(state.getParcel("remoteStates").getParcel(p.getString("remote")));

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

                    default:
                        StateValue sp = state.getStateParcel(p.getString("what"));
                        if (sp.canWrite()) {
                            sp.update(p.get("to"));
                            return Parcel.RESPONSE_PARCEL(sp.getValue());
                        }
                        throw SystemException.ACCESS_DENIED(p);
                    case "preset":
                        return updateToPreset(p);
                }

            case "press":
                return press(p);
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }

    }

    private Parcel press(Parcel p) throws SystemException {

        return Parcel.RESPONSE_PARCEL("button command Pressed");
    }

    private Parcel updateRemoteState(Parcel p) throws SystemException{
        Parcel preset = state.getParcel("presets").getParcel(p.getString("preset"));
        return Parcel.RESPONSE_PARCEL(null);
    }

    private Parcel updateToPreset(Parcel p) throws SystemException {
        Parcel preset = state.getParcel("presets").getParcel(p.getString("to"));
        for(String remoteID: preset.keySet()){
            for(String valueName: preset.getParcel(remoteID).keySet()){
                state.getParcel("remoteStates").getParcel(remoteID).put(valueName, preset.getParcel(remoteID).get(valueName));
            }
        }

        return Parcel.RESPONSE_PARCEL("Preset Updated");
    }



}
