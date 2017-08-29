
package home.system.coffee;

import home.controller.Engine;
import home.controller.subscriber.SubscriberManager;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.system.SystemParent;

/**
 * Created by Willi on 12/4/2016.
 *
 * Blue: Button
 * Yellow: Power LED
 * Grren: No Water
 * Orange: Brew LED
 * White: Lid switch signal
 * Gray: Lid control Signal
 */
public class Coffee extends SystemParent {
    public static final String systemIdentifier = "coffee";
    public Coffee(Engine e){
        super(systemIdentifier, e);

    }

    @Override
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")){
            case "makeCoffee":
                makeCoffee(p);
                return  Parcel.RESPONSE_PARCEL("starting to make coffee");
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }
    }

    private void makeCoffee(Parcel p){
        SubscriberManager.broadcast(this, p);
    }
}
