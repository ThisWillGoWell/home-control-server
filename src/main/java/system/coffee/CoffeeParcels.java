package system.coffee;

import parcel.Parcel;

/**
 * Created by Will on 3/20/2017.
 */
public class CoffeeParcels {
    private static String systemName = Coffee.systemIdentifier;
    public static Parcel brewCoffee(){
        Parcel p = new Parcel();
        p.put("system", Coffee.systemIdentifier);
        p.put("op", "makeCoffee");
        return p;
    }
}
