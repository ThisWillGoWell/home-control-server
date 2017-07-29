<<<<<<< HEAD:src/main/java/system/clockDisplay/displayElements/WeatherElement.java
package system.clockDisplay.displayElements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.Engine;
import parcel.ParcelArray;
import parcel.SystemException;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;
import system.weather.WeatherParcel;

import java.util.ArrayList;

/**
 * Created by Willi on 10/1/2016.
 */

public class WeatherElement extends DisplayElement{

    private Engine e;
    private int numRows;
    private int writePointerRow;
    private int writePointerCol;
    private ParcelArray forecast;
    private double currentTemp;
    private long tempUpdateRate;
    private ArrayList<Double> temps;
    private ArrayList<String> conditions = new ArrayList<>();
    private ArrayList<Long> times = new ArrayList<>();
    private double minTemp, maxTemp,tempTemp;
    private  String tempDisplayID = "tempDisplay";
    private  String tempGraphID = "tempGraph";
    private String weatherImageID = "weatherImage";

    private JsonObject graphJson;
    private JsonObject currentTempJson;
    private JsonObject maxTempJson;
    private JsonObject minTempJson;

    private JsonObject weatherImageJson;

    String currentIcon;


    public WeatherElement(String id, ClockDisplaySystem clockDisplaySystem, int size, int row, int col, long updateInterval, Engine e){
        super(id, clockDisplaySystem,size, row, col, updateInterval);
        this.e = e;
        numRows = size;
        writePointerRow = row;
        writePointerCol = col;
        tempUpdateRate = 5000;
        temps = new ArrayList<>();
        conditions = new ArrayList<>();
        times = new ArrayList<>();

        tempDisplayID = id + "-"+tempDisplayID;
        tempGraphID = id + "-"+tempGraphID;
        weatherImageID = id + "-" + weatherImageID;

        layerManager.addLayer(tempDisplayID);
        layerManager.addLayer(tempGraphID);
        layerManager.addLayer(weatherImageID);

        graphJson = new JsonObject();
        graphJson.addProperty("id", tempGraphID);
        graphJson.add("fill", fill());
        graphJson.addProperty("l", layerManager.get(tempGraphID));

        currentTempJson = new JsonObject();
        currentTempJson.addProperty("id", tempDisplayID);
        currentTempJson.add("fill", fill(255,255,255,255));
        currentTempJson.addProperty("l", layerManager.get(tempDisplayID));

        maxTempJson = new JsonObject();
        maxTempJson.addProperty("id", tempDisplayID);
        maxTempJson.add("fill", fill(255,0,0,255));
        maxTempJson.addProperty("l", layerManager.get(tempDisplayID));

        minTempJson = new JsonObject();
        minTempJson.addProperty("id", tempDisplayID);
        minTempJson.add("fill", fill(0,0,255,255));
        minTempJson.addProperty("l", layerManager.get(tempDisplayID));

        weatherImageJson = new JsonObject();
        weatherImageJson.addProperty("id", weatherImageID);
        weatherImageJson.add("fill", fill());
        weatherImageJson.addProperty("l",layerManager.get(weatherImageID));

    }


    private void updateWeatherImage(){
        JsonObject frame = new JsonObject();
        frame.addProperty("w", 16);
        frame.addProperty("h", 16);
        frame.addProperty("r", 8);
        frame.addProperty("c", 4);
        switch (currentIcon){
            default:
                frame.addProperty("n",spriteDict.get("mario").getFrames().get(0).getFrameNumber());
                break;
            case "cloudy":
            case "nt_cloudy":
            case "nt_mostlycloudy":
            case "mostlycloudy":
                frame.addProperty("n",spriteDict.get("cloudy").getFrames().get(0).getFrameNumber());
            break;

            case "rain":
            case "nt_rain":
                frame.addProperty("n",spriteDict.get("rain").getFrames().get(0).getFrameNumber());
                break;

            case "clear":
                frame.addProperty("n",spriteDict.get("clear").getFrames().get(0).getFrameNumber());
                break;

            case "nt_clear":
                frame.addProperty("n",spriteDict.get("nt_clear").getFrames().get(0).getFrameNumber());
                break;

            case "snow":
            case "nt_snow":
                frame.addProperty("n",spriteDict.get("snow").getFrames().get(0).getFrameNumber());
                break;

        }
        JsonArray frames = new JsonArray();
        frames.add(frame);
        weatherImageJson.add("f",frames);

    }


    private void updateGraphElement()
    {
        //have access to hourly data
        //~40 pixels
        //get the most recent forecast from the weather subsystem
        //we have all the data now we need to compute the graph.
        //for this one, lets say the
        double tempPerPixel = numRows / (maxTemp - minTemp);
        JsonArray graph = new JsonArray();
        JsonObject currentPoint;
        for(int i = 0;i<temps.size();i++)
        {
            currentPoint = new JsonObject();
            currentPoint.addProperty("r",Math.round(writePointerRow + tempPerPixel * (maxTemp - temps.get(i))));
            currentPoint.addProperty("c",writePointerCol);
            currentPoint.addProperty("n",spriteDict.get("pixel").getFrames().get(0).getFrameNumber());
            currentPoint.addProperty("w",spriteDict.get("pixel").getFrames().get(0).getLength());
            currentPoint.addProperty("h",spriteDict.get("pixel").getFrames().get(0).getHeight());
            writePointerCol += 1;
            graph.add(currentPoint);
        }
        graphJson.add("f",graph);
    }

    private void updateTempElement()
    {
        double[] temps = new double[]{currentTemp,minTemp, maxTemp};
        JsonObject[] jsons = new JsonObject[]{currentTempJson, minTempJson,maxTempJson};
        int tempWriter = writePointerCol;
        for(int j=0;j<3;j++)        {
            String tempString = ((int) temps[j]) + "";
            while(tempString.length() < 2){
                tempString = " " + tempString;
            }
            tempString = tempString.substring(0,2);
            tempWriter = writePointerCol;
            JsonArray frames = new JsonArray();
            Frame s = null;
            for (int i = 0; i < tempString.length(); i++) {
                JsonObject frameData = new JsonObject();
                s = spriteDict.get(tempString.charAt(i) + "").getFrames().get(1);
                frameData.addProperty("n", s.getFrameNumber());
                frameData.addProperty("w", s.getHeight());
                frameData.addProperty("h", s.getHeight());
                frameData.addProperty("r", writePointerRow);
                frameData.addProperty("c", tempWriter);
                frames.add(frameData);
                tempWriter += s.getLength();
            }
            JsonObject frameData = new JsonObject();
            s = spriteDict.get("degree").getFrames().get(1);
            frameData.addProperty("n", s.getFrameNumber());
            frameData.addProperty("w", s.getLength());
            frameData.addProperty("h", s.getHeight());
            frameData.addProperty("r", writePointerRow);
            frameData.addProperty("c", tempWriter);
            frames.add(frameData);
            tempWriter += s.getLength()+ 1;
            jsons[j].add("f",frames);
        }
        writePointerCol = tempWriter;
    }

    @Override
    public JsonObject[] get(long time) {
        //so the update time is going to be the same time as the update interval of the weather object.
        //but bascially I think I want it to be quicker than that..... or do I?

        if(time / updateInterval % 10 == 0)
            return new JsonObject[]{minTempJson, graphJson,weatherImageJson};
        else if (time/updateInterval%10==5)
            return new JsonObject[]{maxTempJson, graphJson,weatherImageJson};
        else
            return new JsonObject[]{currentTempJson,graphJson,weatherImageJson};

    }

    @Override
    public void update() {

        try {
            forecast = e.digestParcel(WeatherParcel.GET_HOUR_FORECAST()).getParcel("payload").getParcelArray("hourly_forecast");
            currentTemp = e.digestParcel(WeatherParcel.GET_CURRENT_TEMP()).getDouble("payload");
            currentIcon = e.digestParcel(WeatherParcel.GET_CURRENT_ICON()).getString("payload");
            minTemp = maxTemp = currentTemp;
            writePointerRow = row;
            writePointerCol = col;

            temps = new ArrayList<>();
            conditions = new ArrayList<>();
            times = new ArrayList<>();
            if (forecast != null) {
                for (int i = 0; i < forecast.size(); i++) {
                    //hourly_forecast[] -> FCTTIME -> epoch
                    //hourly_forecast[] -> temp -> metric
                    //hourly_forecast[] -> condition
                    tempTemp = forecast.getParcel(i).getParcel("temp").getDouble("metric");

                    if (minTemp > tempTemp) {
                        minTemp = tempTemp;
                    } else if (maxTemp < tempTemp) {
                        maxTemp = tempTemp;
                    }
                    times.add(forecast.getParcel(i).getParcel("FCTTIME").getLong("epoch"));
                    temps.add(tempTemp);
                    conditions.add(forecast.getParcel(i).getString("icon"));
                }
                updateTempElement();
                updateGraphElement();
                updateWeatherImage();
            }
        }
        catch (SystemException e1) {
            e1.printStackTrace();
        }

    }


}
=======
package system.clockDisplay.displayElements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import controller.Engine;
import parcel.ParcelArray;
import parcel.SystemException;
import system.clockDisplay.ClockDisplaySystem;
import system.clockDisplay.imageManagement.Frame;
import system.weather.WeatherParcel;

import java.util.ArrayList;

/**
 * Created by Willi on 10/1/2016.
 */

public class WeatherElement extends DisplayElement{

    private Engine e;
    private int numRows;
    private int writePointerRow;
    private int writePointerCol;
    private ParcelArray forecast;
    private double currentTemp;
    private long tempUpdateRate;
    private ArrayList<Double> temps;
    private ArrayList<String> conditions = new ArrayList<>();
    private ArrayList<Long> times = new ArrayList<>();
    private double minTemp, maxTemp,tempTemp;
    private  String tempDisplayID = "tempDisplay";
    private  String tempGraphID = "tempGraph";
    private String weatherImageID = "weatherImage";

    private JsonObject graphJson;
    private JsonObject currentTempJson;
    private JsonObject maxTempJson;
    private JsonObject minTempJson;

    private JsonObject weatherImageJson;

    String currentIcon;


    public WeatherElement(String id, ClockDisplaySystem clockDisplaySystem, int size, int row, int col, long updateInterval, Engine e){
        super(id, clockDisplaySystem,size, row, col, updateInterval);
        this.e = e;
        numRows = size;
        writePointerRow = row;
        writePointerCol = col;
        tempUpdateRate = 5000;
        temps = new ArrayList<>();
        conditions = new ArrayList<>();
        times = new ArrayList<>();

        tempDisplayID = id + "-"+tempDisplayID;
        tempGraphID = id + "-"+tempGraphID;
        weatherImageID = id + "-" + weatherImageID;

        layerManager.addLayer(tempDisplayID);
        layerManager.addLayer(tempGraphID);
        layerManager.addLayer(weatherImageID);

        graphJson = new JsonObject();
        graphJson.addProperty("id", tempGraphID);
        graphJson.add("fill", fill());
        graphJson.addProperty("l", layerManager.get(tempGraphID));

        currentTempJson = new JsonObject();
        currentTempJson.addProperty("id", tempDisplayID);
        currentTempJson.add("fill", fill(255,255,255,255));
        currentTempJson.addProperty("l", layerManager.get(tempDisplayID));

        maxTempJson = new JsonObject();
        maxTempJson.addProperty("id", tempDisplayID);
        maxTempJson.add("fill", fill(255,0,0,255));
        maxTempJson.addProperty("l", layerManager.get(tempDisplayID));

        minTempJson = new JsonObject();
        minTempJson.addProperty("id", tempDisplayID);
        minTempJson.add("fill", fill(0,0,255,255));
        minTempJson.addProperty("l", layerManager.get(tempDisplayID));

        weatherImageJson = new JsonObject();
        weatherImageJson.addProperty("id", weatherImageID);
        weatherImageJson.add("fill", fill());
        weatherImageJson.addProperty("l",layerManager.get(weatherImageID));

    }


    private void updateWeatherImage(){
        JsonObject frame = new JsonObject();
        frame.addProperty("w", 16);
        frame.addProperty("h", 16);
        frame.addProperty("r", 8);
        frame.addProperty("c", 4);
        switch (currentIcon){
            default:
                frame.addProperty("n",spriteDict.get("mario").getFrames().get(0).getFrameNumber());
                break;
            case "cloudy":
            case "nt_cloudy":
            case "nt_mostlycloudy":
            case "mostlycloudy":
                frame.addProperty("n",spriteDict.get("cloudy").getFrames().get(0).getFrameNumber());
            break;

            case "rain":
            case "nt_rain":
                frame.addProperty("n",spriteDict.get("rain").getFrames().get(0).getFrameNumber());
                break;

            case "clear":
                frame.addProperty("n",spriteDict.get("clear").getFrames().get(0).getFrameNumber());
                break;

            case "nt_clear":
                frame.addProperty("n",spriteDict.get("nt_clear").getFrames().get(0).getFrameNumber());
                break;

            case "snow":
            case "nt_snow":
                frame.addProperty("n",spriteDict.get("snow").getFrames().get(0).getFrameNumber());
                break;

        }
        JsonArray frames = new JsonArray();
        frames.add(frame);
        weatherImageJson.add("f",frames);

    }


    private void updateGraphElement()
    {
        //have access to hourly data
        //~40 pixels
        //get the most recent forecast from the weather subsystem
        //we have all the data now we need to compute the graph.
        //for this one, lets say the
        double tempPerPixel = numRows / (maxTemp - minTemp);
        JsonArray graph = new JsonArray();
        JsonObject currentPoint;
        for(int i = 0;i<temps.size();i++)
        {
            currentPoint = new JsonObject();
            currentPoint.addProperty("r",Math.round(writePointerRow + tempPerPixel * (maxTemp - temps.get(i))));
            currentPoint.addProperty("c",writePointerCol);
            currentPoint.addProperty("n",spriteDict.get("pixel").getFrames().get(0).getFrameNumber());
            currentPoint.addProperty("w",spriteDict.get("pixel").getFrames().get(0).getLength());
            currentPoint.addProperty("h",spriteDict.get("pixel").getFrames().get(0).getHeight());
            writePointerCol += 1;
            graph.add(currentPoint);
        }
        graphJson.add("f",graph);
    }

    private void updateTempElement()
    {
        double[] temps = new double[]{currentTemp,minTemp, maxTemp};
        JsonObject[] jsons = new JsonObject[]{currentTempJson, minTempJson,maxTempJson};
        int tempWriter = writePointerCol;
        for(int j=0;j<3;j++)        {
            String tempString = ((int) temps[j]) + "";
            while(tempString.length() < 2){
                tempString = " " + tempString;
            }
            tempString = tempString.substring(0,2);
            tempWriter = writePointerCol;
            JsonArray frames = new JsonArray();
            Frame s = null;
            for (int i = 0; i < tempString.length(); i++) {
                JsonObject frameData = new JsonObject();
                s = spriteDict.get(tempString.charAt(i) + "").getFrames().get(1);
                frameData.addProperty("n", s.getFrameNumber());
                frameData.addProperty("w", s.getHeight());
                frameData.addProperty("h", s.getHeight());
                frameData.addProperty("r", writePointerRow);
                frameData.addProperty("c", tempWriter);
                frames.add(frameData);
                tempWriter += s.getLength();
            }
            JsonObject frameData = new JsonObject();
            s = spriteDict.get("degree").getFrames().get(1);
            frameData.addProperty("n", s.getFrameNumber());
            frameData.addProperty("w", s.getLength());
            frameData.addProperty("h", s.getHeight());
            frameData.addProperty("r", writePointerRow);
            frameData.addProperty("c", tempWriter);
            frames.add(frameData);
            tempWriter += s.getLength()+ 1;
            jsons[j].add("f",frames);
        }
        writePointerCol = tempWriter;
    }

    @Override
    public JsonObject[] get(long time) {
        //so the update time is going to be the same time as the update interval of the weather object.
        //but bascially I think I want it to be quicker than that..... or do I?

        if(time / updateInterval % 10 == 0)
            return new JsonObject[]{minTempJson, graphJson,weatherImageJson};
        else if (time/updateInterval%10==5)
            return new JsonObject[]{maxTempJson, graphJson,weatherImageJson};
        else
            return new JsonObject[]{currentTempJson,graphJson,weatherImageJson};

    }

    @Override
    public void update() {

        try {
            forecast = e.command(WeatherParcel.GET_HOUR_FORECAST()).getParcel("payload").getParcelArray("hourly_forecast");
            currentTemp = e.command(WeatherParcel.GET_CURRENT_TEMP()).getDouble("payload");
            currentIcon = e.command(WeatherParcel.GET_CURRENT_ICON()).getString("payload");
            minTemp = maxTemp = currentTemp;
            writePointerRow = row;
            writePointerCol = col;

            temps = new ArrayList<>();
            conditions = new ArrayList<>();
            times = new ArrayList<>();
            if (forecast != null) {
                for (int i = 0; i < forecast.size(); i++) {
                    //hourly_forecast[] -> FCTTIME -> epoch
                    //hourly_forecast[] -> temp -> metric
                    //hourly_forecast[] -> condition
                    tempTemp = forecast.getParcel(i).getParcel("temp").getDouble("metric");

                    if (minTemp > tempTemp) {
                        minTemp = tempTemp;
                    } else if (maxTemp < tempTemp) {
                        maxTemp = tempTemp;
                    }
                    times.add(forecast.getParcel(i).getParcel("FCTTIME").getLong("epoch"));
                    temps.add(tempTemp);
                    conditions.add(forecast.getParcel(i).getString("icon"));
                }
                updateTempElement();
                updateGraphElement();
                updateWeatherImage();
            }
        }
        catch (SystemException e1) {
            e1.printStackTrace();
        }

    }


}
>>>>>>> 946d46e16ba5b8021b06b430b5f78aefc5419f32:src/main/java/system/clockDisplay/displayElements/WeatherElement.java
