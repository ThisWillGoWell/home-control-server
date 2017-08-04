
package system;


import controller.*;
import controller.subscriber.Publisher;
import controller.subscriber.Subscriber;
import controller.subscriber.SubscriberManager;
import parcel.Parcel;
import parcel.SystemException;


/**
 * Created by Willi on 9/26/2016.
 */


public abstract class SystemParent implements Runnable, Subscriber, Publisher {
    
    protected Engine engine;
    private long updateInterval;
    protected String systemIdentifer;
    private Thread thread;



    public SystemParent(String name){
        this(name, null);
    }
    public SystemParent(String name, Engine e)
    {
        this(name, e,-1);
    }
    public SystemParent(String name, Engine e, long updateInterval)
    {
        this.updateInterval = updateInterval;
        this.engine = e;
        this.systemIdentifer = name;
        SubscriberManager.register(this);

        thread = new Thread(this);

    }
    private void registerSubscriber(Parcel p) throws SystemException {
        Logger.log(this, "registering subscriber " + p.toString(), Logger.LOG_LEVEL_INFO);
        p.put("op", "get");
        switch (p.getString("type")){
            case "alert":
                SubscriberManager.listen(p.getSubscriber("subscriber"), this);
                break;
            case "change":
                SubscriberManager.subscribe(p.getSubscriber("subscriber"), this, p);
                break;
            default:
                throw SystemException.WHAT_NOT_SUPPORTED(p);
        }

    }

    public void start(){
        thread.start();
    }

    private void deregisterSubscriber(Subscriber s){
        SubscriberManager.unsubscribe(s, this);
    }

    /*
    Command: Replacement for /set and /get
    All calls will be routed though this command interface and
    use the dict to extrace the operation: "op"
     */
    public Parcel command(Parcel p){
        /*
        take care of any high level command here,
        Only one currently in use is register for websocket listener
         */
        Parcel response = null;
        Logger.log(this,p);
        try {
            switch (p.getString("op")){
                default:
                    return process(p);
                case "subscribe":
                    registerSubscriber(p);
                    response=  Parcel.RESPONSE_PARCEL("register success");
                    break;
                case "deregister":
                    deregisterSubscriber(p.getSubscriber("subscriber"));
                    response = Parcel.RESPONSE_PARCEL("deregister success");
                    break;
                case "update":
                    update();
                    response = Parcel.RESPONSE_PARCEL("updated");
                    break;
                case "hello":
                    response = Parcel.RESPONSE_PARCEL("hi");
                    break;
            }
        } catch (SystemException e) {
            Logger.log(this, e);
            return Parcel.RESPONSE_PARCEL_ERROR(e);
        }finally {
            SubscriberManager.checkUpdate(this);
        }
        Logger.log(this, response);
        return response;
    }

    public abstract Parcel process(Parcel p) throws SystemException;




    public Engine getEngine() {
        return engine;
    }


    public void init(){


    }

    public void stop(){
        thread.interrupt();
    }

    public void run(){
        try {
            init();
            if(updateInterval != -1) {
                while (true) {
                    update();
                    SubscriberManager.checkUpdate(this);
                    if(updateInterval <= 0){
                        break;
                    }
                    Thread.sleep(updateInterval);
                }
            }
        }
        catch (InterruptedException e) {
            Logger.log(this, e);
        }
    }

    public void subscriptionAlert(Parcel p){

    }

    public String getSystemIdentifer() {
        return systemIdentifer;
    }

    @Override
    public String getPublisherIdentifer() {
        return getSystemIdentifer();
    }

    public void update(){

    }
}