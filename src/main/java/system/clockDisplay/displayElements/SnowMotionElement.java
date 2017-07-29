<<<<<<< HEAD:src/main/java/system/clockDisplay/displayElements/SnowMotionElement.java
package system.clockDisplay.displayElements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.Engine;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;

/**
 * Created by Willi on 10/24/2016.
 */
public class SnowMotionElement extends DisplayElement{

    private int imageHeight;
    private int imageWidth;
    Frame s;
    public SnowMotionElement(String id, ClockDisplaySystem clockDisplaySystem, int size, int row, int col, long updateInterval, Engine e)
    {
        super(id, clockDisplaySystem,size, row, col, updateInterval);
        //size is meaningless
        //always 12x12
        imageHeight = 32;
        imageWidth = 16;
        s =  spriteDict.get("snow").getFrames().get(0);
        layerManager.addLayer(id);
    }

    @Override
    public JsonObject[] get(long time) {
        //update interval = 1 pixel down on screen
        //total round trip = rows * updateInterval
        int startLocation = s.getHeight() - (int) (( time / updateInterval) % s.getHeight());
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.add("fill", fill());
        json.addProperty("l", layerManager.get(id));
        JsonArray frames = new JsonArray();
        JsonObject current = new JsonObject();


        if(startLocation + imageHeight <= s.getHeight()) {
            current.addProperty("r", row);
            current.addProperty("c", col);
            current.addProperty("sr", startLocation);
            current.addProperty("n", s.getFrameNumber());
            current.addProperty("w", imageWidth);
            current.addProperty("h", imageHeight);
            frames.add(current);
        }
        else {

            current.addProperty("r", row);
            current.addProperty("c", col);
            current.addProperty("sr", startLocation);
            current.addProperty("n", s.getFrameNumber());
            current.addProperty("w", imageWidth);
            current.addProperty("h", imageHeight);
            frames.add(current);

            current = new JsonObject();
            current.addProperty("r", row + s.getHeight() - startLocation);
            current.addProperty("c", col);
            current.addProperty("n", s.getFrameNumber());
            current.addProperty("w", imageWidth);
            current.addProperty("h",imageHeight- (s.getHeight()-startLocation));
            frames.add(current);

        }
        json.add("f",frames);

        return new JsonObject[]{json};
    }


}
=======
package system.clockDisplay.displayElements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.Engine;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;

/**
 * Created by Willi on 10/24/2016.
 */
public class SnowMotionElement extends DisplayElement{

    private int imageHeight;
    private int imageWidth;
    Frame s;
    public SnowMotionElement(String id, ClockDisplaySystem clockDisplaySystem, int size, int row, int col, long updateInterval, Engine e)
    {
        super(id, clockDisplaySystem,size, row, col, updateInterval);
        //size is meaningless
        //always 12x12
        imageHeight = 32;
        imageWidth = 16;
        s =  spriteDict.get("snow").getFrames().get(0);
        layerManager.addLayer(id);
    }

    @Override
    public JsonObject[] get(long time) {
        //update interval = 1 pixel down on screen
        //total round trip = rows * updateInterval
        int startLocation = s.getHeight() - (int) (( time / updateInterval) % s.getHeight());
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.add("fill", fill());
        json.addProperty("l", layerManager.get(id));
        JsonArray frames = new JsonArray();
        JsonObject current = new JsonObject();


        if(startLocation + imageHeight <= s.getHeight()) {
            current.addProperty("r", row);
            current.addProperty("c", col);
            current.addProperty("sr", startLocation);
            current.addProperty("n", s.getFrameNumber());
            current.addProperty("w", imageWidth);
            current.addProperty("h", imageHeight);
            frames.add(current);
        }
        else {

            current.addProperty("r", row);
            current.addProperty("c", col);
            current.addProperty("sr", startLocation);
            current.addProperty("n", s.getFrameNumber());
            current.addProperty("w", imageWidth);
            current.addProperty("h", imageHeight);
            frames.add(current);

            current = new JsonObject();
            current.addProperty("r", row + s.getHeight() - startLocation);
            current.addProperty("c", col);
            current.addProperty("n", s.getFrameNumber());
            current.addProperty("w", imageWidth);
            current.addProperty("h",imageHeight- (s.getHeight()-startLocation));
            frames.add(current);

        }
        json.add("f",frames);

        return new JsonObject[]{json};
    }


}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/displayElements/SnowMotionElement.java
