
package system.clockDisplay.displayElements;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Simple clock element, displays the time, thats it...
 *
 */
public class ClockElement extends DisplayElement {
    private SimpleDateFormat format;
    private int characterCount;
    private static long UPDATE_INTERVAL = 30000;

    public ClockElement(String id, ClockDisplaySystem system, int size, int row, int col, SimpleDateFormat format, int characterCount) {
        super(id, system, size, row, col, UPDATE_INTERVAL);
        this.characterCount = characterCount;
        this.format = format;
        this.layerManager.addLayer(id);

    }

    public JsonObject[] get(long time) {
        //make the time for that time
        String timeStr = format.format(new Date(time));
        while (timeStr.length() < characterCount) {
            timeStr = " " + timeStr;
        }
        JsonObject json = new JsonObject();
        JsonArray list = new JsonArray();
        int writePointerRow = row;
        int writePointerCol = col;
        for (int i = 0; i < timeStr.length(); i++) {
            JsonObject frameData = new JsonObject();
            Frame s = spriteDict.get(timeStr.charAt(i) + "").getFrames().get(size);
            frameData.addProperty("n", s.getFrameNumber());
            frameData.addProperty("w", s.getLength());
            frameData.addProperty("h", s.getHeight());
            frameData.addProperty("r", writePointerRow);
            frameData.addProperty("c", writePointerCol);
            list.add(frameData);
            writePointerCol += s.getLength() + 1;
        }
        json.addProperty("id", id);
        json.addProperty("t", time);
        json.add("f", list);
        json.add("fill",fill());
        json.addProperty("l",layerManager.get(id));

        return new JsonObject[]{json};
    }

}
