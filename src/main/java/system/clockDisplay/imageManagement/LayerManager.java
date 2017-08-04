
package system.clockDisplay.imageManagement;

import java.util.HashMap;

/**
 * Created by Willi on 10/23/2016.
 * This class is to manage the layers, to make sure evryone knows
 * what layer they are on.
 *
 * maps elementID->layer
 * Also makes the interaction simple for add()
 */
public class LayerManager extends HashMap<String, Integer> {
    private int currentPointer = 0;
    public LayerManager()
    {
        super();
    }

    public int addLayer(String s) {
        this.put(s,currentPointer++);
        return currentPointer;
    }

}