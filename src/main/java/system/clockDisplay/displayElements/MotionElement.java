<<<<<<< HEAD:src/main/java/system/clockDisplay/displayElements/MotionElement.java
package system.clockDisplay.displayElements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;
import system.clockDisplay.imageManagement.MotionSprite;


/**
 * Created by Willi on 10/3/2016.
 */
public class MotionElement extends DisplayElement {

    MotionSprite motionSprite;
    MotionElement(String id, ClockDisplaySystem system, int row, int col)
    {
        super( id,  system,1, row, col, 1000);
        motionSprite = (MotionSprite) system.getSpriteDict().get(id);
    }

    @Override
    public JsonObject[] get(long time) {
        JsonObject json = new JsonObject();
        Frame f = motionSprite.getFrames().get(motionSprite.getFrameIndex(time));
        JsonArray list = new JsonArray();
        JsonObject frame = new JsonObject();
        frame.addProperty("n", f.getFrameNumber());
        frame.addProperty("w", f.getLength());
        frame.addProperty("h", f.getHeight());
        frame.addProperty("r", row);
        frame.addProperty("c", col);

        list.add(frame);
        json.addProperty("id", id);
        json.addProperty("t", time);
        json.add("f", list);
        json.add("fill", fill(255,255,0,255));


        return new JsonObject[]{json};
    }


}
=======
package system.clockDisplay.displayElements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;
import system.clockDisplay.imageManagement.MotionSprite;


/**
 * Created by Willi on 10/3/2016.
 */
public class MotionElement extends DisplayElement {

    MotionSprite motionSprite;
    MotionElement(String id, ClockDisplaySystem system, int row, int col)
    {
        super( id,  system,1, row, col, 1000);
        motionSprite = (MotionSprite) system.getSpriteDict().get(id);
    }

    @Override
    public JsonObject[] get(long time) {
        JsonObject json = new JsonObject();
        Frame f = motionSprite.getFrames().get(motionSprite.getFrameIndex(time));
        JsonArray list = new JsonArray();
        JsonObject frame = new JsonObject();
        frame.addProperty("n", f.getFrameNumber());
        frame.addProperty("w", f.getLength());
        frame.addProperty("h", f.getHeight());
        frame.addProperty("r", row);
        frame.addProperty("c", col);

        list.add(frame);
        json.addProperty("id", id);
        json.addProperty("t", time);
        json.add("f", list);
        json.add("fill", fill(255,255,0,255));


        return new JsonObject[]{json};
    }


}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/displayElements/MotionElement.java
