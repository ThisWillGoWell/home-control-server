package home.controller.webmanager;

import home.controller.Logger;
import home.controller.subscriber.SubscriberManager;
import home.controller.subscriber.Subscriber;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import home.parcel.Parcel;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by Willi on 12/30/2016.
 * So websockets can act as subscribers, this class will broker that
 *
 */
public class SocketSession implements Subscriber, Runnable{

    private WebSocketSession session;
    public SocketSession(WebSocketSession session){
        this.session = session;
    }
    private Semaphore semaphore = new Semaphore(1);
    BlockingQueue<Parcel> parcelQueue =new LinkedBlockingQueue<>();

    /**
     * queue send all alets
     * @param p parcel to be sent
     */
    @Override
    public void subscriptionAlert(Parcel p) {
        queueMsg(p);
    }

    /**
     * put the parcel into a Thread safe queue
     * @param p parcel to be sent
     */
    void queueMsg(Parcel p){
        try {
            parcelQueue.put(p);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public String toString(){
        return "SocketSession: " + session.getId();
    }

    /**
     * Run method, each socket session is its own thread
     * Will blovk until there is a message, at what pooint will
     * sent the message out the session.
     *
     * Had to make it this way to prevent a disconnct from failing
     * to unscbscribe.
     * Todo have all socketsessions be managed by same thread
     */
    @Override
    public void run() {

        while(session.isOpen()){
            Parcel p = null;
            try {
                p = parcelQueue.take();
                if(session.isOpen()) {
                    Logger.log("sending: " + p.toString() + " to Socket Session " + session.getId(),Logger.LOG_LEVEL_WEB);
                    session.sendMessage(new TextMessage(p.toString()));
                    p = null;
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                if(p != null){
                    Logger.log(e);
                }
            }
        }
        SubscriberManager.unsubscribe(this);
    }
}
