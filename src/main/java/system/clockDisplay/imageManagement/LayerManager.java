<<<<<<< HEAD:src/main/java/system/clockDisplay/imageManagement/LayerManager.java
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
=======
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
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/imageManagement/LayerManager.java
