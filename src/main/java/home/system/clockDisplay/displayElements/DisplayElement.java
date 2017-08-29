
package home.system.clockDisplay.displayElements;

import com.google.gson.JsonObject;
import home.controller.Engine;
import home.system.clockDisplay.ClockDisplaySystem;
import home.system.clockDisplay.imageManagement.LayerManager;
import home.system.clockDisplay.imageManagement.SpriteDict;

/**
 * Parent Object for anything that wishes to be displayed on scrren
 *
 */
public abstract class DisplayElement {

    SpriteDict spriteDict;
    int size, row, col;
    long updateInterval;
    LayerManager layerManager;
    Engine engine;
    String id;
    DisplayElement(String id, ClockDisplaySystem system, int size, int row, int col, long updateInterval)
    {
        this.id = id;
        this.spriteDict = system.getSpriteDict();
        layerManager = system.getLayoutManager();
        this.size = size;
        this.row = row;
        this.col =col;
        this.updateInterval = updateInterval;
        engine = system.getEngine();
    }

    public abstract JsonObject[] get(long time);

    public long getUpdateInterval(){
        return updateInterval;
    }

    public void update()
    {

    }

    static JsonObject fill(int r, int g, int b, int a)
    {

        JsonObject json = new JsonObject();
        json.addProperty("fill",true);
        json.addProperty("r", r);
        json.addProperty("g", g);
        json.addProperty("b", b);
        json.addProperty("a", a);

        return json;
    }

    static JsonObject fill()
    {
        JsonObject json = new JsonObject();
        json.addProperty("fill", false);
        return json;
    }


}