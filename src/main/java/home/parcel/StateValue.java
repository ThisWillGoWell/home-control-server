package home.parcel;

import javax.swing.plaf.nimbus.State;

/**
 * Created by Willi on 12/30/2016.
 */
public class StateValue{
    public static int READ_WRITE_PRIVLAGE = 0;
    public static int WRITE_PRIVLAGE = 1;
    public static int READ_PRIVLAGE = 2;

    private int privlage;
    private Object value;



    public StateValue(Object o, int p) {
        this.privlage = p;
        this.value = o;
    }

    public StateValue(StateValue sp) {
        this.privlage= sp.privlage;
        this.value = sp.value;
    }

    public Object getValue(){
        return value;
    }


    public static Parcel DEAFULT_WEATHER_STATE(){
        return null;
    }

    public static Parcel DEAFULT_CLOCK_STATE(){
        return null;
    }

    public static Parcel DEAFULT_HUE_STATE(){
        return null;
    }

    public static Parcel DEAFULT_NETWORK_STATE(){
        return null;
    }

    public void update(Object value) throws SystemException {
        if(value.getClass().equals(this.value.getClass())){
            this.value = value;
        }
        else
            throw new SystemException("update value: " + value.toString() + " of different type than current value: " + this.value, SystemException.UPDATE_FAILED, this);
    }

    public boolean canWrite(){
        return (privlage == READ_WRITE_PRIVLAGE || privlage == WRITE_PRIVLAGE);
    }

    public boolean canRead(){
        return (privlage == READ_WRITE_PRIVLAGE || privlage == READ_PRIVLAGE);
    }

    public String toString(){
        return value.toString();
    }
}
