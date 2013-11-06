package eu.hydrologis.geodroid.gpx;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import eu.geopaparazzi.library.database.GPLog;
import eu.geopaparazzi.library.gpx.GpxItem;
import eu.geopaparazzi.library.gpx.parser.GpxParser;
import eu.geopaparazzi.library.gpx.parser.GpxParser.Route;
import eu.geopaparazzi.library.gpx.parser.GpxParser.TrackSegment;
import eu.geopaparazzi.library.gpx.parser.WayPoint;
import eu.geodroid.library.util.FileUtilities;
import eu.geodroid.library.util.LibraryConstants;
import eu.hydrologis.geodroid.R;
import eu.hydrologis.geodroid.database.DaoGpsLog;

/**
 * Gpx file import activity.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GpxImportActivity extends Activity {

    private String path;

    public void onCreate( Bundle icicle ) {
        super.onCreate(icicle);
        // setContentView(R.layout.note);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString(LibraryConstants.PREFS_KEY_PATH);

            gpxImportProgressDialog = ProgressDialog.show(GpxImportActivity.this, getString(R.string.gpx_import_processing),
                    "", true, true); //$NON-NLS-1$
            new Thread(){
                public void run() {
                    try {
                        File file = new File(path);
                        String fileName = FileUtilities.getNameWithoutExtention(file);
                        GpxParser parser = new GpxParser(path);
                        if (parser.parse()) {
                            List<WayPoint> wayPoints = parser.getWayPoints();
                            if (wayPoints.size() > 0) {
                                GpxItem item = new GpxItem();
                                item.setName(fileName);
                                item.setWidth("2"); //$NON-NLS-1$
                                item.setVisible(false);
                                item.setColor("blue"); //$NON-NLS-1$
                                item.setData(wayPoints);
                                DaoGpsLog.importGpxToMap(GpxImportActivity.this, item);
                            }
                            List<TrackSegment> tracks = parser.getTracks();
                            if (tracks.size() > 0) {
                                for( TrackSegment trackSegment : tracks ) {
                                    String tName = trackSegment.getName();
                                    if (tName == null) {
                                        tName = ""; //$NON-NLS-1$
                                    } else {
                                        tName = " - " + tName; //$NON-NLS-1$
                                    }
                                    String name = fileName + tName;
                                    GpxItem item = new GpxItem();
                                    item.setName(name);
                                    item.setWidth("2"); //$NON-NLS-1$
                                    item.setVisible(false);
                                    item.setColor("red"); //$NON-NLS-1$
                                    item.setData(trackSegment);
                                    DaoGpsLog.importGpxToMap(GpxImportActivity.this, item);
                                }
                            }
                            List<Route> routes = parser.getRoutes();
                            if (routes.size() > 0) {
                                for( Route route : routes ) {
                                    String rName = route.getName();
                                    if (rName == null) {
                                        rName = ""; //$NON-NLS-1$
                                    } else {
                                        rName = " - " + rName; //$NON-NLS-1$
                                    }
                                    String name = fileName + rName;
                                    GpxItem item = new GpxItem();
                                    item.setName(name);
                                    item.setWidth("2"); //$NON-NLS-1$
                                    item.setVisible(false);
                                    item.setColor("green"); //$NON-NLS-1$
                                    item.setData(route);
                                    DaoGpsLog.importGpxToMap(GpxImportActivity.this, item);
                                }
                            }
                        } else {
                            if (GPLog.LOG)
                                GPLog.addLogEntry(this, "ERROR"); //$NON-NLS-1$
                        }

                    } catch (IOException e) {
                        GPLog.error(this, e.getLocalizedMessage(), e);
                        e.printStackTrace();
                    } finally {
                        gpsImportHandler.sendEmptyMessage(0);
                        finish();
                    }
                }
            }.start();

        }

    }
    private ProgressDialog gpxImportProgressDialog;
    private Handler gpsImportHandler = new Handler(){
        public void handleMessage( android.os.Message msg ) {
            gpxImportProgressDialog.dismiss();
        };
    };
}
