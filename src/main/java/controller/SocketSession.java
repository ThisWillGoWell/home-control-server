package controller;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import parcel.Parcel;
import system.SystemParent;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Created by Willi on 12/30/2016.
 */
public class SocketSession implements Subscriber, Runnable{

    private WebSocketSession session;
    public SocketSession(WebSocketSession session){
        this.session = session;
    }
    private Semaphore semaphore = new Semaphore(1);
    BlockingQueue<Parcel> parcelQueue =new LinkedBlockingQueue<>();


    @Override
    public void subscriptionAlert(Parcel p) {
        queueMsg(p);
    }


    void queueMsg(Parcel p){
        try {
            System.out.println("parcel Queued");
            parcelQueue.put(p);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public String toString(){
        return "SocketSession: " + session.getId();
    }

    @Override
    public void run() {

        while(session.isOpen()){
            Parcel p = null;
            try {
                p = parcelQueue.take();
                if(session.isOpen()) {
                    session.sendMessage(new TextMessage(p.toString()));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        SubscriberManager.unsubscribe(this);
    }
}
