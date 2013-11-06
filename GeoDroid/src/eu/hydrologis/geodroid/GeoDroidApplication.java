package eu.hydrologis.geodroid;

import eu.geopaparazzi.library.database.GPLog;
import android.app.Application;
import android.util.Log;

/**
 * Application singleton.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GeoDroidApplication extends Application {

    private static GeoDroidApplication instance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (GPLog.LOG_ANDROID) {
            Log.i(getClass().getSimpleName(), "GeoDroidApplication singleton created.");
        }
    }

    public static GeoDroidApplication getInstance() {
        return instance;
    }
}
