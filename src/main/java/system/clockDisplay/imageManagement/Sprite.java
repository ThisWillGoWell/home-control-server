<<<<<<< HEAD:src/main/java/system/clockDisplay/imageManagement/Sprite.java
package system.clockDisplay.imageManagement;

import java.util.ArrayList;

/**
 * Created by Willi on 10/2/2016.
 */
public class Sprite {

   boolean animated = false;
    String spriteID;
    ArrayList<Frame> frames;
    int startFrame;

    static Sprite[] NUMBERS;

    Sprite(String spriteID, ArrayList<Frame> frames)
    {
        this.spriteID = spriteID;
        this.frames = frames;
    }
    Sprite(String spriteID, Frame[] f)
    {
        this.spriteID = spriteID;
        this.frames = new ArrayList<>();
        for (int i = 0; i < f.length; i++) {
            this.frames.add(f[i]);
        }
    }


    void setStartFrame(int i)
    {
        startFrame =i;
    }

    public ArrayList<Frame> getFrames()
    {
        return frames;
    }




    /*
        Values[]

    Sprite(String spriteID, ArrayList<int[][]> values)
    {

        frames = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            frames.add(new Frame(values.get(i) ))
        }
    }
    */


}
=======
package system.clockDisplay.imageManagement;

import java.util.ArrayList;

/**
 * Created by Willi on 10/2/2016.
 */
public class Sprite {

   boolean animated = false;
    String spriteID;
    ArrayList<Frame> frames;
    int startFrame;

    static Sprite[] NUMBERS;

    Sprite(String spriteID, ArrayList<Frame> frames)
    {
        this.spriteID = spriteID;
        this.frames = frames;
    }
    Sprite(String spriteID, Frame[] f)
    {
        this.spriteID = spriteID;
        this.frames = new ArrayList<>();
        for (int i = 0; i < f.length; i++) {
            this.frames.add(f[i]);
        }
    }


    void setStartFrame(int i)
    {
        startFrame =i;
    }

    public ArrayList<Frame> getFrames()
    {
        return frames;
    }




    /*
        Values[]

    Sprite(String spriteID, ArrayList<int[][]> values)
    {

        frames = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            frames.add(new Frame(values.get(i) ))
        }
    }
    */


}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/imageManagement/Sprite.java
