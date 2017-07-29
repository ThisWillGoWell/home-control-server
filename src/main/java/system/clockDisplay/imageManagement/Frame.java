<<<<<<< HEAD:src/main/java/system/clockDisplay/imageManagement/Frame.java
package system.clockDisplay.imageManagement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Willi on 9/30/2016.
 */
public class Frame {
    //http://www.quinapalus.com/hd44780udg.html

    //Medium Ascii 5x7 Standard Characters
    static private Frame ZERO_FRAME = new Frame(5, 7, new int[]{14, 27, 27, 27, 27, 27, 14});
    static private Frame ONE_FRAME = new Frame(5, 7, new int[]{2,6,14,6,6,6,6});
    static private Frame TWO_FRAME =  new Frame(5, 7, new int[]{14,27,3,6,12,24,31});
    static private Frame THREE_FRAME =   new Frame(5, 7, new int[]{14,27,3,14,3,27,14});
    static private Frame FOUR_FRAME =  new Frame(5, 7, new int[]{3,7,15,27,31,3,3});
    static private Frame FIVE_FRAME =  new Frame(5, 7, new int[]{31,24,30,3,3,27,14});
    static private Frame SIX_FRAME =  new Frame(5, 7, new int[]{14,27,24,30,27,27,14});
    static private Frame SEVEN_FRAME =  new Frame(5, 7, new int[]{31,3,6,12,12,12,12});
    static private Frame EIGHT_FRAME =  new Frame(5, 7, new int[]{14,27,27,14,27,27,14});
    static private Frame NINE_FRAME =  new Frame(5, 7, new int[]{14,27,27,15,3,27,14});
    static protected Frame DEGREE_FRAME =  new Frame(3, 3, new int[]{2,5,2});
    static protected Frame COLON_FRAME = new Frame(5,7, new int[]{0,12,12,0,12,12,0});
    static protected Frame[] NUMBERS_FRAME = new Frame[]{ZERO_FRAME, ONE_FRAME, TWO_FRAME, THREE_FRAME, FOUR_FRAME, FIVE_FRAME, SIX_FRAME, SEVEN_FRAME, EIGHT_FRAME, NINE_FRAME};
    static protected Frame EMPTY_FRAME = new Frame(5,7, new int[]{0,0,0,0,0,0,0});
    static Frame MINUS_FRAME = new Frame(5,7,new int[]{0,0,0,7,0,0,0});


    //Small ASCII 3 x 5
    static private Frame ZERO_FRAME_SMALL =     new Frame(3, 5, new int[]{7,5,5,5,7});
    static private Frame ONE_FRAME_SMALL =      new Frame(2, 5, new int[]{1,1,1,1,1});
    static private Frame TWO_FRAME_SMALL =      new Frame(3, 5, new int[]{7,1,7,4,7});
    static private Frame THREE_FRAME_SMALL =    new Frame(3, 5, new int[]{7,1,7,1,7});
    static private Frame FOUR_FRAME_SMALL =     new Frame(3, 5, new int[]{5,5,7,1,1});
    static private Frame FIVE_FRAME_SMALL =     new Frame(3, 5, new int[]{7,4,7,1,7});
    static private Frame SIX_FRAME_SMALL =      new Frame(3, 5, new int[]{7,4,7,5,7});
    static private Frame SEVEN_FRAME_SMALL =    new Frame(3, 5, new int[]{7,1,1,1,1});
    static private Frame EIGHT_FRAME_SMALL =    new Frame(3, 5, new int[]{7,5,7,5,7});
    static private Frame NINE_FRAME_SMALL =     new Frame(3,5, new int[]{7,5,7,1,1});
    static Frame DEGREE_FRAME_SMALL =   new Frame(1, 1, new int[] {1});
    static Frame COLON_FRAME_SMALL =    new Frame(3,5, new int[]{0,2,0,2,0});

    static Frame DECIMAL_FRAME_SMALL =  new Frame(1,5,new int[]{0,0,0,0,1});
    static Frame DECIMAL_FRAME =  new Frame(1,7,new int[]{0,0,0,0,0,0,1});
    static Frame DECIMAL_FRAME_LARGE =  new Frame(1,19,new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1});

    static Frame[] NUMBERS_FRAME_SMALL = new Frame[]{ZERO_FRAME_SMALL, ONE_FRAME_SMALL, TWO_FRAME_SMALL, THREE_FRAME_SMALL, FOUR_FRAME_SMALL, FIVE_FRAME_SMALL, SIX_FRAME_SMALL, SEVEN_FRAME_SMALL, EIGHT_FRAME_SMALL, NINE_FRAME_SMALL};
    static Frame EMPTY_FRAME_SMALL = new Frame(3,5, new int[]{0,0,0,0,0});

    //Large 13x19 ASCII Frames
    static private Frame ZERO_FRAME_LARGE        = new Frame(13,19, new int[]{1016,2044,4094,7967,7695,7175,7175,7175,7175,7175,7175,7175,7175,7175,7695,7967,4094,2044,1016,});
    static private Frame ONE_FRAME_LARGE         = new Frame(13,19, new int[]{224,496,1008,2032,4080,240,240,240,240,240,240,240,240,240,240,240,240,240,240,});
    static private Frame TWO_FRAME_LARGE         = new Frame(13,19, new int[]{504,1020,2046,3855,3079,7,15,30,60,120,240,480,960,1920,3840,7168,7168,8190,4095,});
    static private Frame THREE_FRAME_LARGE       = new Frame(13,19, new int[]{1016,2044,4094,31,15,7,15,31,510,508,510,31,15,7,15,31,4094,2044,1016,});
    static private Frame FOUR_FRAME_LARGE        = new Frame(13,19, new int[]{15,31,63,119,231,455,903,1799,3591,7175,8191,4095,7,7,7,7,7,7,7,});
    static private Frame FIVE_FRAME_LARGE        = new Frame(13,19, new int[]{8191,8191,7680,7680,7680,7680,7680,7680,8190,8191,7,7,7,7,6151,7182,3612,4092,2040,});
    static private Frame SIX_FRAME_LARGE         = new Frame(13,19, new int[]{2040,4092,8190,7680,7168,7168,7168,7680,8184,8188,8190,7967,7695,7175,7695,7967,4094,2044,1016,});
    static private Frame SEVEN_FRAME_LARGE       = new Frame(13,19, new int[]{4094,8191,15,7,7,14,30,28,56,56,112,112,224,224,448,448,896,896,896,});
    static private Frame EIGHT_FRAME_LARGE       = new Frame(13,19, new int[]{1016,2044,4094,7967,7695,7175,7695,7967,4094,2044,4094,7967,7695,7175,7695,7967,4094,2044,1016,});
    static private Frame NINE_FRAME_LARGE        = new Frame(13,19, new int[]{1016,2044,4094,7967,7695,7175,7695,7967,4095,2047,511,15,7,7,7,7,7,7,7,});
    static Frame[] NUMBERS_FRAME_LARGE = new Frame[]{ZERO_FRAME_LARGE, ONE_FRAME_LARGE, TWO_FRAME_LARGE, THREE_FRAME_LARGE, FOUR_FRAME_LARGE, FIVE_FRAME_LARGE, SIX_FRAME_LARGE, SEVEN_FRAME_LARGE, EIGHT_FRAME_LARGE, NINE_FRAME_LARGE};
    static Frame COLON_FRAME_LARGE = new Frame(3,19,new int[]{0,0,0,0,0,7,7,7,0,0,0,7,7,7,0,0,0,0,0});
    static Frame EMPTY_FRAME_LARGE = new Frame(13,19, new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
    //Spin1 Motion Frame
    static private Frame SPIN1_0 = new Frame(5,7, new int[]{27,27,27,0,27,27,27});
    static private Frame SPIN1_1 = new Frame(5,7, new int[]{3,3,3,0,27,27,27});
    static private Frame SPIN1_2 = new Frame(5,7, new int[]{24,24,24,0,27,27,27});
    static private Frame SPIN1_3 = new Frame(5,7, new int[]{27,27,27,0,24,24,24});
    static private Frame SPIN1_4 = new Frame(5,7, new int[]{27,27,27,0,3,3,3});
    static Frame[] SPIN1 = new Frame[]{SPIN1_1,SPIN1_2, SPIN1_3,SPIN1_4};
    static Frame PIXEL = new Frame(1,1,new int[]{1});


    private static Frame RAIN_DROP = new Frame(5,7,new int[]{4,14,14,30,29,14,4}, Color.blue);
    private static Frame SNOW_FLAKE = new Frame(3,3, new int[]{2,7,2});

    private static Frame[] CLOUDS = new Frame[]{new Frame(16,9, new int[]{736,2320,45064,28676,32772,32770,32769,16385,16382}),
                                    new Frame(16,9,new int[]{448,560,3088,4104,24592,32784,32776,28792,3968}),
                                    new Frame(16,9,new int[]{0,1719,2176,28736,32816,32784,32784,25480,7288}) };



    static Frame MARIO(){
        Frame mario = new Frame(13,16);
        mario.placeFrame(0,0, new Frame(13,16, new int[]{1008,2046,0,0,0,0,0,1776,3804,7710,1032,0,0,0,0,0}, Color.RED));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,0,0,0,0,0,256,288,480,720,1008,2040,1848,0,0}, Color.BLUE));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,248,1534,1279,1022,1020,0,0,0,6150,7182,6150,0,0,0},  new Color(210,180,140)));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,1792,2560,2816,3072,0,0,0,0,0,0,0,0,3612,7710}, new Color(58,50,39)));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,0,0,0,0,0,0,0,0,288,0,0,0,0,0}, Color.YELLOW));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,16,16,8,30,0,0,0,0,0,0,0,0,0,0}, new Color(1,1,1)));
        mario.writeFrame("mario.gif");
        return mario;
    }

    static Frame RAIN_SCREEN()
    {
        int cols = 16;
        int rows = 32;
        Frame rainScreen = new Frame(cols, rows);
        rainScreen.placeFrame(0,0,RAIN_DROP);
        rainScreen.placeFrame(0,13,RAIN_DROP);
        rainScreen.placeFrame(8,1,RAIN_DROP);
        rainScreen.placeFrame(10,8,RAIN_DROP);
        rainScreen.placeFrame(16,5,RAIN_DROP);
        rainScreen.placeFrame(19,12,RAIN_DROP);
        rainScreen.placeFrame(22,1,RAIN_DROP);
        rainScreen.placeFrame(25,7,RAIN_DROP);
        return rainScreen;

    }

    static Frame CLOUD_SCREEN(){
        Frame cloudScreen = new Frame(50,16);
        cloudScreen.placeFrame(0,0,CLOUDS[1]);
        cloudScreen.placeFrame(0,16,CLOUDS[1]);
        cloudScreen.placeFrame(0,35,CLOUDS[1]);
        return cloudScreen;
    }

    static Frame SNOW_SCREEN(){
        Frame snowScreen = new Frame(16,32);
        snowScreen.placeFrame(12,13,SNOW_FLAKE);
        snowScreen.placeFrame(29,3,PIXEL);
        snowScreen.placeFrame(25,5,PIXEL);
        snowScreen.placeFrame(29,6,PIXEL);
        snowScreen.placeFrame(10,11,PIXEL);
        snowScreen.placeFrame(22,5,PIXEL);
        snowScreen.placeFrame(5,2  ,SNOW_FLAKE);
        snowScreen.placeFrame(0,9,SNOW_FLAKE);
        snowScreen.placeFrame(22,6,PIXEL);
        snowScreen.placeFrame(9,14,PIXEL);
        snowScreen.placeFrame(11,8,SNOW_FLAKE);
        snowScreen.placeFrame(24,7,PIXEL);
        snowScreen.placeFrame(22,2,PIXEL);
        snowScreen.placeFrame(0,5,PIXEL);
        snowScreen.placeFrame(12,5,PIXEL);
        snowScreen.placeFrame(0,4,PIXEL);
        snowScreen.placeFrame(16,15,PIXEL);
        snowScreen.placeFrame(17,0,PIXEL);
        snowScreen.placeFrame(30,12,PIXEL);
        snowScreen.placeFrame(28,0,PIXEL);
        snowScreen.placeFrame(17,11,PIXEL);
        snowScreen.placeFrame(21,15,PIXEL);
        snowScreen.placeFrame(2,5,PIXEL);
        snowScreen.placeFrame(24,3,PIXEL);
        snowScreen.placeFrame(18,14,PIXEL);
        snowScreen.placeFrame(21,13,PIXEL);
        snowScreen.placeFrame(18,7,SNOW_FLAKE);
        snowScreen.placeFrame(25,14,PIXEL);
        snowScreen.placeFrame(10,2,PIXEL);
        snowScreen.placeFrame(27,5,PIXEL);
        return snowScreen;
    }



    protected  String FrameID;
    int length, height;
    private Color[][] pixels;
    private ArrayList<Frame> subFrames;
    int frameNumber = -1;

    public Frame(int length,int height)
    {
        pixels = new Color[height][length];
        subFrames = new ArrayList<Frame>();
        this.length = length;
        this.height = height;

        for(int r=0;r<height;r++)
        {
            for(int c= 0;c<length;c++)
            {
                pixels[r][c] = new Color(0,0,0);
            }
        }
    }

    public Frame(int length, int height, int[] values)
    {
        this(length,height,values, new Color(255,255,255));

    }
    public Frame(int length, int height, int[] values, Color color)
    {
        subFrames = new ArrayList<Frame>();
        pixels = new Color[height][length];
        this.length = length;
        this.height = height;
        for(int r=0;r<height;r++)
        {
            String line = Integer.toBinaryString(values[r]);
            while(line.length() < length)
                line = '0' + line;
            for(int c=0;c<length;c++)
            {
                if (line.charAt(c) == '1')
                    pixels[r][c] = new Color(color.getRGB());
                else
                {
                    pixels[r][c] = new Pixel(0,0,0);
                }
            }
        }
    }
    public void setFrameNumber(int i)
    {
        frameNumber = i;
    }

    Color[][] getPixels()
    {
        return pixels;
    }
    public Color getPixel(int r, int c)
    {
        return pixels[r][c];
    }

    public int getLength()
    {
        return length;
    }

    public int getHeight()
    {
        return height;
    }

    public void placeFrame(int r, int c, Frame f) {
        //subFrames.add(f);
        for (int i = 0; i < f.getHeight();i++)
        {
            for(int j=0;j<f.getLength();j++)
            {
                if(r+i < getHeight() && c+j < getLength())
                    if(f.getPixel(i,j).getGreen() != 0 ||  f.getPixel(i,j).getBlue() != 0 ||f.getPixel(i,j).getRed() != 0 )
                        pixels[r+i][c+j] = f.getPixel(i,j);
            }
        }
    }

    public int getFrameNumber()
    {
        return frameNumber;
    }

    public File writeFrame(String filename)
    {
        BufferedImage image = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getLength(); j++) {
                image.setRGB(j, i, getPixel(i, j).getRGB());
            }
        }

        File outputFile = new File(filename);
        try {
            ImageIO.write(image, "gif", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public static Frame gifToFrame(File file){
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Frame frame = null;
        if (image != null) {
            frame = new Frame(image.getWidth(), image.getHeight());
            int[] color;
            int[] temp = null;
            for (int r = 0; r < image.getWidth(); r++) {
                for (int c = 0; c < image.getHeight(); c++) {
                    frame.placeFrame(r, c, new Frame(1, 1, new int[]{1}, new Color(image.getRGB(c,r), true)));
                }
            }
        }
        return frame;
    }






    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < getHeight();i++)
        {
            for(int j=0;j<getLength();j++)
            {
                if(pixels[i][j].getRed() != 0 || pixels[i][j].getBlue() != 0 || pixels[i][j].getGreen() != 0)
                    s+='1';
                else
                    s+=' ';
            }
            s+="\n";
        }
        return s;
    }

    public static void main(String args[])
    {
        System.out.println("test bby");
    }











}
=======
package system.clockDisplay.imageManagement;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Willi on 9/30/2016.
 */
public class Frame {
    //http://www.quinapalus.com/hd44780udg.html

    //Medium Ascii 5x7 Standard Characters
    static private Frame ZERO_FRAME = new Frame(5, 7, new int[]{14, 27, 27, 27, 27, 27, 14});
    static private Frame ONE_FRAME = new Frame(5, 7, new int[]{2,6,14,6,6,6,6});
    static private Frame TWO_FRAME =  new Frame(5, 7, new int[]{14,27,3,6,12,24,31});
    static private Frame THREE_FRAME =   new Frame(5, 7, new int[]{14,27,3,14,3,27,14});
    static private Frame FOUR_FRAME =  new Frame(5, 7, new int[]{3,7,15,27,31,3,3});
    static private Frame FIVE_FRAME =  new Frame(5, 7, new int[]{31,24,30,3,3,27,14});
    static private Frame SIX_FRAME =  new Frame(5, 7, new int[]{14,27,24,30,27,27,14});
    static private Frame SEVEN_FRAME =  new Frame(5, 7, new int[]{31,3,6,12,12,12,12});
    static private Frame EIGHT_FRAME =  new Frame(5, 7, new int[]{14,27,27,14,27,27,14});
    static private Frame NINE_FRAME =  new Frame(5, 7, new int[]{14,27,27,15,3,27,14});
    static protected Frame DEGREE_FRAME =  new Frame(3, 3, new int[]{2,5,2});
    static protected Frame COLON_FRAME = new Frame(5,7, new int[]{0,12,12,0,12,12,0});
    static protected Frame[] NUMBERS_FRAME = new Frame[]{ZERO_FRAME, ONE_FRAME, TWO_FRAME, THREE_FRAME, FOUR_FRAME, FIVE_FRAME, SIX_FRAME, SEVEN_FRAME, EIGHT_FRAME, NINE_FRAME};
    static protected Frame EMPTY_FRAME = new Frame(5,7, new int[]{0,0,0,0,0,0,0});
    static Frame MINUS_FRAME = new Frame(5,7,new int[]{0,0,0,7,0,0,0});


    //Small ASCII 3 x 5
    static private Frame ZERO_FRAME_SMALL =     new Frame(3, 5, new int[]{7,5,5,5,7});
    static private Frame ONE_FRAME_SMALL =      new Frame(2, 5, new int[]{1,1,1,1,1});
    static private Frame TWO_FRAME_SMALL =      new Frame(3, 5, new int[]{7,1,7,4,7});
    static private Frame THREE_FRAME_SMALL =    new Frame(3, 5, new int[]{7,1,7,1,7});
    static private Frame FOUR_FRAME_SMALL =     new Frame(3, 5, new int[]{5,5,7,1,1});
    static private Frame FIVE_FRAME_SMALL =     new Frame(3, 5, new int[]{7,4,7,1,7});
    static private Frame SIX_FRAME_SMALL =      new Frame(3, 5, new int[]{7,4,7,5,7});
    static private Frame SEVEN_FRAME_SMALL =    new Frame(3, 5, new int[]{7,1,1,1,1});
    static private Frame EIGHT_FRAME_SMALL =    new Frame(3, 5, new int[]{7,5,7,5,7});
    static private Frame NINE_FRAME_SMALL =     new Frame(3,5, new int[]{7,5,7,1,1});
    static Frame DEGREE_FRAME_SMALL =   new Frame(1, 1, new int[] {1});
    static Frame COLON_FRAME_SMALL =    new Frame(3,5, new int[]{0,2,0,2,0});

    static Frame DECIMAL_FRAME_SMALL =  new Frame(1,5,new int[]{0,0,0,0,1});
    static Frame DECIMAL_FRAME =  new Frame(1,7,new int[]{0,0,0,0,0,0,1});
    static Frame DECIMAL_FRAME_LARGE =  new Frame(1,19,new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1});

    static Frame[] NUMBERS_FRAME_SMALL = new Frame[]{ZERO_FRAME_SMALL, ONE_FRAME_SMALL, TWO_FRAME_SMALL, THREE_FRAME_SMALL, FOUR_FRAME_SMALL, FIVE_FRAME_SMALL, SIX_FRAME_SMALL, SEVEN_FRAME_SMALL, EIGHT_FRAME_SMALL, NINE_FRAME_SMALL};
    static Frame EMPTY_FRAME_SMALL = new Frame(3,5, new int[]{0,0,0,0,0});

    //Large 13x19 ASCII Frames
    static private Frame ZERO_FRAME_LARGE        = new Frame(13,19, new int[]{1016,2044,4094,7967,7695,7175,7175,7175,7175,7175,7175,7175,7175,7175,7695,7967,4094,2044,1016,});
    static private Frame ONE_FRAME_LARGE         = new Frame(13,19, new int[]{224,496,1008,2032,4080,240,240,240,240,240,240,240,240,240,240,240,240,240,240,});
    static private Frame TWO_FRAME_LARGE         = new Frame(13,19, new int[]{504,1020,2046,3855,3079,7,15,30,60,120,240,480,960,1920,3840,7168,7168,8190,4095,});
    static private Frame THREE_FRAME_LARGE       = new Frame(13,19, new int[]{1016,2044,4094,31,15,7,15,31,510,508,510,31,15,7,15,31,4094,2044,1016,});
    static private Frame FOUR_FRAME_LARGE        = new Frame(13,19, new int[]{15,31,63,119,231,455,903,1799,3591,7175,8191,4095,7,7,7,7,7,7,7,});
    static private Frame FIVE_FRAME_LARGE        = new Frame(13,19, new int[]{8191,8191,7680,7680,7680,7680,7680,7680,8190,8191,7,7,7,7,6151,7182,3612,4092,2040,});
    static private Frame SIX_FRAME_LARGE         = new Frame(13,19, new int[]{2040,4092,8190,7680,7168,7168,7168,7680,8184,8188,8190,7967,7695,7175,7695,7967,4094,2044,1016,});
    static private Frame SEVEN_FRAME_LARGE       = new Frame(13,19, new int[]{4094,8191,15,7,7,14,30,28,56,56,112,112,224,224,448,448,896,896,896,});
    static private Frame EIGHT_FRAME_LARGE       = new Frame(13,19, new int[]{1016,2044,4094,7967,7695,7175,7695,7967,4094,2044,4094,7967,7695,7175,7695,7967,4094,2044,1016,});
    static private Frame NINE_FRAME_LARGE        = new Frame(13,19, new int[]{1016,2044,4094,7967,7695,7175,7695,7967,4095,2047,511,15,7,7,7,7,7,7,7,});
    static Frame[] NUMBERS_FRAME_LARGE = new Frame[]{ZERO_FRAME_LARGE, ONE_FRAME_LARGE, TWO_FRAME_LARGE, THREE_FRAME_LARGE, FOUR_FRAME_LARGE, FIVE_FRAME_LARGE, SIX_FRAME_LARGE, SEVEN_FRAME_LARGE, EIGHT_FRAME_LARGE, NINE_FRAME_LARGE};
    static Frame COLON_FRAME_LARGE = new Frame(3,19,new int[]{0,0,0,0,0,7,7,7,0,0,0,7,7,7,0,0,0,0,0});
    static Frame EMPTY_FRAME_LARGE = new Frame(13,19, new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
    //Spin1 Motion Frame
    static private Frame SPIN1_0 = new Frame(5,7, new int[]{27,27,27,0,27,27,27});
    static private Frame SPIN1_1 = new Frame(5,7, new int[]{3,3,3,0,27,27,27});
    static private Frame SPIN1_2 = new Frame(5,7, new int[]{24,24,24,0,27,27,27});
    static private Frame SPIN1_3 = new Frame(5,7, new int[]{27,27,27,0,24,24,24});
    static private Frame SPIN1_4 = new Frame(5,7, new int[]{27,27,27,0,3,3,3});
    static Frame[] SPIN1 = new Frame[]{SPIN1_1,SPIN1_2, SPIN1_3,SPIN1_4};
    static Frame PIXEL = new Frame(1,1,new int[]{1});


    private static Frame RAIN_DROP = new Frame(5,7,new int[]{4,14,14,30,29,14,4}, Color.blue);
    private static Frame SNOW_FLAKE = new Frame(3,3, new int[]{2,7,2});

    private static Frame[] CLOUDS = new Frame[]{new Frame(16,9, new int[]{736,2320,45064,28676,32772,32770,32769,16385,16382}),
                                    new Frame(16,9,new int[]{448,560,3088,4104,24592,32784,32776,28792,3968}),
                                    new Frame(16,9,new int[]{0,1719,2176,28736,32816,32784,32784,25480,7288}) };



    static Frame MARIO(){
        Frame mario = new Frame(13,16);
        mario.placeFrame(0,0, new Frame(13,16, new int[]{1008,2046,0,0,0,0,0,1776,3804,7710,1032,0,0,0,0,0}, Color.RED));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,0,0,0,0,0,256,288,480,720,1008,2040,1848,0,0}, Color.BLUE));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,248,1534,1279,1022,1020,0,0,0,6150,7182,6150,0,0,0},  new Color(210,180,140)));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,1792,2560,2816,3072,0,0,0,0,0,0,0,0,3612,7710}, new Color(58,50,39)));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,0,0,0,0,0,0,0,0,288,0,0,0,0,0}, Color.YELLOW));
        mario.placeFrame(0,0, new Frame(13,16, new int[]{0,0,16,16,8,30,0,0,0,0,0,0,0,0,0,0}, new Color(1,1,1)));
        mario.writeFrame("mario.gif");
        return mario;
    }

    static Frame RAIN_SCREEN()
    {
        int cols = 16;
        int rows = 32;
        Frame rainScreen = new Frame(cols, rows);
        rainScreen.placeFrame(0,0,RAIN_DROP);
        rainScreen.placeFrame(0,13,RAIN_DROP);
        rainScreen.placeFrame(8,1,RAIN_DROP);
        rainScreen.placeFrame(10,8,RAIN_DROP);
        rainScreen.placeFrame(16,5,RAIN_DROP);
        rainScreen.placeFrame(19,12,RAIN_DROP);
        rainScreen.placeFrame(22,1,RAIN_DROP);
        rainScreen.placeFrame(25,7,RAIN_DROP);
        return rainScreen;

    }

    static Frame CLOUD_SCREEN(){
        Frame cloudScreen = new Frame(50,16);
        cloudScreen.placeFrame(0,0,CLOUDS[1]);
        cloudScreen.placeFrame(0,16,CLOUDS[1]);
        cloudScreen.placeFrame(0,35,CLOUDS[1]);
        return cloudScreen;
    }

    static Frame SNOW_SCREEN(){
        Frame snowScreen = new Frame(16,32);
        snowScreen.placeFrame(12,13,SNOW_FLAKE);
        snowScreen.placeFrame(29,3,PIXEL);
        snowScreen.placeFrame(25,5,PIXEL);
        snowScreen.placeFrame(29,6,PIXEL);
        snowScreen.placeFrame(10,11,PIXEL);
        snowScreen.placeFrame(22,5,PIXEL);
        snowScreen.placeFrame(5,2  ,SNOW_FLAKE);
        snowScreen.placeFrame(0,9,SNOW_FLAKE);
        snowScreen.placeFrame(22,6,PIXEL);
        snowScreen.placeFrame(9,14,PIXEL);
        snowScreen.placeFrame(11,8,SNOW_FLAKE);
        snowScreen.placeFrame(24,7,PIXEL);
        snowScreen.placeFrame(22,2,PIXEL);
        snowScreen.placeFrame(0,5,PIXEL);
        snowScreen.placeFrame(12,5,PIXEL);
        snowScreen.placeFrame(0,4,PIXEL);
        snowScreen.placeFrame(16,15,PIXEL);
        snowScreen.placeFrame(17,0,PIXEL);
        snowScreen.placeFrame(30,12,PIXEL);
        snowScreen.placeFrame(28,0,PIXEL);
        snowScreen.placeFrame(17,11,PIXEL);
        snowScreen.placeFrame(21,15,PIXEL);
        snowScreen.placeFrame(2,5,PIXEL);
        snowScreen.placeFrame(24,3,PIXEL);
        snowScreen.placeFrame(18,14,PIXEL);
        snowScreen.placeFrame(21,13,PIXEL);
        snowScreen.placeFrame(18,7,SNOW_FLAKE);
        snowScreen.placeFrame(25,14,PIXEL);
        snowScreen.placeFrame(10,2,PIXEL);
        snowScreen.placeFrame(27,5,PIXEL);
        return snowScreen;
    }



    protected  String FrameID;
    int length, height;
    private Color[][] pixels;
    private ArrayList<Frame> subFrames;
    int frameNumber = -1;

    public Frame(int length,int height)
    {
        pixels = new Color[height][length];
        subFrames = new ArrayList<Frame>();
        this.length = length;
        this.height = height;

        for(int r=0;r<height;r++)
        {
            for(int c= 0;c<length;c++)
            {
                pixels[r][c] = new Color(0,0,0);
            }
        }
    }

    public Frame(int length, int height, int[] values)
    {
        this(length,height,values, new Color(255,255,255));

    }
    public Frame(int length, int height, int[] values, Color color)
    {
        subFrames = new ArrayList<Frame>();
        pixels = new Color[height][length];
        this.length = length;
        this.height = height;
        for(int r=0;r<height;r++)
        {
            String line = Integer.toBinaryString(values[r]);
            while(line.length() < length)
                line = '0' + line;
            for(int c=0;c<length;c++)
            {
                if (line.charAt(c) == '1')
                    pixels[r][c] = new Color(color.getRGB());
                else
                {
                    pixels[r][c] = new Pixel(0,0,0);
                }
            }
        }
    }
    public void setFrameNumber(int i)
    {
        frameNumber = i;
    }

    Color[][] getPixels()
    {
        return pixels;
    }
    public Color getPixel(int r, int c)
    {
        return pixels[r][c];
    }

    public int getLength()
    {
        return length;
    }

    public int getHeight()
    {
        return height;
    }

    public void placeFrame(int r, int c, Frame f) {
        //subFrames.add(f);
        for (int i = 0; i < f.getHeight();i++)
        {
            for(int j=0;j<f.getLength();j++)
            {
                if(r+i < getHeight() && c+j < getLength())
                    if(f.getPixel(i,j).getGreen() != 0 ||  f.getPixel(i,j).getBlue() != 0 ||f.getPixel(i,j).getRed() != 0 )
                        pixels[r+i][c+j] = f.getPixel(i,j);
            }
        }
    }

    public int getFrameNumber()
    {
        return frameNumber;
    }

    public File writeFrame(String filename)
    {
        BufferedImage image = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < getHeight(); i++) {
            for (int j = 0; j < getLength(); j++) {
                image.setRGB(j, i, getPixel(i, j).getRGB());
            }
        }

        File outputFile = new File(filename);
        try {
            ImageIO.write(image, "gif", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputFile;
    }

    public static Frame gifToFrame(File file){
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Frame frame = null;
        if (image != null) {
            frame = new Frame(image.getWidth(), image.getHeight());
            int[] color;
            int[] temp = null;
            for (int r = 0; r < image.getWidth(); r++) {
                for (int c = 0; c < image.getHeight(); c++) {
                    frame.placeFrame(r, c, new Frame(1, 1, new int[]{1}, new Color(image.getRGB(c,r), true)));
                }
            }
        }
        return frame;
    }






    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < getHeight();i++)
        {
            for(int j=0;j<getLength();j++)
            {
                if(pixels[i][j].getRed() != 0 || pixels[i][j].getBlue() != 0 || pixels[i][j].getGreen() != 0)
                    s+='1';
                else
                    s+=' ';
            }
            s+="\n";
        }
        return s;
    }

    public static void main(String args[])
    {
        System.out.println("test bby");
    }











}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/imageManagement/Frame.java
