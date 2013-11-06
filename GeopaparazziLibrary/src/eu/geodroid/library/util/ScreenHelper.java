package eu.geodroid.library.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Helper class for screen matters. 
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class ScreenHelper {

    private static ScreenHelper screenHelper;
    private final Activity activity;

    private ScreenHelper( Activity activity ) {
        this.activity = activity;
    }

    public static ScreenHelper getInstance( Activity activity ) throws Exception {
        if (screenHelper == null) {
            screenHelper = new ScreenHelper(activity);
        }
        return screenHelper;
    }

    /**
     * Gets the density of the screen.
     * 
     * Can be checked with:
     * <code>
     *  switch( metrics.densityDpi ) {
     *   case DisplayMetrics.DENSITY_LOW:
     *       break;
     *   case DisplayMetrics.DENSITY_MEDIUM:
     *       break;
     *   case DisplayMetrics.DENSITY_HIGH:
     *       break;
     *   }
     * </code>
     * 
     * @return the density of the screen.
     */
    public int getDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.densityDpi;
    }

}
