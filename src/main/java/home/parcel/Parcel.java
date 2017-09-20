package home.parcel;

import com.google.gson.*;
import home.controller.webmanager.SocketSession;
import home.controller.subscriber.Subscriber;
import org.json.hue.JSONException;
import org.json.hue.JSONArray;
import org.json.hue.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import home.system.scheduler.Scheduler;
import su.litvak.chromecast.api.v2.ChromeCast;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Willi on 12/19/2016.
 * The core of the messageing system
 */
public class Parcel extends ConcurrentHashMap<String, Object> {



    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public Parcel() {
        super();
    }

    public Parcel( Parcel p) throws SystemException {
        for(String key: p.keySet()){
            Object o = p.get(key);
            if(o instanceof StateValue){
                this.put(key, new StateValue((StateValue) o));
            }
            else if(o instanceof Parcel) {
                this.put(key, new Parcel((Parcel) o));
            }
            else if(o instanceof ParcelArray){
                this.put(key, new ParcelArray((ParcelArray) o));
            }
            else{
                this.put(key, o);
            }
        }
    }

    public Parcel(Map<String, String> allRequestParams) {
        super(allRequestParams);
    }

    /**
     * Add a key-value that also is the key in the system envvs
     * @param key the key for the parcel and the key of the system env
     */

    public void fromEnvString(String key){
        this.put(key, System.getenv(key));
    }

    /**
     * add a key-value where value is pulled
     * @param key the key for the parcel
     * @param systemKey the key in the system
     */
    public void fromEnvString(String key, String systemKey ){
        this.put(key, System.getenv(systemKey));
    }

    /*
    @TODO: FIX THIS SHIT
     */
    public boolean equals(Object o){
        if(!(o instanceof Parcel)) {
            return false;
        }
        Parcel p = (Parcel) o;
        for(String key : this.keySet()){
            if(!p.containsKey(key)){
                return false;
            }
            try {
                Object currentObject = this.get(key);
                Object theirObejct = p.get(key);
                //Only do .equals on wrapper types
                if(isWrapperType(currentObject.getClass())){
                    if(!currentObject.equals(theirObejct)){
                        return false;
                    }
                }
                else{ //otherwise do String compare?
                    if(!currentObject.toString().equals(theirObejct.toString())){
                        return false;
                    }
                }
            } catch (SystemException e) {
                e.printStackTrace();
                return false;
            }
        }
    return true;
    }

    public static Parcel PROCESS_JSONOBJ(JSONObject jsonObject){
        Parcel p = new Parcel();
        Object resp = null;
        String reply;
        for(Object key : jsonObject.keySet()){
            String k = (String) key;
            if(jsonObject.get(k) instanceof JSONArray){
                p.put(k,ParcelArray.PROCESS_JSONARRAY( jsonObject.getJSONArray(k)));
            }
            else if(jsonObject.get(k) instanceof JSONObject){
                p.put(k, PROCESS_JSONOBJ(jsonObject.getJSONObject(k)));
            }
            else{
                p.put(k, jsonObject.get(k));
            }
        }
        return p;
    }

    public static Parcel JOIN_PARCELS(Parcel p1, Parcel p2){
        Parcel p3 = null;
        try {
            p3 = new Parcel(p1);
            Parcel p4 = new Parcel(p2);
            for(String key:p4.keySet()){
                p3.put(key, p4.get(key));
            }
        } catch (SystemException e) {
            e.printStackTrace();
        }
        return p3;

    }

    public static Parcel PROCESS_MAP(Map<String, String> m){
        Parcel p = new Parcel();
        Object resp = null;
        String reply;
        for(String key: m.keySet()){
            p.put(key,m.get(key));
        }
        return p;
    }

    public static Parcel PROCESS_JSONSTR(String jsonStr){
        try {
            JSONObject m = new JSONObject(jsonStr);
            return PROCESS_JSONOBJ(m);
        }catch (JSONException e){
            return Parcel.RESPONSE_PARCEL_ERROR(0,"Invalid Json Code");
        }
    }

    public static Parcel PROCESS_XML_URL(String xmlURL){
        Parcel p = new Parcel();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        Document doc = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(xmlURL);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        return PROCESS_XML_HELPER(doc.getDocumentElement());

    }

    private static Parcel PROCESS_XML_HELPER(Node n){
        NodeList childrenNodes = n.getChildNodes();
        Parcel subNode = new Parcel();


        if(childrenNodes.getLength() != 0){

            for(int i = 0; i < childrenNodes.getLength();i++){
                Node childNode = childrenNodes.item(i);

                if(childNode.getChildNodes().getLength() == 0 ) {
                    if(childNode.hasAttributes()){
                        Parcel subsubNode = new Parcel();
                        for(int j=0;j<childNode.getAttributes().getLength();j++){
                            Node attributeNode = childNode.getAttributes().item(j);
                            subsubNode.put(attributeNode.getNodeName(), attributeNode.getNodeValue());
                        }
                        subNode.put(childNode.getNodeName(), subsubNode);
                    }

                }
                if(childNode.getChildNodes().getLength() == 1){
                   subNode.put(childNode.getNodeName(), childNode.getChildNodes().item(0).getNodeValue());
                }
                else if(childNode.getChildNodes().getLength() > 1){
                    subNode.put(childNode.getNodeName(), PROCESS_XML_HELPER(childNode));
                }

            }

        }
        return subNode;
    }

    public static Parcel OP_PARCEL(String op){
        Parcel p = new Parcel();
        p.put("op", op);
        return p;
    }

    public static Parcel GET_PARCEL(String system, String what) {
        Parcel p = new Parcel();
        p.put("op", "get");
        p.put("system", system);
        p.put("what", what);
        return p;
    }

    public static Parcel SET_PARCEL(String system, String what, Object to) {
        Parcel p = new Parcel();
        p.put("op", "set");
        p.put("system", system);
        p.put("what", what);
        p.put("to", to);
        return p;
    }

    public static Parcel RESPONSE_PARCEL_ERROR(SystemException e){
        Parcel p = new Parcel();
        p.put("success", false);
        p.put("onlyPayload", false);
        p.put("status", e.getError());
        p.put("error", e.getMessage());
        p.put("payload", "none");
        return p;
    }

    public static Parcel RESPONSE_PARCEL_ERROR(int errorCode, String error ){
        Parcel p = new Parcel();
        p.put("success", false);
        p.put("status", errorCode);
        p.put("onlyPayload", false);
        p.put("error", error);
        p.put("payload", "none");
        return p;
    }

    public static Parcel RESPONSE_PARCEL(Object payload){
        return RESPONSE_PARCEL(payload, false);
    }

    public static Parcel RESPONSE_PARCEL(Object payload, boolean onlyPayload){
        Parcel p = new Parcel();
        p.put("success", true);
        p.put("status", 200);
        p.put("onlyPayload", onlyPayload);
        p.put("error", "None");
        p.put("payload", payload);
        return p;
    }

    private static ParcelArray REMOVE_STATE_OBJECTS(ParcelArray p){
        ParcelArray pa = new ParcelArray();
        for(Object o : p){
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();
            if(o instanceof Parcel)
                o = REMOVE_STATE_OBJECTS((Parcel) o);
            if(o instanceof ParcelArray) {
                o = REMOVE_STATE_OBJECTS((ParcelArray) o);
            }
            pa.add(o);
        }
        return pa;
    }

    private static Parcel REMOVE_STATE_OBJECTS(Parcel p){
        try {
            Parcel noStateParcel = new Parcel();
            for (String key : p.keySet())
            {
                Object o = p.get(key);
                if(o instanceof StateValue)
                    noStateParcel.put(key, ((StateValue) o).getValue());
                else if(o instanceof Parcel)
                    noStateParcel.put(key , REMOVE_STATE_OBJECTS((Parcel) o));
                else if(o instanceof ParcelArray) {
                    noStateParcel.put(key, REMOVE_STATE_OBJECTS((ParcelArray) o));
                }
                else{
                    noStateParcel.put(key, o);
                }
            }
            return noStateParcel;
        }catch (SystemException e) {
            e.printStackTrace();
        }
        return null;

    }

    static Parcel ONLY_WRAPPER_VALUES(Parcel p){
        try {
            Parcel onlyWrapper = new Parcel();
            for (String key : p.keySet())
            {
                Object o = p.get(key);
                if (o != null && isWrapperType(o.getClass())){
                    if(o instanceof Parcel) {
                        onlyWrapper.put(key, ONLY_WRAPPER_VALUES((Parcel) o));
                    }
                    else if(o instanceof ParcelArray){
                        onlyWrapper.put(key, ParcelArray.ONLY_WRAPPER_VALUES((ParcelArray) o));
                    }
                    else
                        onlyWrapper.put(key, o);
                }
                else {
                    if(o == null){
                        onlyWrapper.put(key, "none");
                    }else{
                        onlyWrapper.put(key, o.toString());
                    }

                }
            }
            return onlyWrapper;
        }catch (SystemException e) {

            e.printStackTrace();
        }
        return null;

    }

    /**
     * to Payload
     * return the payload value
     * @return the payload object
     * @throws SystemException if key payload not found
     */
    public Object toPayload() throws SystemException {
        if(containsKey("payload")){
            if(containsKey("onlyPayload")){
                if(getBoolean("onlyPayload")){
                    return this.get("payload");
                }
                else{
                    return this.toString();
                }
            }
            else{
                return this.toString();
            }
        }
        throw  new SystemException("No Payload Found in package", SystemException.PAYLOAD_NOT_FOUND, this);
    }

    public Object get(String key) throws SystemException {
        if(contains(key)){
            return super.get(key);
        }
        throw new SystemException("Key " + key + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public String toString(){
        Parcel p = ONLY_WRAPPER_VALUES(REMOVE_STATE_OBJECTS(this));
        StringBuilder json = new StringBuilder("{");
        for(String key : this.keySet()){
            Object o = null;

            try {
                o = this.get(key);
            } catch (SystemException e) {
                e.printStackTrace();
            }
            if(!(o instanceof StateValue) || ((StateValue) o).canRead()) {
                json.append('"');
                json.append(key);
                json.append('"');
                json.append(':');
                if(!isWrapperType(o.getClass())){
                    json.append('"');
                    String objStr = o.toString();
                    objStr = objStr.replaceAll("(?<!\\\\)\'", "\\\\'");
                    objStr = objStr.replaceAll("(?<!\\\\)\"", "\\\\\"");
                    json.append(objStr);
                    json.append('"');
                }else{
                    json.append(o);
                }


            }
            json.append(',');
        }
        if(json.length() == 0)
            return "{}";
        json.deleteCharAt(json.length()-1);
        json.append('}');
        return json.toString();
    }

    public boolean contains(String value) {
        return this.containsKey(value);
    }

    public boolean success(){
        if(containsKey("success")){
            try {
                return this.getBoolean("success");
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getString(String value) throws SystemException {

        if (this.containsKey(value)) {
            return this.get(value).toString();
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }


    public Long getLong(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();
            if(o instanceof Long)
                return (Long) o;
            if(o instanceof Double)
                return ((Double) o).longValue();
            try {
                return Long.parseLong(o.toString());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Long", SystemException.CLASS_CAST_ERROR, this);
            }
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public Integer getInteger(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();
            if(o instanceof Double)
                return ((Double) o).intValue();
            if(isNumeric(o.toString())){
                try{
                    return Integer.parseInt(o.toString());
                }
                catch (java.lang.NumberFormatException e){
                    throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Integer", SystemException.CLASS_CAST_ERROR, this);
                }
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Integer", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public Double getDouble(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();
            if(o instanceof Double){
                return (Double) o;
            }
            if(isNumeric(o.toString())){
                try {
                    return Double.parseDouble(o.toString());
                }
                catch (java.lang.NumberFormatException e){
                    throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Double", SystemException.CLASS_CAST_ERROR, this);
                }
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Double", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public Parcel getParcel(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();
            if (o instanceof Parcel) {
                return (Parcel) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Parcel", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public boolean getBoolean(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();
            if (o instanceof Boolean) {
                return (Boolean) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Boolean", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public Subscriber getSubscriber(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();

            if (o instanceof Subscriber) {
                return (Subscriber) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected Subscriber", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public ParcelArray getParcelArray(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();

            if (o instanceof ParcelArray) {
                return (ParcelArray) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected ParcelArray", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public Scheduler.ScheduleObject getScheduleObject(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();

            if (o instanceof Scheduler.ScheduleObject) {
                return (Scheduler.ScheduleObject) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected ScheduleObject", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    public ChromeCast getChromecastObject(String value) throws SystemException{
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if(o instanceof StateValue)
                o = ((StateValue) o).getValue();

            if (o instanceof ChromeCast) {
                return (ChromeCast) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected ChromeCast", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }


    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Parcel.class);
        ret.add(ParcelArray.class);
        return ret;
    }

    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    public StateValue getStateParcel(String value) throws SystemException {
        if (this.containsKey(value)) {
            Object o = this.get(value);
            if (o instanceof StateValue) {
                return (StateValue) o;
            }
            throw new SystemException("Key " + value + " returns object " + o.toString() + " of class " + o.getClass().toString() + " Expected StateValue", SystemException.CLASS_CAST_ERROR, this);
        }
        throw new SystemException("Key " + value + " not found in package " + toString(), SystemException.KEY_NOT_FOUND, this);
    }

    private boolean isNumeric(String s) {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static void main(String args[]){
        double d = 1/3;
        int i = 100;
        long l = 50000;
        Parcel p = new Parcel();
        p.put("boolean", true);
        p.put("boolean", false);
        p.put("string", "testString");
        p.put("boolean", true);
        p.put("double", d);
        p.put("int", i);
        p.put("long", l);
        p.put("complex", new SocketSession(null));

        ParcelArray pa = new ParcelArray();

        Parcel p1 = new Parcel();
        p1.put("number", 0);
        pa.add(p);

        pa.add(p);
        pa.add(p);

        Parcel top = new Parcel();
        System.out.println(p.toString());

        try {
            System.out.println(p.getInteger("double"));
        } catch (SystemException e) {
            e.printStackTrace();
        }
        try {
            System.out.println(p.getInteger("int"));
        } catch (SystemException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(p.getInteger("long"));
        } catch (SystemException e) {
            e.printStackTrace();
        }
        top.put("array", pa);
        System.out.println(top);
        System.out.println(Parcel.PROCESS_JSONSTR(top.toString()));
    }
}
