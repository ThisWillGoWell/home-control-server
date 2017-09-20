package home.system.irRemote;

import home.controller.Engine;
import home.controller.subscriber.SubscriberManager;
import home.parcel.Parcel;
import home.parcel.StateValue;
import home.parcel.SystemException;
import home.system.SystemParent;

import static home.controller.PS.IR_REMOTE_SYSTEM_NAME;
import static home.controller.PS.RemotePS.*;
import static home.controller.PS.GenericPS.*;
/**
 * Created by Will on 2/22/2017.
 * irsend -#10 SEND_ONCE sony_receiver KEY_POWER
 *
 */
public class IrRemote  extends SystemParent{
    public static final String systemIdentifier = IR_REMOTE_SYSTEM_NAME;
    Parcel state;


    public IrRemote(Engine e) {
        super(systemIdentifier, e);
        state = DEAFULT_IRREMOTE_STATE();

    }
    static Parcel DEAFULT_IRREMOTE_STATE(){

        Parcel keys = new Parcel();
        keys.put(VOLUME_UP_REMOTE_KEY, "KEY_VOLUMEUP");
        keys.put(VOLUME_DOWN_REMOTE_KEY, "KEY_VOLUMEDOWN");
        keys.put(MUTE_REMOTE_KEY, "KEY_MUTE");
        keys.put(POWER_REMOTE_KEY, "KEY_POWER");
        keys.put(AUDIO_INPUT_SELECT, "KEY_AUDIO");
        keys.put(TV_INPUT_SELECT, "KEY_TV");
        keys.put(AUX_INPUT_SELECT, "KEY_AUX");

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put(ENABLE_IR_REMOTE_STATE, false);
        sonyReceiver.put(REMOTE_NAME, "receiverRoom");
        sonyReceiver.put(REMOTE_CODE, "sony_receiver");
        sonyReceiver.put(POWER_REMOTE_STATE, false);
        sonyReceiver.put(VOLUME_REMOTE_STATE, 0.0);
        sonyReceiver.put(INPUT_REMOTE_STATE, "auxIn");

        Parcel samsungTv = new Parcel();
        samsungTv.put(ENABLE_IR_REMOTE_STATE, false);
        samsungTv.put(REMOTE_NAME, "tvRoom");
        samsungTv.put(REMOTE_CODE, "samsung_tv");
        samsungTv.put(POWER_REMOTE_STATE, false);
        samsungTv.put(VOLUME_REMOTE_STATE, 0.0);


        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);



        Parcel presets = new Parcel();
        presets.put(BEDROOM_CHROMECAST_REMOTES_PRESET, bedroomChromecastPreset());
        presets.put(BEDROOM_STANDARD_REMOTES_PRESET, bedroomStandardPreset());
        presets.put(BEDROOM_OFF_REMOTES_PRESET, bedroomOffPreset());


        Parcel p = new Parcel();
        p.put(KEY_CODES_KEY, new StateValue(keys, StateValue.READ_PRIVLAGE));
        p.put(REMOTE_STATES_KEY, new StateValue(remotes, StateValue.READ_WRITE_PRIVLAGE));
        p.put(PRESETS_KEY, presets);
        return p;
    }


    private static Parcel bedroomChromecastPreset(){

        Parcel samsungTv = new Parcel();
        samsungTv.put(POWER_REMOTE_STATE, true);
        samsungTv.put(VOLUME_REMOTE_STATE, 0.0);

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put(POWER_REMOTE_STATE, true);
        sonyReceiver.put(VOLUME_REMOTE_STATE, 10.0);
        sonyReceiver.put(INPUT_REMOTE_STATE, TV_INPUT_SELECT);

        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);

        return remotes;
    }

    private static Parcel bedroomStandardPreset(){

        Parcel samsungTv = new Parcel();
        samsungTv.put(POWER_REMOTE_STATE, true);
        samsungTv.put(VOLUME_REMOTE_STATE, 0.0);

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put(POWER_REMOTE_STATE, true);
        sonyReceiver.put(VOLUME_REMOTE_STATE, 10.0);
        sonyReceiver.put("input", "auxIn");

        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);

        return remotes;
    }

    private static Parcel bedroomOffPreset(){

        Parcel samsungTv = new Parcel();
        samsungTv.put(POWER_REMOTE_STATE, false);

        Parcel sonyReceiver = new Parcel();
        sonyReceiver.put(POWER_REMOTE_STATE, false);

        Parcel remotes = new Parcel();
        remotes.put("receiverRoom", sonyReceiver);
        remotes.put("tvRoom", samsungTv);

        return remotes;
    }

    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString(OP_KEY)){
            case GET_OP_KEY:
                switch (p.getString(WHAT_KEY)) {
                    case STATE_KEY:
                        return Parcel.RESPONSE_PARCEL(state);
                    case REMOTE_STATES_KEY:
                        return Parcel.RESPONSE_PARCEL(state.getParcel(REMOTE_STATES_KEY));
                    case REMOTE_STATE_KEY:
                        return Parcel.RESPONSE_PARCEL(state.getParcel(REMOTE_STATES_KEY).getParcel(p.getString("remote")));

                    default:
                        if(state.contains(p.getString(WHAT_KEY))) {
                            StateValue sp = state.getStateParcel(p.getString("what"));
                            if (sp.canRead()) {
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                        }
                        throw SystemException.WHAT_NOT_SUPPORTED(p);
                }
            case SET_OP_KEY:
                switch (p.getString(WHAT_KEY)) {

                    default:
                        StateValue sp = state.getStateParcel(p.getString("what"));
                        if (sp.canWrite()) {
                            sp.update(p.get(TO_KEY));
                            return Parcel.RESPONSE_PARCEL(sp.getValue());
                        }
                        throw SystemException.ACCESS_DENIED(p);
                    case PRESET_UPDATE:
                        return updateToPreset(p);
                }

            case PRESS_OP:
                return press(p);
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }

    }

    /**
     *
     * @param p {remote, command}
     * @return
     * @throws SystemException
     */
    private Parcel press(Parcel p) throws SystemException {
        //check if the press is valid
        if(state.getParcel(KEY_CODES_KEY).contains(p.getString("command"))){
            p.put(REMOTE_CODE, state.getParcel(REMOTE_STATES_KEY).getParcel(p.getString("remote")).getString(REMOTE_CODE));
            SubscriberManager.broadcast(this, p);
        }

        return Parcel.RESPONSE_PARCEL("button command Pressed");
    }

    private Parcel updateRemoteState(Parcel p) throws SystemException{
        Parcel preset = state.getParcel(PRESETS_KEY).getParcel(p.getString("preset"));
        return Parcel.RESPONSE_PARCEL(null);
    }

    private Parcel updateToPreset(Parcel p) throws SystemException {
        Parcel preset = state.getParcel(PRESETS_KEY).getParcel(p.getString("to"));
        for(String remoteID: preset.keySet()){
            for(String valueName: preset.getParcel(remoteID).keySet()){
                state.getParcel(REMOTE_STATES_KEY).getParcel(remoteID).put(valueName, preset.getParcel(remoteID).get(valueName));
            }
        }

        return Parcel.RESPONSE_PARCEL("Preset Updated");
    }



}
