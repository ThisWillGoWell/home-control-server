package system.weather;

import com.google.gson.*;

import controller.Engine;
import parcel.Parcel;
import parcel.SystemException;
import parcel.StateValue;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import system.SystemParent;


/**
 * Created by Willi on 9/26/2016.
 * Class that manages the WeatherUnderground API Class
 * as a system.
 *
 */
public class Weather extends SystemParent{
    public static final String systemIdentifier = "weather";

    private static Parcel DEAFULT_WEATHER_STATE(){
        Parcel p = new Parcel();
        p.put("currentWeatherURL", new StateValue("http://api.wunderground.com/api/0457bdf6163baa58/conditions/q/NY/Rochester.json", StateValue.READ_WRITE_PRIVLAGE));
        p.put("weekForecastURL", new StateValue("http://api.wunderground.com/api/0457bdf6163baa58/forecast/q/NY/Rochester.json", StateValue.READ_WRITE_PRIVLAGE));
        p.put("hourlyForecastURL", new StateValue("http://api.wunderground.com/api/0457bdf6163baa58/hourly/q/NY/Rochester.json", StateValue.READ_WRITE_PRIVLAGE));
        p.put("conditions", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put("weeklyForecast", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        p.put("hourlyForecast", new StateValue(new Parcel(), StateValue.READ_PRIVLAGE));
        return p;
    }

    private HttpClient client ;
    private Parcel state;
    /*
    Update once every 10 min
    Use HTTPClientBulder class for http calls
     */
    public Weather( Engine e)
    {
        super(systemIdentifier, e,10*60*1000);
        state = DEAFULT_WEATHER_STATE();
        client = HttpClientBuilder.create().build();
        update();
    }

    @Override
    public Parcel process(Parcel p) throws SystemException {
        switch (p.getString("op")) {
            case "get":
                switch (p.getString("what")) {
                    case "currentTemp":
                        return Parcel.RESPONSE_PARCEL(getCurrentTemp());
                    case "todayHigh":
                        return Parcel.RESPONSE_PARCEL(getTodayHigh());
                    case "todayLow":
                        return Parcel.RESPONSE_PARCEL(getTodayLow());
                    case "currentIcon":
                        return Parcel.RESPONSE_PARCEL(getCurrentConditions());
                    case "state":
                        return Parcel.RESPONSE_PARCEL(state);
                    default:
                        if (state.contains(p.getString("what"))) {
                            StateValue sp = state.getStateParcel(p.getString("what"));
                            if (sp.canRead()) {
                                return Parcel.RESPONSE_PARCEL(sp.getValue());
                            }
                            throw SystemException.ACCESS_DENIED(p);
                        }
                        throw SystemException.WHAT_NOT_SUPPORTED(p);
                }
            case "set":
                switch (p.getString("what")) {
                    default:
                        StateValue sp = state.getStateParcel(p.getString("what"));
                        if (sp.canWrite()) {
                            sp.update(p.get("to"));
                            return Parcel.RESPONSE_PARCEL(sp.getValue());
                        }
                        throw SystemException.ACCESS_DENIED(p);
                }
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }
    }

    /*
    Query the current weather URL of the weather underground API
     */
    private Parcel queryCurrentWeather() throws Exception {
        HttpGet request = null;
        request = new HttpGet(state.getString("currentWeatherURL"));
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String response = client.execute(request, responseHandler);
        return Parcel.PROCESS_JSONSTR(response);
    }

    /*
    Query the hour-by-hour forecast
     */
    private Parcel queryHourForecastWeather() throws Exception {
        HttpGet request = new HttpGet(state.getString("hourlyForecastURL"));
        ResponseHandler<String> responseHandler=new BasicResponseHandler();
        String response = client.execute(request, responseHandler);
        return Parcel.PROCESS_JSONSTR(response);
    }

    /*
    Query the Whole weeks weather, day by day
     */
    private Parcel queryWeekForecastWeather() throws Exception {
        HttpGet request = new HttpGet(state.getString("weekForecastURL"));
        ResponseHandler<String> responseHandler=new BasicResponseHandler();
        String response = client.execute(request, responseHandler);
        return Parcel.PROCESS_JSONSTR(response);
    }


    /*
        On update will update the response jsons
     */
    public void update() {
        try {
            state.getStateParcel("conditions").update(queryCurrentWeather());
            state.getStateParcel("hourlyForecast").update(queryHourForecastWeather());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    The following methods just parse the reponse json
     */
    private double getCurrentTemp() throws SystemException {
        return  state.getParcel("conditions").getParcel("current_observation").getDouble("temp_c");

    }

    private String getTodayForecast() throws SystemException {
        return state.getParcel("weekForecast").getParcel("forecast").getParcel("simpleforecast").getParcelArray("forecastday").getParcel(0).getString("icon");
    }

    private double getTodayHigh() throws SystemException {
        return state.getParcel("weekForecast").getParcel("forecast").getParcel("simpleforecast").getParcelArray("forecastday").getParcel(0).getParcel("high").getDouble("celsius");
    }

    private double getTodayLow() throws SystemException {
        return state.getParcel("weekForecast").getParcel("forecast").getParcel("simpleforecast").getParcelArray("forecastday").getParcel(0).getParcel("low").getDouble("celsius");
    }


    String getCurrentConditions() throws SystemException {
        String s =  state.getParcel("conditions").getParcel("current_observation").getString("icon_url");
        return s.substring(s.lastIndexOf('/') + 1).replaceFirst("[.][^.]+$", "");
    }


    public static void main(String args[]){
        Weather weather = new Weather(new Engine());
        String s = null;
        JsonObject json = null;
        weather.update();
    }





}