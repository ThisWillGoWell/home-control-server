
package home.controller.webmanager;

import home.controller.Engine;
import home.parcel.ParcelArray;
import org.springframework.ui.ModelMap;
import home.parcel.Parcel;
import home.parcel.SystemException;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;


@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
@EnableWebSocket
@RestController
public class Application extends SpringBootServletInitializer{

    public static void disableHTTPLogging(){

        Set<String> loggers = new HashSet<>(Arrays.asList("org.apache.http", "groovyx.net.http"));

        for(String log:loggers) {
            Logger logger = (Logger)LoggerFactory.getLogger(log);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }
    }

    private static Engine e = null; //= new Engine();

    public static Engine getEngine() {
        return e;
    }

    public static void main(String[] args) {
        disableHTTPLogging();
        e = new Engine();

        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    public Object command() {
        return "hello";
    }

    @RequestMapping(value = "/c", method = RequestMethod.POST)
    public Object command(@RequestBody String jsonString) {
        if(jsonString.substring(0,1).equals("[")){
            ParcelArray pa = new ParcelArray();
            pa = e.digestParcels(ParcelArray.PROCESS_JSONARRAY(jsonString));
            try {
                StringBuilder response = new StringBuilder("[");
                for(Parcel p : pa.getParcelArray()){
                    response.append(p.toPayload()).append(",");
                }
                if(response.length() > 1){
                    response.deleteCharAt(response.length()-1);
                }
                response.append("]");
                return response;
            } catch (SystemException e1) {
                return Parcel.RESPONSE_PARCEL_ERROR(e1);
            }
        }
        try {
            Parcel p = e.digestParcel(Parcel.PROCESS_JSONSTR(jsonString));
            return p.toPayload();
        } catch (SystemException e1) {
            return Parcel.RESPONSE_PARCEL_ERROR(e1);
        }
    }

    @RequestMapping(value = {"/c/", "/c"}, method = RequestMethod.GET)
    public Object command( @RequestParam Map<String,String> allRequestParams, ModelMap model) throws SystemException {
        return e.digestParcel(new Parcel(allRequestParams)).toPayload();
    }



    public static String readFile(String path, Charset encoding)
            throws IOException
    {
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