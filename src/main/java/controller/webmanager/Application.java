
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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


    private static Engine e = null; //= new Engine();

    public static Engine getEngine() {
        return e;
    }

    public static void main(String[] args) {
        e = new Engine();

        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    public Object command() {
        return "hello";
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

    /**
     * Will run command in shell and reutrn the results as a string
     * @param command command to be run
     * @return the output
     */
    public static String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public static String getOS(){
        return System.getProperty("os.name");
    }
    public static boolean isWindows(){
        return getOS().contains("Windows");
    }


}