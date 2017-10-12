package home.system.homeState;

import home.parcel.Parcel;
import home.parcel.StateValue;
import home.parcel.SystemException;
import home.system.SystemParent;

import static home.controller.PS.GenericPS.GET_OP_KEY;
import static home.controller.PS.GenericPS.OP_KEY;
import static home.controller.PS.GenericPS.WHAT_KEY;
import static home.controller.PS.HOME_STATE_SYSTEM_NAME;
import static home.controller.PS.HomeState.IS_WILL_HERE_KEY;

public class HomeStateSystem extends SystemParent{
    Parcel state;
    public HomeStateSystem() {
        super(HOME_STATE_SYSTEM_NAME);
        state = startState();
    }

    private static Parcel startState(){
        Parcel state = new Parcel();
        state.put(IS_WILL_HERE_KEY, new StateValue(true, StateValue.READ_WRITE_PRIVLAGE));
        return state;
    }

    @Override
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString(OP_KEY)) {
            case GET_OP_KEY:
            switch (p.getString(WHAT_KEY)) {
                case "state":
                    return Parcel.RESPONSE_PARCEL(state);
                default:
                    if (state.contains(p.getString("what"))) {
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

        default:
            throw SystemException.OP_NOT_SUPPORTED(p);
        }
    }
}
