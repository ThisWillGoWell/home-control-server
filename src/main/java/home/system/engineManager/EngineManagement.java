package home.system.engineManager;

import home.controller.Engine;
import home.parcel.Parcel;
import home.parcel.SystemException;
import home.system.SystemParent;

/**
 * THis system is for spawing new systems into the engine
 * Created by Will on 3/6/2017.
 */
public class EngineManagement extends SystemParent{
    private static final String systemIdentifier = "engine";
    public EngineManagement(Engine e) {
        super(systemIdentifier, e);
    }

    @Override
    public Parcel process(Parcel p) throws SystemException {

        switch (p.getString("op")){
            case "init":
                engine.initialize();
                return Parcel.RESPONSE_PARCEL("All Systems have been reset");
            case "startSystem":
                return engine.addSystem(p);
            case "stopSystem":
                return engine.stopSystem(p);
            default:
                throw SystemException.OP_NOT_SUPPORTED(p);
        }

    }
}
