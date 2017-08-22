package controller;

import controller.subscriber.Publisher;
import parcel.Parcel;
import parcel.SystemException;
import system.SystemParent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by Will on 3/16/2017.
 */
public class Logger {
    public static final int LOG_LEVEL_INFO = 1;
    public static final int LOG_LEVEL_ERROR = 2;
    public static final int LOG_LEVEL_DEBUG = 3;
    public static final int LOG_LEVEL_PARCEL_MESSAGE = 4;
    public static final int LOG_LEVEL_WEB = 5;
    public static final int LOG_LEVEL_SUBSCRIPTION = 6;

    private static File logFile;

    private static final String noSystem = "No System";

    private void initFile(){
        logFile = new File("/logs");
        if(!logFile.exists()){
            try {
                logFile.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void log(Publisher pub, Parcel par){
        log(pub.getPublisherIdentifer(),par.toString(),LOG_LEVEL_SUBSCRIPTION);
    }
    public static void log(String message, int type){
        log(noSystem, message, type);
    }

    public static void log( Exception e){
        log(noSystem, e.toString(), LOG_LEVEL_ERROR);
    }


    public static void log(SystemParent source, Exception e){

        log(source.getSystemIdentifer(), e.toString(), LOG_LEVEL_ERROR);

    }



    public static void log(SystemParent source, Parcel p){
        log(source, p.toString(), LOG_LEVEL_PARCEL_MESSAGE);
    }


    public static void log(SystemParent source, String msg, Parcel p, int type){
        log(source,msg + ": " + p.toString(), type);
    }

    public static void log(SystemParent source, String msg, Parcel p){
        log(source,msg + ": " + p.toString(), LOG_LEVEL_PARCEL_MESSAGE);
    }

    public static void log(SystemParent source, String message, int type){
        //Write to file
       log(source.getSystemIdentifer(),message,type);
    }

    public static void log(String source, String message, int type){
        String printString = String.format("%s [%s]\t%s\t- %s", timestamp(), source, logLevelMap(type), message);
        System.out.println(printString);
    }

    private static String timestamp(){
        return new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSS").format(Calendar.getInstance().getTime());

    }

    private static String logLevelMap(int logNum){
        switch (logNum){
            default:
                return "UNKNOWN";
            case LOG_LEVEL_INFO:
                return "INFO";
            case LOG_LEVEL_ERROR:
                return "ERROR";
            case LOG_LEVEL_DEBUG:
                return "DEBUG";
            case LOG_LEVEL_PARCEL_MESSAGE:
                return "PARCEL";
            case LOG_LEVEL_SUBSCRIPTION:
                return "SUBSCRIPTION";
            case LOG_LEVEL_WEB:
                return "WEB";
        }

    }




    public static void print(String msg){
        System.out.println(msg);
    }
}

