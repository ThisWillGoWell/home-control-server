<<<<<<< HEAD:src/main/java/system/clockDisplay/imageManagement/SpriteDict.java
package system.clockDisplay.imageManagement;

import java.io.File;
import java.util.HashMap;

/**
 * Sprite Dict, used to store all the sprints in a hashMap
 * Created by Willi on 10/2/2016.
 */
public class SpriteDict extends HashMap<String,Sprite>{
    public SpriteDict()
    {
        super();
        make();
    }

    private void make()
    {
        //AutoAdd anything in the folder
        File[] files = new File("resources/gifs").listFiles();
        for (File file : files) {
            this.put(file.getName().replaceFirst("[.][^.]+$", ""), new Sprite(file.getName().replaceFirst("[.][^.]+$", ""),new Frame[]{Frame.gifToFrame(file)}));
        }


        //Numbers
        for (int i = 0; i < 10; i++) {
            this.put(i+"", new Sprite(i+"", new Frame[]{Frame.NUMBERS_FRAME_SMALL[i], Frame.NUMBERS_FRAME[i], Frame.NUMBERS_FRAME_LARGE[i]} ));
        }

        //OtherCharacters
        this.put(" ", new Sprite(" ", new Frame[]{Frame.EMPTY_FRAME_SMALL, Frame.EMPTY_FRAME, Frame.EMPTY_FRAME_LARGE}));
        this.put(":", new Sprite(":", new Frame[]{Frame.COLON_FRAME_SMALL, Frame.COLON_FRAME, Frame.COLON_FRAME_LARGE}));
        this.put("degree", new Sprite("degree", new Frame[]{Frame.DEGREE_FRAME_SMALL, Frame.DEGREE_FRAME}));
        this.put(".", new Sprite(".", new Frame[]{Frame.DECIMAL_FRAME_SMALL, Frame.DECIMAL_FRAME, Frame.DECIMAL_FRAME_LARGE}));
        this.put("spin1", new MotionSprite("spin1",Frame.SPIN1, 500));
        this.put("pixel", new Sprite("pixel", new Frame[]{Frame.PIXEL}));
        this.put("rainDrop", new Sprite("rainDrop", new Frame[]{Frame.RAIN_SCREEN()}));
        //this.put("snow", new Sprite("snow", new Frame[]{Frame.SNOW_SCREEN()}));
        this.put("mario", new Sprite("mario", new Frame[]{Frame.MARIO()}));
        this.put("-",new Sprite("-", new Frame[]{Frame.MINUS_FRAME,Frame.MINUS_FRAME}));
    }


}
=======
package system.clockDisplay.imageManagement;

import java.io.File;
import java.util.HashMap;

/**
 * Sprite Dict, used to store all the sprints in a hashMap
 * Created by Willi on 10/2/2016.
 */
public class SpriteDict extends HashMap<String,Sprite>{
    public SpriteDict()
    {
        super();
        make();
    }

    private void make()
    {
        //AutoAdd anything in the folder
        File[] files = new File("resources/gifs").listFiles();
        for (File file : files) {
            this.put(file.getName().replaceFirst("[.][^.]+$", ""), new Sprite(file.getName().replaceFirst("[.][^.]+$", ""),new Frame[]{Frame.gifToFrame(file)}));
        }


        //Numbers
        for (int i = 0; i < 10; i++) {
            this.put(i+"", new Sprite(i+"", new Frame[]{Frame.NUMBERS_FRAME_SMALL[i], Frame.NUMBERS_FRAME[i], Frame.NUMBERS_FRAME_LARGE[i]} ));
        }

        //OtherCharacters
        this.put(" ", new Sprite(" ", new Frame[]{Frame.EMPTY_FRAME_SMALL, Frame.EMPTY_FRAME, Frame.EMPTY_FRAME_LARGE}));
        this.put(":", new Sprite(":", new Frame[]{Frame.COLON_FRAME_SMALL, Frame.COLON_FRAME, Frame.COLON_FRAME_LARGE}));
        this.put("degree", new Sprite("degree", new Frame[]{Frame.DEGREE_FRAME_SMALL, Frame.DEGREE_FRAME}));
        this.put(".", new Sprite(".", new Frame[]{Frame.DECIMAL_FRAME_SMALL, Frame.DECIMAL_FRAME, Frame.DECIMAL_FRAME_LARGE}));
        this.put("spin1", new MotionSprite("spin1",Frame.SPIN1, 500));
        this.put("pixel", new Sprite("pixel", new Frame[]{Frame.PIXEL}));
        this.put("rainDrop", new Sprite("rainDrop", new Frame[]{Frame.RAIN_SCREEN()}));
        //this.put("snow", new Sprite("snow", new Frame[]{Frame.SNOW_SCREEN()}));
        this.put("mario", new Sprite("mario", new Frame[]{Frame.MARIO()}));
        this.put("-",new Sprite("-", new Frame[]{Frame.MINUS_FRAME,Frame.MINUS_FRAME}));
    }


}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/imageManagement/SpriteDict.java
