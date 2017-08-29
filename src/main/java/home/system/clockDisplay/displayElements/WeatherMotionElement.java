
package home.system.clockDisplay.displayElements;

import com.google.gson.JsonObject;
import home.system.clockDisplay.ClockDisplaySystem;

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
