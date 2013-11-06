package eu.hydrologis.geodroid.dashboard;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.GpsStatus;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import eu.geopaparazzi.library.database.GPLog;
import eu.geopaparazzi.library.gps.GpsLocation;
import eu.geopaparazzi.library.gps.GpsManager;
import eu.geopaparazzi.library.gps.GpsManagerListener;
import eu.geopaparazzi.library.gps.GpsStatusInfo;
import eu.geopaparazzi.library.sensors.SensorsManager;
import eu.hydrologis.geodroid.R;
import eu.hydrologis.geodroid.dashboard.quickaction.actionbar.ActionItem;
import eu.hydrologis.geodroid.dashboard.quickaction.actionbar.QuickAction;

/**
 * The action bar utilities class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class ActionBar implements GpsManagerListener {
    private static final boolean LOG_HOW = GPLog.LOG_ABSURD;
    private static DecimalFormat formatter = new DecimalFormat("0.00000"); //$NON-NLS-1$
    private final View actionBarView;
    private ActionItem infoQuickaction;

    private static String nodataString;
    private static String timeString;
    private static String lonString;
    private static String latString;
    private static String altimString;
    private static String azimString;
    private static String loggingString;
    private static String acquirefixString;
    private static String gpsonString;
    private final GpsManager gpsManager;
    private final SensorsManager sensorsManager;
    private boolean gotFix;
    private boolean isProviderEnabled;
    private GpsStatus lastGpsStatus;
    private long lastLocationupdateMillis;
    private String satellitesString;

    private ActionBar( View actionBarView, GpsManager _gpsManager, SensorsManager sensorsManager ) {
        this.actionBarView = actionBarView;
        gpsManager = _gpsManager;
        this.sensorsManager = sensorsManager;

        // set initial enablement
        isProviderEnabled = gpsManager.isEnabled();

        gpsManager.addListener(this);

        initVars();
        createQuickActions();

        final int gpsInfoButtonId = R.id.action_bar_info;
        ImageButton gpsInfoButton = (ImageButton) actionBarView.findViewById(gpsInfoButtonId);
        gpsInfoButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick( View v ) {
                push(gpsInfoButtonId, v);
            }
        });

        final int compassButtonId = R.id.action_bar_compass;
        ImageButton compassButton = (ImageButton) actionBarView.findViewById(compassButtonId);
        compassButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick( View v ) {
                push(compassButtonId, v);
            }
        });

        checkLogging();
    }

    public void cleanup() {
        gpsManager.removeListener(this);
    }

    public View getActionBarView() {
        return actionBarView;
    }

    private void initVars() {
        Context context = actionBarView.getContext();
        timeString = context.getString(R.string.utctime);
        lonString = context.getString(R.string.lon);
        latString = context.getString(R.string.lat);
        altimString = context.getString(R.string.altim);
        azimString = context.getString(R.string.azimuth);
        nodataString = context.getString(R.string.nogps_data);
        loggingString = context.getString(R.string.text_logging);
        acquirefixString = context.getString(R.string.gps_searching_fix);
        gpsonString = context.getString(R.string.text_gpson);
        satellitesString = context.getString(R.string.satellites);

    }

    public static ActionBar getActionBar( Activity activity, int activityId, GpsManager gpsManager, SensorsManager sensorsManager ) {
        View view = activity.findViewById(activityId);
        return new ActionBar(view, gpsManager, sensorsManager);
    }

    public void setTitle( int titleResourceId, int titleViewId ) {
        TextView textView = (TextView) actionBarView.findViewById(titleViewId);
        if (textView != null) {
            textView.setText(titleResourceId);
        }
    }

    public void push( int id, View v ) {
        switch( id ) {
        case R.id.action_bar_info: {
            QuickAction qa = new QuickAction(v);
            infoQuickaction.setTitle(createGpsInfo());
            qa.addActionItem(infoQuickaction);
            qa.setAnimStyle(QuickAction.ANIM_AUTO);
            qa.show();

            break;
        }
        case R.id.action_bar_compass: {
            Context context = actionBarView.getContext();
            openCompass(context);
            break;
        }
        default:
            break;
        }
    }

    public static void openCompass( final Context context ) {
        String gpsStatusAction = "com.eclipsim.gpsstatus.VIEW";
        String gpsStatusPackage = "com.eclipsim.gpsstatus";
        boolean hasGpsStatus = false;
        List<PackageInfo> installedPackages = new ArrayList<PackageInfo>();

        { // try to get the installed packages list. Seems to have troubles over different
          // versions, so trying them all
            try {
                installedPackages = context.getPackageManager().getInstalledPackages(0);
            } catch (Exception e) {
                // ignore
            }
            if (installedPackages.size() == 0)
                try {
                    installedPackages = context.getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
                } catch (Exception e) {
                    // ignore
                }
        }

        if (installedPackages.size() > 0) {
            // if a list is available, check if the status gps is installed
            for( PackageInfo packageInfo : installedPackages ) {
                String packageName = packageInfo.packageName;
                if (LOG_HOW)
                    GPLog.addLogEntry("ACTIONBAR", packageName);
                if (packageName.startsWith(gpsStatusPackage)) {
                    hasGpsStatus = true;
                    if (LOG_HOW)
                        GPLog.addLogEntry("ACTIONBAR", "Found package: " + packageName);
                    break;
                }
            }
        } else {
            /*
             * if no package list is available, for now try to fire it up anyways.
             * This has been a problem for a user on droidx with android 2.2.1.
             */
            hasGpsStatus = true;
        }

        if (hasGpsStatus) {
            Intent intent = new Intent(gpsStatusAction);
            context.startActivity(intent);
        } else {
            new AlertDialog.Builder(context).setTitle(context.getString(R.string.installgpsstatus_title))
                    .setMessage(context.getString(R.string.installgpsstatus_message)).setIcon(android.R.drawable.ic_dialog_info)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                        public void onClick( DialogInterface dialog, int whichButton ) {
                        }
                    }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                        public void onClick( DialogInterface dialog, int whichButton ) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://search?q=com.eclipsim.gpsstatus2"));
                            context.startActivity(intent);
                        }
                    }).show();
        }
    }

    private void createQuickActions() {
        /*
         * LOG QUICKACTIONS
         */
        infoQuickaction = new ActionItem();
        infoQuickaction.setTitle("Info:\ninfo1\ninfo2");
        // Context context = actionBarView.getContext();
        // infoQuickaction.setIcon(context.getResources().getDrawable(R.drawable.action_bar_info));
        // infoQuickaction.setOnClickListener(new OnClickListener(){
        // public void onClick( View v ) {
        // }
        // });

    }

    private String createGpsInfo() {
        double azimuth = sensorsManager.getNormalAzimuth();
        GpsLocation loc = gpsManager.getLocation();

        StringBuilder sb = new StringBuilder();
        if (loc == null || !gpsManager.isEnabled()) {
            // Logger.d("COMPASSVIEW", "Location from gps is null!");
            sb.append(nodataString);
            sb.append("\n");
            if (isProviderEnabled) {
                if (!gotFix) {
                    sb.append(acquirefixString);
                } else {
                    sb.append(gpsonString);
                    sb.append(": ").append(gpsManager.isEnabled()); //$NON-NLS-1$
                }
            }
            sb.append("\n");
            addGpsStatusInfo(sb);

        } else {
            sb.append(timeString);
            sb.append(" ").append(loc.getTimeString()); //$NON-NLS-1$
            sb.append("\n");
            sb.append(latString);
            sb.append(" ").append(formatter.format(loc.getLatitude())); //$NON-NLS-1$
            sb.append("\n");
            sb.append(lonString);
            sb.append(" ").append(formatter.format(loc.getLongitude())); //$NON-NLS-1$
            sb.append("\n");
            sb.append(altimString);
            sb.append(" ").append((int) loc.getAltitude()); //$NON-NLS-1$
            sb.append("\n");
            sb.append(azimString);
            sb.append(" ").append((int) (360 - azimuth)); //$NON-NLS-1$
            sb.append("\n");
            sb.append(loggingString);
            sb.append(": ").append(gpsManager.isDatabaseLogging()); //$NON-NLS-1$
            sb.append("\n");
            addGpsStatusInfo(sb);
        }
        return sb.toString();
    }

    private void addGpsStatusInfo( StringBuilder sb ) {
        if (lastGpsStatus != null) {
            GpsStatusInfo info = new GpsStatusInfo(lastGpsStatus);
            int satCount = info.getSatCount();
            // int satForFixCount = info.getSatUsedInFixCount();
            sb.append(satellitesString).append(": ").append(satCount).append("\n");
            // sb.append("used for fix: ").append(satForFixCount).append("\n");
        }
    }

    public void checkLogging() {
        Button gpsOnOffView = (Button) actionBarView.findViewById(R.id.gpsOnOff);
        gpsOnOffView.setOnClickListener(new View.OnClickListener(){
            public void onClick( View v ) {
                Context context = v.getContext();
                GpsManager.getInstance(context).checkGps(context);
            }
        });

        if (LOG_HOW && lastGpsStatus != null) {
            GpsStatusInfo info = new GpsStatusInfo(lastGpsStatus);
            int satCount = info.getSatCount();
            int satForFixCount = info.getSatUsedInFixCount();
            StringBuilder sb = new StringBuilder();
            sb.append("satellites: ").append(satCount);
            sb.append(" of which for fix: ").append(satForFixCount);
            GPLog.addLogEntry(this, sb.toString());
        }

        Resources resources = gpsOnOffView.getResources();

        if (isProviderEnabled) {// gpsManager.isEnabled()) {
            if (LOG_HOW)
                GPLog.addLogEntry(this, "GPS seems to be on");
            if (gpsManager.isDatabaseLogging()) {
                if (LOG_HOW)
                    GPLog.addLogEntry(this, "GPS seems to be also logging");
                gpsOnOffView.setBackgroundDrawable(resources.getDrawable(R.drawable.gps_background_logging));
            } else {
                if (gotFix) {
                    if (LOG_HOW)
                        GPLog.addLogEntry(this, "GPS has fix");
                    gpsOnOffView.setBackgroundDrawable(resources.getDrawable(R.drawable.gps_background_hasfix_notlogging));
                } else {
                    if (LOG_HOW)
                        GPLog.addLogEntry(this, "GPS doesn't have a fix");
                    gpsOnOffView.setBackgroundDrawable(resources.getDrawable(R.drawable.gps_background_notlogging));
                }
            }
        } else {
            if (LOG_HOW)
                GPLog.addLogEntry(this, "GPS seems to be off");
            gpsOnOffView.setBackgroundDrawable(resources.getDrawable(R.drawable.gps_background_off));
        }
    }

    public void onLocationChanged( Location location ) {
        if (location == null) {
            return;
        }
        lastLocationupdateMillis = SystemClock.elapsedRealtime();
        if (GPLog.LOG_ABSURD)
            GPLog.addLogEntry(this, "Location update: " + location);
    }

    public void onProviderDisabled( String provider ) {
        if (LOG_HOW)
            GPLog.addLogEntry(this, "Check logging on provider disabled.");
        isProviderEnabled = false;
        checkLogging();
    }

    public void onProviderEnabled( String provider ) {
        isProviderEnabled = true;
        if (LOG_HOW)
            GPLog.addLogEntry(this, "Check logging on provider enabled.");
        checkLogging();
    }

    public void onStatusChanged( String provider, int status, Bundle extras ) {
        // switch( status ) {
        // case LocationProvider.AVAILABLE:
        // if (LOG_HOW)
        // GPLog.addLogEntry(this, "AVAILABLE.");
        // break;
        // case LocationProvider.OUT_OF_SERVICE:
        // if (LOG_HOW)
        // GPLog.addLogEntry(this, "AVAILABLE.");
        // break;
        // case LocationProvider.TEMPORARILY_UNAVAILABLE:
        // if (LOG_HOW)
        // GPLog.addLogEntry(this, "AVAILABLE.");
        // break;
        // default:
        // break;
        // }
    }

    public void gpsStart() {
        gotFix = false;
    }

    public void gpsStop() {
    }

    public void onGpsStatusChanged( int event, GpsStatus status ) {
        boolean tmpGotFix = GpsStatusInfo.checkFix(gotFix, lastLocationupdateMillis, event);
        if (tmpGotFix != gotFix) {
            gotFix = tmpGotFix;
            if (LOG_HOW)
                if (gotFix) {
                    GPLog.addLogEntry(this, "Aquired fix.");
                } else {
                    GPLog.addLogEntry(this, "Lost fix.");
                }
            if (!gotFix) {
                // check if it is just standing still
                GpsStatusInfo info = new GpsStatusInfo(status);
                int satForFixCount = info.getSatUsedInFixCount();
                if (satForFixCount > 2) {
                    gotFix = true;
                    // updating loc update, assuming the still filter is giving troubles
                    lastLocationupdateMillis = SystemClock.elapsedRealtime();
                    if (LOG_HOW)
                        GPLog.addLogEntry(this, "Fix kept due to fix satellites.");
                }
            }
            checkLogging();
        }
        lastGpsStatus = status;
    }

    public boolean hasFix() {
        return gotFix;
    }
}
