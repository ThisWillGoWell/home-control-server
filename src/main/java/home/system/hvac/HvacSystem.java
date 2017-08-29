
package home.system.hvac;

import home.controller.Engine;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.parcel.StateValue;
import home.system.SystemParent;

/**
 * Created by Will on 9/3/2016.
 * Class to manage the HVAC system
 * Controls Heater, AC and Fan
 *
 */

public class HvacSystem extends SystemParent{
    public static final String systemIdentifier = "hvac";
    private Parcel state;


    public static Parcel DEAFULT_HVAC_STATE(){
        Parcel p = new Parcel();
        p.put("systemTemp", new StateValue((double) 20, StateValue.READ_WRITE_PRIVLAGE));
        p.put("roomTemp", new StateValue( (double) 20, StateValue.READ_WRITE_PRIVLAGE));
        p.put("mode", new StateValue("off", StateValue.READ_WRITE_PRIVLAGE));
        p.put("acState", new StateValue(false, StateValue.READ_PRIVLAGE));
        p.put("heatState", new StateValue(false, StateValue.READ_PRIVLAGE));
        p.put("fanState", new StateValue(false, StateValue.READ_PRIVLAGE));
        p.put("threshold", new StateValue(0.75, StateValue.READ_WRITE_PRIVLAGE));
        return p;
    }

    /*
    Class to represent the HVAC system
    Controls the heater, fan, ac
    update every 3.5 seconds
     */
    public HvacSystem( Engine e)
    {
        super(systemIdentifier, e, 3500);
        state = DEAFULT_HVAC_STATE();
        update();
    }


    public Parcel process(Parcel p) throws SystemException {
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
    }



    /*
    Use a smhit trigger style to control the modes
    not sure why I used a state here, but it does make it easier to read?
     */
   public void update() {
       try {
           switch (state.getString("mode")) {
               case "cool":
                   state.getStateParcel("heatState").update(false);
                   if (state.getDouble("roomTemp") > state.getDouble("systemTemp") + state.getDouble("threshold")) {
                       state.getStateParcel("acState").update(true);
                       state.getStateParcel("fanState").update(true);
                   } else if (state.getDouble("roomTemp") < state.getDouble("systemTemp") - state.getDouble("threshold")) {
                       state.getStateParcel("acState").update(false);
                       state.getStateParcel("fanState").update(false);
                   }
                   break;

               case "heat":
                   state.getStateParcel("acState").update(false);
                   if (state.getDouble("roomTemp") < state.getDouble("systemTemp") - state.getDouble("threshold")) {
                       state.getStateParcel("heatState").update(true);
                       state.getStateParcel("fanState").update(true);
                   } else if (state.getDouble("roomTemp") > state.getDouble("systemTemp") + state.getDouble("threshold")) {
                       state.getStateParcel("heatState").update(false);
                       state.getStateParcel("fanState").update(false);
                   }
                   break;

               case "fan":
                   state.getStateParcel("heatState").update(false);
                   state.getStateParcel("acState").update(false);
                   state.getStateParcel("fanState").update(true);
                   break;

               case "off":
                   state.getStateParcel("fanState").update(false);
                   state.getStateParcel("heatState").update(false);
                   state.getStateParcel("acState").update(false);
                   break;
           }

       } catch (SystemException e) {
           e.printStackTrace();
       }
   }
}
