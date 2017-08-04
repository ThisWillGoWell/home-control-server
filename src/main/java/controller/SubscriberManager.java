package controller;

import controller.subscriber.Subscriber;
import parcel.Parcel;
import parcel.SystemException;
import system.SystemParent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Willi on 12/30/2016.
 */
public class SubscriberManager{

    static ConcurrentHashMap<SystemParent, ArrayList<Subscription>> subscriptions = new ConcurrentHashMap<>();

    public static void subscribe(Subscriber s, SystemParent system, Parcel requsetParcel){
        if(!subscriptions.containsKey(system)){
            subscriptions.put(system, new ArrayList<>());
        }
        Subscription subscription = new Subscription(s,requsetParcel);
        try {
            subscription.lastValue =  system.process(requsetParcel).get("payload").toString();
        } catch (SystemException e) {
            e.printStackTrace();
        }
        subscriptions.get(system).add(subscription);
    }

    public static void unsubscribe(Subscriber s, SystemParent system){
        if(subscriptions.containsKey(system)){
            ArrayList<Subscription> currentSubs = new ArrayList<>(subscriptions.get(system));
            for(Subscription subscription : currentSubs){
                if(subscription.subscriber.equals(s)){
                    subscriptions.get(system).remove(subscription);
                }
            }
            if(subscriptions.get(system).size() == 0){
                subscriptions.remove(system);
            }
        }
    }
    public static void unsubscribe(Subscriber s){
        for(SystemParent system : subscriptions.keySet()){
            unsubscribe(s,system);
        }
    }

    public static void checkUpdate(SystemParent system){
        if(subscriptions.containsKey(system)){
            try {

                for(Subscription s: subscriptions.get(system)){
                    Object newVal = system.process(s.requestParcel).get("payload");
                    if(newVal != null && !(s.lastValue.equals(newVal.toString()))){
                        s.lastValue = newVal.toString();
                        Parcel p = new Parcel();
                        p.put("payload", newVal);
                        p.put("alert", "subscription");
                        p.put("request", s.requestParcel);
                        s.subscriber.subscriptionAlert(p);
                        }
                    }

                } catch (SystemException e) {
                e.printStackTrace();
            }
        }
    }

    public static void broadcast(SystemParent system, Parcel msg){
        msg.put("alert","broadcast");
        if(subscriptions.containsKey(system)){
            for(Subscription s : subscriptions.get(system)){
                s.subscriber.subscriptionAlert(msg);
            }
        }
    }
    public static void register(SystemParent system){
        subscriptions.put(system, new ArrayList<>());
    }


}
class Subscription {
    final Subscriber subscriber;
    final Parcel requestParcel;
    Object lastValue;

    Subscription(Subscriber x, Parcel y) {
        this.subscriber = x;
        this.requestParcel = y;
        lastValue = null;

    }
}
