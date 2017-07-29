package system.scheduler;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import controller.Engine;
import controller.Logger;
import parcel.Parcel;
import parcel.ParcelArray;
import parcel.SystemException;
import system.SystemParent;
import system.hue.HueParcel;

import java.time.ZonedDateTime;

/**
 * Created by Willi on 11/10/2016.
 * So I really dont like the way this was designed.
 * Should have kept the update and engine communiation in this
 * class rather than the Schedule Obeject for too many reasons
 * to count.
 *
 * Should be a Quick @TODO
 *
 */
public class Scheduler extends SystemParent{
    public static final String systemIdentifier = "scheduler";

    private ParcelArray scheduleObjects;
    public Scheduler( Engine e){
        super(systemIdentifier, e,1000);
        scheduleObjects = new ParcelArray();
    }

    @Override
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")){
            case "schedule":
                Parcel sp = new Parcel();
                sp.put("name", p.getString("name"));
                sp.put("scheduleObject", new ScheduleObject(p.getString("name"), engine, p.getString("cron"),p.getParcelArray("parcelArray"), p, this));
                scheduleObjects.add(sp);
                return Parcel.RESPONSE_PARCEL(sp.toString());
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }

    }

    @Override
    public void update() {
            try {
                for(Parcel scheduleObjectParcel : scheduleObjects.getParcelArray()){
                    scheduleObjectParcel.getScheduleObject("scheduleObject").update();
                }
            } catch (SystemException e1) {
                e1.printStackTrace();
            }
    }

    public static void main(String[] args) {
        Engine e = new Engine();
        Parcel p0 = HueParcel.SET_ALL_LIGHTS_ON_PARCEL();
        ParcelArray pa = new ParcelArray();
        pa.add(p0);
        Parcel p1  = new Parcel();
        p1.put("systemIdentifier", "AllLightOn");
        p1.put("cron", "0 0/1 * 1/1 * ? *");
        p1.put("parcelArray", pa);
        p1.put("op", "schedule");
        p1.put("system", "system/scheduler");

        System.out.println(e.digestParcel(p1));

        //ScheduleObject scheduleObject = new ScheduleObject("0 0 8 ? * MON,TUE,WED,THU,FRI *", new Parcel());


    }


    public class ScheduleObject {
        private ParcelArray parcels;
        private Cron cron;
        private long lastUpdateTime;
        private Parcel source;
        private String name;
        private Engine e;
        private SystemParent system;

        ScheduleObject(String name, Engine e, String cronStr, ParcelArray parcels, Parcel source, SystemParent system) {
            this.parcels = parcels;
            CronParser cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ));
            this.cron = cronParser.parse(cronStr);
            this.name = name;
            this.source = source;
            this.e = e;
            this.system = system;
            lastUpdateTime = System.currentTimeMillis();
        }

        long getLastExecution() {
            return ExecutionTime.forCron(cron).lastExecution(ZonedDateTime.now()).toEpochSecond() * 1000;
        }

        public String toString(){
            return "ScheduleObject:" +  name + "Cron:"+ cron.asString() + "Source:" + source.toString();
        }

        void update() throws SystemException {
            if (lastUpdateTime < getLastExecution()) {
                lastUpdateTime = System.currentTimeMillis();
                for (Parcel p : parcels.getParcelArray()) {
                    Logger.log(system, "Sending", p, Logger.LOG_LEVEL_INFO);
                    Parcel response = e.digestParcel(p);
                    Logger.log(system, "Recieved", response, Logger.LOG_LEVEL_INFO);
                }
            }
        }

    }
}