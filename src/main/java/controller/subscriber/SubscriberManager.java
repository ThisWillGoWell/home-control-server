package controller.subscriber;

import parcel.Parcel;
import parcel.SystemException;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Subscriber Manager for the system
 * Static statndalone class that mangaes it for the whole system
 * Allows for a subscriber to be updated when a request changes value
 *
 * Todo
 * Turn into a system, similar to what happened to engine
 * Created by Willi on 12/30/2016.
 */
public class SubscriberManager{

    private static ConcurrentHashMap<Publisher, ArrayList<Subscription>> subscriptions = new ConcurrentHashMap<>();

    /**
     * subscribe: realates a subscriber to a publisher
     * @param s The Subscriber object who is subscribing to:
     * @param publisher the publisher that is being subscribed to
     * @param publisher the publisher that is being subscribed to
     * @param requsetparcel parcel that when the response changes will update the subsriber
     *                      if null you will only get broadcasts to that publisher
     */
    public static void subscribe(Subscriber s, Publisher publisher, Parcel requsetparcel){
        if(!subscriptions.containsKey(publisher)){
            subscriptions.put(publisher, new ArrayList<>());
        }
        Subscription  subscription= new Subscription(s,requsetparcel);
        if(requsetparcel != null){
            try {
                subscription.lastValue =  publisher.process(requsetparcel).get("payload").toString();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }

        subscriptions.get(publisher).add(subscription);
    }

    public static void listen(Subscriber s, Publisher publisher){
        subscribe(s, publisher, null);
    }

    /**
     * Removes subscriber s from the publisher
     * @param s
     * @param publisher
     */
    public static void unsubscribe(Subscriber s, Publisher publisher){
        if(subscriptions.containsKey(publisher)){
            ArrayList<Subscription> currentSubs = new ArrayList<>(subscriptions.get(publisher));
            for(Subscription subscription : currentSubs){
                if(subscription.subscriber.equals(s)){
                    subscriptions.get(publisher).remove(subscription);
                }
            }
            if(subscriptions.get(publisher).size() == 0){
                subscriptions.remove(publisher);
            }
        }
    }

    /**
     * removes subscriber from all systems
     * Used in web socket disconcert
     * @param s: subscriber unsubcriber
     */
    public static void unsubscribe(Subscriber s){
        for(Publisher system : subscriptions.keySet()){
            unsubscribe(s,system);
        }
    }

    /**
     *  Checkupdate()
     *      takes in the system that was just updated
     *      processes each of  the request parceles in
     *      the systems subscriber
     *      Here the request parcel is the command that is subscribed
     *      to. Does String compare to check when two parcels are differnt
     *
     */
    public static void checkUpdate(Publisher publisher){

        if(subscriptions.containsKey(publisher)){


                for(Subscription s: subscriptions.get(publisher)) {
                    if (s.requestParcel != null) {
                        Object newVal = null;
                        try {
                            newVal = publisher.process(s.requestParcel).get("payload");
                            if (newVal != null && !(s.lastValue.equals(newVal.toString()))) {
                                s.lastValue = newVal.toString();
                                Parcel p = new Parcel();
                                p.put("payload", newVal);
                                p.put("alert", "subscription");
                                p.put("request", s.requestParcel);
                                s.subscriber.subscriptionAlert(p);
                            }
                        } catch (SystemException e) {
                            e.printStackTrace();
                            s.subscriber.subscriptionAlert(Parcel.RESPONSE_PARCEL_ERROR(e));
                        }

                    }
                }

        }
    }

    /**
     * Broadcast a msg to all subscribers of a publisher
     * @param publisher system ready to accpect subscribers
     * @param msg what you want to say to all subscriptions
     */
    public static void broadcast(Publisher publisher, Parcel msg) {

        broadcast(publisher, null, msg);

    }

    public static void broadcast(Publisher publisher, String listenKey, Parcel msg){
        msg.put("alert","broadcast");
        if(subscriptions.containsKey(publisher)){
            for(Subscription s : subscriptions.get(publisher)){
                if(listenKey == null || Objects.equals(listenKey, s.listenKey)) {
                    s.subscriber.subscriptionAlert(msg);
                }
            }
        }
    }


    /**
     * Register a system to accept subscribers
     * @param system system ready to accept subscribers
     */
    public static void register(Publisher system){
        subscriptions.put(system, new ArrayList<>());
    }


}

/**
 * Subscription storage
 * Used to tie a subscriber to requestParcel
 *
 */
class Subscription {
    final Subscriber subscriber;
    final Parcel requestParcel;
    final String listenKey;
    Object lastValue;

    Subscription(Subscriber x, String y){
        this.subscriber = x;
        this.listenKey = y;
        requestParcel = null;
        lastValue = null;
    }

    Subscription(Subscriber x, Parcel y) {
        this.subscriber = x;
        this.requestParcel = y;
        this.listenKey = null;
        lastValue = null;

    }
}
