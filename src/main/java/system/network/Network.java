package system.network;


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Will on 4/7/2017.
 */
public class Network {

    public static void main(String args[]){
        try {
            JSch jSch = new JSch();
            String host = "192.168.1.1";
            Session session = jSch.getSession("admin", host, 22);
            session.setPassword("manage89");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
            Channel channel  = session.openChannel("shell");
            channel.connect(30000);
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
                out.write("arp\n".getBytes());
            out.flush();

            byte[] tmp=new byte[1024];
            while(true){
                while(in.available()>0){
                    int i=in.read(tmp, 0, 1024);
                    if(i<0)break;
                    System.out.print(new String(tmp, 0, i));
                }
                if(channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    break;
                }
                try{Thread.sleep(1000);}catch(Exception ee){}
            }
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
