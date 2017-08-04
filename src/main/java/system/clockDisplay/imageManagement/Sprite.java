
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