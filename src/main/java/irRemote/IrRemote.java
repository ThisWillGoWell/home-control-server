package irRemote;

import controller.Engine;
import parcel.Parcel;
import parcel.StateValue;
import parcel.SystemException;
import system.hue.HueSystem;

/**
 * Created by Will on 2/22/2017.
 * irsend -#10 SEND_ONCE sony_receiver KEY_POWER
 *
 */
public class IrRemote  extends HueSystem{
    Parcel state;

    public IrRemote(Engine e) {
        super(e);

    }
    static Parcel DEAFULT_IRREMOTE_STATE(){
        return new Parcel();
    };

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
                        default:
                            StateValue sp = state.getStateParcel(p.getString("what"));
                            if (sp.canWrite()) {
                                sp.update(p.get("to"));
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                    }
                case "systemTempUp":
                    state.getStateParcel("systemTemp").update((Double) state.getStateParcel("systemTemp").getValue() + 1);
                    return Parcel.RESPONSE_PARCEL("tempUp success");
                case "systemTempDown":
                    state.getStateParcel("systemTemp").update((Double) state.getStateParcel("systemTemp").getValue() - 1);
                    return Parcel.RESPONSE_PARCEL("tempDown success");
                default:
                    throw SystemException.OP_NOT_SUPPORTED(p);
            }
        } catch (SystemException e) {
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        }

    }
}
