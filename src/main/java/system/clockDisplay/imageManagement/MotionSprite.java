
package system.clockDisplay.imageManagement;

import java.util.ArrayList;

/**
 * Created by Willi on 10/2/2016.
 */
public class MotionSprite extends Sprite {

    private long updateInterval;
    private long nextUpdateTime;
    private int numFrames;


    public MotionSprite(String spriteID, ArrayList<Frame> frames, long updateInterval) {
        super(spriteID,frames);

        this.updateInterval = updateInterval;
        numFrames = frames.size();
        animated = true;
    }

    public MotionSprite(String spriteID, Frame[] f, long updateInterval) {
        super(spriteID,f);
        this.updateInterval = updateInterval;
        numFrames = frames.size();
        animated = true;

    }


    /*
        Given a time, what index should it be at?
     */
    public int getFrameIndex(long time)
    {
        return (int) (((time)/updateInterval) % numFrames);
    }

    public long getNextUpdateTime()
    {
        return System.currentTimeMillis() + System.currentTimeMillis() % updateInterval;
    }





}