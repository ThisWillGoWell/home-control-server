package system.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Willi on 9/26/2016.
 */
public class NetworkState {

    private HashMap<String, NetworkDevice> connected;

    NetworkState()
    {
        connected = new HashMap<>();
    }

    HashMap<String, NetworkDevice> getConnected(){return connected;};


    String getJson()
    {
        String s;
        s = "\"connected\":{";

        String[] macs = (String[]) connected.keySet().toArray();
        for(int i=0;i<macs.length;i++)
        {
            s += "\"" + macs[i]  + "\":{ \"ip\":\"" + connected.get(macs[i]).ip + "\",\"systemIdentifier\":" + connected.get(macs[i]).name+"\"}";
            if(i!=macs.length-1)
            {
                s += ",";
            }
        }
        return s;

    }
}
