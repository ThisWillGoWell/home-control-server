<<<<<<< HEAD:src/main/java/system/clockDisplay/displayElements/DisplayElement.java
package system.clockDisplay.displayElements;

import com.google.gson.JsonObject;
import controller.Engine;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.LayerManager;
import system.clockDisplay.imageManagement.SpriteDict;

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
=======
package system.clockDisplay.displayElements;

import com.google.gson.JsonObject;
import controller.Engine;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.LayerManager;
import system.clockDisplay.imageManagement.SpriteDict;

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
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/displayElements/DisplayElement.java
