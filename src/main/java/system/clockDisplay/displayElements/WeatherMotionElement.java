<<<<<<< HEAD:src/main/java/system/clockDisplay/displayElements/WeatherMotionElement.java
package system.clockDisplay.displayElements;

import com.google.gson.JsonObject;
import system.clockDisplay.ClockDisplaySystem;

/**
 * Created by Willi on 12/5/2016.
 */
public class WeatherMotionElement extends DisplayElement{


    WeatherMotionElement(String id, ClockDisplaySystem system, int size, int row, int col, long updateInterval) {
        super(id, system, size, row, col, updateInterval);
    }

    @Override
    public JsonObject[] get(long time) {
        return new JsonObject[0];
    }

    @Override
    public void update(){


    }
}
=======
package system.clockDisplay.displayElements;

import com.google.gson.JsonObject;
import system.clockDisplay.ClockDisplaySystem;

/**
 * Created by Willi on 12/5/2016.
 */
public class WeatherMotionElement extends DisplayElement{


    WeatherMotionElement(String id, ClockDisplaySystem system, int size, int row, int col, long updateInterval) {
        super(id, system, size, row, col, updateInterval);
    }

    @Override
    public JsonObject[] get(long time) {
        return new JsonObject[0];
    }

    @Override
    public void update(){


    }
}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/displayElements/WeatherMotionElement.java
