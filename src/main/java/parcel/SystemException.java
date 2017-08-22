package parcel;

import system.SystemParent;

public class SystemException extends Exception{
    static int EXCEPTION = 0;
    static int KEY_NOT_FOUND = 1;
    static int CLASS_CAST_ERROR = 2;
    static int INVLAID_JSON_STR = 3;
    static int SYSTEM_NOT_FOUND = 4;
    static int OP_NOT_SUPPORTED = 5;
    static int PAYLOAD_NOT_FOUND = 6;
    static int WHAT_NOT_SUPPORTED = 7;
    static int TO_NOT_SUPPORTED = 8;
    static int PARCEL_ARRAY_PARSE_ERROR = 9;
    static int PARCEL_ARRAY_INDEX_BOUNDS = 10;
    static int ACCESS_DENIED = 11;
    static int UPDATE_FAILED = 12;
    static int INVLAID_ROUTE_ERROR = 13;
    static int ENGINE_ERROR;
    private int error;

    Object source;
    public SystemException(String msg, int error, Object p){
        super(msg);
        this.error = error;
        source = p;
    }

    public static SystemException SYSTEM_NOT_FOUND_EXCEPTION( Parcel p){
        return new SystemException("System Not Found", SYSTEM_NOT_FOUND, p);
    }

    public static SystemException OP_NOT_SUPPORTED(Parcel p){
        return new SystemException("op not supported", OP_NOT_SUPPORTED, p);
    }

    public static SystemException WHAT_NOT_SUPPORTED(Parcel p){
        return new SystemException("what not supported", WHAT_NOT_SUPPORTED,p);
    }

    public static SystemException TO_NOT_SUPPORTED(Parcel p){
        return new SystemException("to not supported", TO_NOT_SUPPORTED,p);
    }

    public static SystemException ROUTE_NOT_SUPPORTED(Parcel p){
        return new SystemException("Route Not Supported", INVLAID_ROUTE_ERROR, p);
    }

    int getError(){
        return error;
    }

    public static SystemException ACCESS_DENIED(Parcel p){
        return new SystemException("Access Denied: op not suppored on what",ACCESS_DENIED, p );
    }
    public static SystemException GENERIC_EXCEPTION(Exception e, Object source){
        return new SystemException(e.toString(), EXCEPTION, source);
    }
    public static SystemException GENERIC_EXCEPTION(Exception e){
        return new SystemException(e.toString(), EXCEPTION, "No Source Specified");
    }

    public static SystemException GENERIC_EXCEPTION(String msg, Parcel source){
        return new SystemException(msg, EXCEPTION, source);
    }


    public static SystemException ENGINE_EXCEPTION(Object p ){
        return new SystemException("Error in Engine: ", ENGINE_ERROR, p);
    }

    @Override
    public String toString() {
        return String.format("%s on source:%s", super.toString(), source.toString());
    }
}
