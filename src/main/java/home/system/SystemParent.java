
package home.system;


import home.controller.*;
import home.controller.subscriber.Publisher;
import home.controller.subscriber.Subscriber;
import home.controller.subscriber.SubscriberManager;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.controller.PS.GenericPS;

import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by Willi on 9/26/2016.
 */


public abstract class SystemParent implements Runnable, Subscriber, Publisher {


    protected Engine engine;
    private long updateInterval;
    protected String systemIdentifer;
    private Thread thread;
    protected ConcurrentLinkedQueue<Parcel> updateQueue;



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
        this.updateQueue = new ConcurrentLinkedQueue();

    }
    private void registerSubscriber(Parcel p) throws SystemException {
        Logger.log(this, "registering subscriber " + p.toString(), Logger.LOG_LEVEL_INFO);
        p.put(GenericPS.OP_KEY, "get");
        switch (p.getString("type")){
            case GenericPS.SUB_ALERT_TYPE:
                SubscriberManager.listen(p.getSubscriber(GenericPS.SUBSCRIBER_KEY), this);
                break;
            case GenericPS.SUB_CHANGE_TYPE:
                SubscriberManager.subscribe(p.getSubscriber(GenericPS.SUBSCRIBER_KEY), this, p);
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
            switch (p.getString(GenericPS.OP_KEY)){
                default:
                    return process(p);
                case GenericPS.SUB_COMMAND:
                    registerSubscriber(p);
                    response=  Parcel.RESPONSE_PARCEL("register success");
                    break;
                case GenericPS.DEREGISTER_COMMAND:
                    deregisterSubscriber(p.getSubscriber("subscriber"));
                    response = Parcel.RESPONSE_PARCEL("deregister success");
                    break;
                case GenericPS.UPDATE_COMMAND:
                    update();
                    response = Parcel.RESPONSE_PARCEL("updated");
                    break;
                case GenericPS.HELLO_COMMAND:
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

    /**
     * init is called before the first update after thread has been started
     * @throws SystemException
     */
    public void init() throws SystemException {


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
        } catch (SystemException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run this on subscription alert
     * @param p
     */
    public void subscriptionAlert(Parcel p){

    }

    /**
     *
     * @return
     */
    public String getSystemIdentifer() {
        return systemIdentifer;
    }

    @Override
    public String getPublisherIdentifer() {
        return getSystemIdentifer();
    }

    public void update() throws SystemException {

    }

}