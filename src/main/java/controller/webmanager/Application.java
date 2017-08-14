
package controller.webmanager;

import controller.Engine;
import org.springframework.ui.ModelMap;
import parcel.Parcel;
import parcel.SystemException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableWebSocket
@RestController
public class Application extends SpringBootServletInitializer{


    private static Engine e = new Engine();

    public static Engine getEngine() {
        return e;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/c", method = RequestMethod.POST)
    public Object command(@RequestBody String jsonString) {
        Parcel p = e.digestParcel(Parcel.PROCESS_JSONSTR(jsonString));
        try {
            String s = (String) p.toPayload();
            return p.toPayload();
        } catch (SystemException e1) {
            return Parcel.RESPONSE_PARCEL_ERROR(e1);
        }
    }

    @RequestMapping(value = "/spotifyRD", method = RequestMethod.GET)
    public Object command(@RequestParam Map<String,String> allRequestParams, ModelMap model ) {
        Parcel requset = null;
        try {
            requset = Parcel.SET_PARCEL("spotify","userCode",Parcel.PROCESS_MAP(allRequestParams).getString("code"));
        } catch (SystemException e1) {
            e1.printStackTrace();
        }
        Parcel p = e.digestParcel(requset);
        if(p.success()){
            e.digestParcel(Parcel.OP_PARCEL("login"));
        }
        try {
            String s = (String) p.toPayload();
            return p.toPayload();
        } catch (SystemException e1) {
            return Parcel.RESPONSE_PARCEL_ERROR(e1);
        }
    }

    public static String readFile(String path, Charset encoding)
            throws IOException
    {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

}