package home.system.condtional;

import home.controller.Engine;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.system.SystemParent;

import static home.controller.PS.CONDITIONAL_SYSTEM_NAME;
import static home.controller.PS.GenericPS.PAYLOAD_KEY;
import static home.controller.PS.IFTTT.*;

public class ConditionalSystem  extends SystemParent{
    public ConditionalSystem( Engine e) {
        super(CONDITIONAL_SYSTEM_NAME, e);
    }

    @Override
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString(CONDITIONAL_KEY)){
            case CONDITIONAL_IF:
                return conditionalIf(p);
            case COGNITIONAL_SWITCH:
                return conditionalSwitch(p);
            default:
                throw SystemException.GENERIC_EXCEPTION("condition not supported", p);
        }
    }

    private Parcel conditionalIf(Parcel p) throws SystemException {
        Parcel result = engine.digestParcel(p.getParcel(CONDITIONAL_PARCEL_KEY));
        Object payload = result.toPayload();
        if(result.get(PAYLOAD_KEY).equals(p.get(CONDITIONAL_RESULT_KEY)) == p.getBoolean(CONDITIONAL_VALUE_KEY)){
            return engine.digestParcel(p.getParcel("doThis"));
        }
        throw SystemException.GENERIC_EXCEPTION("condition not met", p);
    }

    private Parcel conditionalSwitch(Parcel p) throws SystemException{
        Parcel result = engine.digestParcel(p.getParcel(CONDITIONAL_PARCEL_KEY));
        return engine.digestParcel(p.getParcel(CONDITIONAL_RESULT_KEY).getParcel(result.toPayload().toString()));
    }
}
