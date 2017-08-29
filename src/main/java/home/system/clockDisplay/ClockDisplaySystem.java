
package home.system.clockDisplay;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import home.controller.Engine;
import home.parcel.Parcel;
import home.parcel.SystemException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import home.system.clockDisplay.displayElements.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import home.system.clockDisplay.imageManagement.Frame;
import home.system.clockDisplay.imageManagement.GifSequenceWriter;
import home.system.clockDisplay.imageManagement.LayerManager;
import home.system.clockDisplay.imageManagement.SpriteDict;
import home.system.SystemParent;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * The System level clock display
 * manages a set of display elements, writes the resource gif
 * Also does the /set&get
 */
@Component
@EnableScheduling
public class ClockDisplaySystem extends SystemParent{

   /*
   So get request is going to just get the rgb values for what the clock needs to display
   Right now the sever and the clock are going to be running on the same lcoalhost so maybe liek 5 times a second update()

    */

    public static final String systemIdentifier = "clock";

    private int rows = 32;
    private int cols = 96;
    private SpriteDict spriteDict;
    private ArrayList<DisplayElement> elements = new ArrayList<>();
    private File resourceGif;
    private LayerManager layerManager;

    public ClockDisplaySystem( Engine e)
    {
        super(systemIdentifier, e,3000);
        spriteDict = new SpriteDict();
        try {
            writeResourceGif();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        layerManager = new LayerManager();
        elements.add(new ClockElement("clock", this, 2,0,18,new SimpleDateFormat("h:mm"), 5));
        //elements.add(new WeatherElement("weather", this,8,23,18, 20000,e));
        //elements.add(new HVACMotionElement("hvac-mon", this, 25,89));

        update();

    }

    @Override
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")){
            case "get":
                return get(p);
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }
    }


    public Parcel get(Parcel p){
        try {
            switch (p.getString("what")){
                case "resourceImage":
                    try {
                        return Parcel.RESPONSE_PARCEL(getResourceGif(), true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                case "imageStart":
                    return Parcel.RESPONSE_PARCEL(getImageUpdate(p.getLong("t1"), p.getLong("t2"), p.getLong("interval"), true), false);
                case "imageUpdate":
                    return Parcel.RESPONSE_PARCEL(getImageUpdate(p.getLong("t1"), p.getLong("t2"), p.getLong("interval"), false), false);
            }
            throw SystemException.WHAT_NOT_SUPPORTED(p);

        } catch (SystemException e) {
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        }
    }

    private String getImageUpdate(long start, long stop, long interval, boolean fullImage){

        JsonObject imageUpdate = new JsonObject();
        JsonArray frames = new JsonArray();
        System.out.println(Engine.timestamp() + new SimpleDateFormat(" HH:mm:ss\t").format(start));
        for(long i=start; i<stop; i+=interval){
            JsonObject time = new JsonObject();
            JsonArray eles = new JsonArray();
            JsonObject[] eleArray = new JsonObject[]{};
            for (DisplayElement ele: elements) {
                if(fullImage ) {
                    eleArray = ele.get(i);
                }
                else if((i-1)%ele.getUpdateInterval() >= (i+interval-1)%ele.getUpdateInterval()){
                    eleArray = ele.get(i);
                }
                for(int j=0;j<eleArray.length;j++)                {
                    eles.add(eleArray[j]);
                }
            }
            if(eles.size() != 0) {

                time.add("elements", eles);
                time.addProperty("time", i);
                frames.add(time);
            }
        }
        imageUpdate.add("frames",frames);
        imageUpdate.addProperty("start",start);
        imageUpdate.addProperty("stop", stop);
        imageUpdate.addProperty("interval", interval);
        imageUpdate.addProperty("alpha",10);
        System.out.println(imageUpdate.toString());
        return imageUpdate.toString();
    }

    private Object getResourceGif() throws FileNotFoundException {
        File image = resourceGif;


        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");


        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(image.length())
                .contentType(MediaType.IMAGE_GIF)
                .body(new InputStreamResource(new FileInputStream(image)));


    }

    private void writeResourceGif() throws IOException {
        resourceGif = new File("resources.gif");
        ImageOutputStream output = new FileImageOutputStream(resourceGif);
        GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB,1, false);
        int currentFrame = 0;
        for (String key : spriteDict.keySet()) {
            for (Frame f: spriteDict.get(key).getFrames() ) {
                f.setFrameNumber(currentFrame++);
                BufferedImage img = new BufferedImage(cols, rows, BufferedImage.TYPE_INT_RGB);
                for (int i = 0; i < f.getHeight(); i++) {
                    for (int j = 0; j < f.getLength(); j++) {
                        img.setRGB(j, i, f.getPixel(i, j).getRGB());
                    }
                }
                writer.writeToSequence(img);
            }
        }
        writer.close();
        output.close();
    }

    public SpriteDict getSpriteDict()
    {
        return spriteDict;
    }

    public LayerManager getLayoutManager()
    {
        return layerManager;
    }


    @Override
    public void update() {
        int k = 0;

        for(int i=0;i<elements.size();i++)
        {
            elements.get(i).update();
        }
    }
    public static void main(String[] args)
    {
        ClockDisplaySystem system = new ClockDisplaySystem(null);
        try {
            system.writeResourceGif();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}