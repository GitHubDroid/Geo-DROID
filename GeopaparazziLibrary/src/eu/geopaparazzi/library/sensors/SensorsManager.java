package eu.geopaparazzi.library.sensors;

import static java.lang.Math.toDegrees;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Singleton that takes care of sensor matters.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class SensorsManager implements SensorEventListener {

    private static SensorsManager sensorManager;

    private SensorManager sensorManagerInternal;
    private ConnectivityManager connectivityManager;

    private int accuracy;

    private double normalAzimuth = -1;
    // private double normalPitch = -1;
    // private double normalRoll = -1;
    private double pictureAzimuth = -1;
    // private double picturePitch = -1;
    // private double pictureRoll = -1;
    private float[] mags;

    private boolean isReady;

    private float[] accels;

    private final static int matrix_size = 16;
    private final float[] RM = new float[matrix_size];
    private final float[] outR = new float[matrix_size];
    private final float[] I = new float[matrix_size];
    private final float[] values = new float[3];

    private final Context context;

    private List<SensorsManagerListener> listeners = new ArrayList<SensorsManagerListener>();

    private SensorsManager( Context context ) {
        this.context = context;

    }

    public synchronized static SensorsManager getInstance( Context context ) {
        if (sensorManager == null) {
            sensorManager = new SensorsManager(context);
            sensorManager.activateSensorManagers();
            sensorManager.startSensorListening();
        }
        return sensorManager;
    }

    /**
     * Add a listener to gps.
     * 
     * @param listener the listener to add.
     */
    public void addListener( SensorsManagerListener listener ) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a listener to gps.
     * 
     * @param listener the listener to remove.
     */
    public void removeListener( SensorsManagerListener listener ) {
        listeners.remove(listener);
    }

    /**
     * Get the location and sensor managers.
     */
    public void activateSensorManagers() {
        sensorManagerInternal = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isInternetOn() {
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    public int getAccuracy() {
        return accuracy;
    }

    public double getNormalAzimuth() {
        return normalAzimuth;
    }

    public double getPictureAzimuth() {
        return pictureAzimuth;
    }

    /**
     * Stops listening to all the devices.
     */
    public void stopSensorListening() {
        if (sensorManagerInternal != null && sensorManager != null)
            sensorManagerInternal.unregisterListener(sensorManager);
    }

    /**
     * Starts listening to all the devices.
     */
    public void startSensorListening() {

        sensorManagerInternal.unregisterListener(sensorManager);
        sensorManagerInternal.registerListener(sensorManager, sensorManagerInternal.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManagerInternal.registerListener(sensorManager, sensorManagerInternal.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManagerInternal.registerListener(sensorManager, sensorManagerInternal.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onAccuracyChanged( Sensor sensor, int accuracy ) {
        int type = sensor.getType();
        if (type == SensorManager.SENSOR_ORIENTATION) {
            this.accuracy = accuracy;
        }
    }

    public void onSensorChanged( SensorEvent event ) {
        Sensor sensor = event.sensor;
        int type = sensor.getType();

        switch( type ) {
        case Sensor.TYPE_MAGNETIC_FIELD:
            mags = event.values.clone();
            isReady = true;
            break;
        case Sensor.TYPE_ACCELEROMETER:
            accels = event.values.clone();
            break;
        // case Sensor.TYPE_ORIENTATION:
        // orients = event.values.clone();
        // break;
        }

        if (mags != null && accels != null && isReady) {
            isReady = false;

            SensorManager.getRotationMatrix(RM, I, accels, mags);
            SensorManager.remapCoordinateSystem(RM, SensorManager.AXIS_X, SensorManager.AXIS_Y, outR);
            SensorManager.getOrientation(outR, values);
            normalAzimuth = toDegrees(values[0]);
            // normalPitch = toDegrees(values[1]);
            // normalRoll = toDegrees(values[2]);
            // int orientation = getContext().getResources().getConfiguration().orientation;
            // switch( orientation ) {
            // case Configuration.ORIENTATION_LANDSCAPE:
            // normalAzimuth = -1 * (normalAzimuth - 135);
            // case Configuration.ORIENTATION_PORTRAIT:
            // default:
            // break;
            // }
            // normalAzimuth = normalAzimuth > 0 ? normalAzimuth : (360f + normalAzimuth);
            // Logger.d(this, "NAZIMUTH = " + normalAzimuth);

            SensorManager.remapCoordinateSystem(RM, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);
            SensorManager.getOrientation(outR, values);

            pictureAzimuth = toDegrees(values[0]);
            // picturePitch = toDegrees(values[1]);
            // pictureRoll = toDegrees(values[2]);
            pictureAzimuth = pictureAzimuth > 0 ? pictureAzimuth : (360f + pictureAzimuth);

            // Logger.d(sensorManager, "PAZIMUTH = " + pictureAzimuth);

            for( SensorsManagerListener listener : listeners ) {
                listener.onSensorChanged(normalAzimuth, pictureAzimuth);
            }
        }

    }

}
