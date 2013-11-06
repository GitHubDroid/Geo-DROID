package eu.geodroid.library.util;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;
import eu.geopaparazzi.library.database.GPLog;

/**
 * Utilities class.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class Utilities {

    private static double originShift = 2 * Math.PI * 6378137 / 2.0;
    private static final double METER_TO_FEET_CONVERSION_FACTOR = 3.2808399;

    public static String getUniqueDeviceId( Context context ) {
        // try to go for the imei
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String id = tm.getDeviceId();
        if (id == null) {
            // try the android id
            id = android.provider.Settings.Secure.getString(context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * Checks if we are on the UI thread.
     * 
     * @return <code>true</code> if we are on the UI thread.
     */
    public static boolean isCurrentThreadTheUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * Convert decimal degrees to exif format.
     * 
     * @param decimalDegree the angle in decimal format.
     * @return the exif format string.
     */
    @SuppressWarnings("nls")
    public static String degreeDecimal2ExifFormat( double decimalDegree ) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) decimalDegree);
        sb.append("/1,");
        decimalDegree = (decimalDegree - (int) decimalDegree) * 60;
        sb.append((int) decimalDegree);
        sb.append("/1,");
        decimalDegree = (decimalDegree - (int) decimalDegree) * 60000;
        sb.append((int) decimalDegree);
        sb.append("/1000");
        if (GPLog.LOG) {
            GPLog.addLogEntry("UTILITIES", sb.toString());
        }
        return sb.toString();
    }

    /**
     * Convert exif format to decimal degree.
     * 
     * @param exifFormat the exif string of the gps position.
     * @return the decimal degree.
     */
    @SuppressWarnings("nls")
    public static double exifFormat2degreeDecimal( String exifFormat ) {
        // latitude=44/1,10/1,28110/1000
        String[] exifSplit = exifFormat.trim().split(",");

        String[] value = exifSplit[0].split("/");

        double tmp1 = Double.parseDouble(value[0]);
        double tmp2 = Double.parseDouble(value[1]);
        double degree = tmp1 / tmp2;

        value = exifSplit[1].split("/");
        tmp1 = Double.parseDouble(value[0]);
        tmp2 = Double.parseDouble(value[1]);
        double minutes = tmp1 / tmp2;

        value = exifSplit[2].split("/");
        tmp1 = Double.parseDouble(value[0]);
        tmp2 = Double.parseDouble(value[1]);
        double seconds = tmp1 / tmp2;

        double result = degree + (minutes / 60.0) + (seconds / 3600.0);
        return result;
    }

    /**
     * Calculates the hypothenuse as of the Pythagorean theorem.
     * 
     * @param d1 the length of the first leg.
     * @param d2 the length of the second leg.
     * @return the length of the hypothenuse.
     */
    public static double pythagoras( double d1, double d2 ) {
        return Math.sqrt(Math.pow(d1, 2.0) + Math.pow(d2, 2.0));
    }

    /**
     * Tries to adapt a value to the supplied type.
     * 
     * @param value the value to adapt. 
     * @param adaptee the class to adapt to.
     * @return the adapted object or <code>null</code>, if it fails.
     */
    public static <T> T adapt( Object value, Class<T> adaptee ) {
        if (value instanceof Number) {
            Number num = (Number) value;
            if (adaptee.isAssignableFrom(Double.class)) {
                return adaptee.cast(num.doubleValue());
            } else if (adaptee.isAssignableFrom(Float.class)) {
                return adaptee.cast(num.floatValue());
            } else if (adaptee.isAssignableFrom(Integer.class)) {
                return adaptee.cast(num.intValue());
            } else if (adaptee.isAssignableFrom(Long.class)) {
                return adaptee.cast(num.longValue());
            } else if (adaptee.isAssignableFrom(String.class)) {
                return adaptee.cast(num.toString());
            } else {
                throw new IllegalArgumentException();
            }
        } else if (value instanceof String) {
            if (adaptee.isAssignableFrom(Double.class)) {
                try {
                    Double parsed = Double.parseDouble((String) value);
                    return adaptee.cast(parsed);
                } catch (Exception e) {
                    return null;
                }
            } else if (adaptee.isAssignableFrom(Float.class)) {
                try {
                    Float parsed = Float.parseFloat((String) value);
                    return adaptee.cast(parsed);
                } catch (Exception e) {
                    return null;
                }
            } else if (adaptee.isAssignableFrom(Integer.class)) {
                try {
                    Integer parsed = Integer.parseInt((String) value);
                    return adaptee.cast(parsed);
                } catch (Exception e) {
                    return null;
                }
            } else if (adaptee.isAssignableFrom(String.class)) {
                return adaptee.cast(value);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException("Can't adapt attribute of type: " + value.getClass().getCanonicalName()); //$NON-NLS-1$
        }
    }

    /**
     * Execute a message dialog in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param msg the message to show.
     * @param okRunnable optional {@link Runnable} to trigger after ok was pressed. 
     */
    public static void messageDialog( final Context context, final String msg, final Runnable okRunnable ) {
        new AsyncTask<String, Void, String>(){
            protected String doInBackground( String... params ) {
                return ""; //$NON-NLS-1$
            }

            protected void onPostExecute( String response ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(msg).setIcon(android.R.drawable.ic_dialog_info).setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                            public void onClick( DialogInterface dialog, int id ) {
                                if (okRunnable != null) {
                                    new Thread(okRunnable).start();
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }.execute((String) null);
    }

    /**
     * Execute a generic error dialog in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param t the exception.
     * @param okRunnable optional {@link Runnable} to trigger after ok was pressed. 
     */
    public static void errorDialog( final Context context, final Throwable t, final Runnable okRunnable ) {

        new AsyncTask<String, Void, String>(){
            protected String doInBackground( String... params ) {
                return ""; //$NON-NLS-1$
            }

            protected void onPostExecute( String response ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder //
                .setTitle(t.getLocalizedMessage()).setMessage(Log.getStackTraceString(t))
                        .setIcon(android.R.drawable.ic_dialog_alert).setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                            public void onClick( DialogInterface dialog, int id ) {
                                if (okRunnable != null) {
                                    new Thread(okRunnable).start();
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }.execute((String) null);
    }

    /**
     * Execute a message dialog in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param msg the message to show.
     * @param yesRunnable optional {@link Runnable} to trigger after yes was pressed. 
     * @param noRunnable optional {@link Runnable} to trigger after no was pressed. 
     */
    public static void yesNoMessageDialog( final Context context, final String msg, final Runnable yesRunnable,
            final Runnable noRunnable ) {
        new AsyncTask<String, Void, String>(){
            protected String doInBackground( String... params ) {
                return ""; //$NON-NLS-1$
            }

            protected void onPostExecute( String response ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(msg).setCancelable(false)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                            public void onClick( DialogInterface dialog, int id ) {
                                if (yesRunnable != null) {
                                    new Thread(yesRunnable).start();
                                }
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener(){
                            public void onClick( DialogInterface dialog, int id ) {
                                if (noRunnable != null) {
                                    new Thread(noRunnable).start();
                                }
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }.execute((String) null);
    }

    /**
     * Execute a message dialog in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param msgId the id of the message to show.
     * @param okRunnable optional {@link Runnable} to trigger after ok was pressed. 
     */
    public static void messageDialog( final Context context, final int msgId, final Runnable okRunnable ) {
        String msg = context.getString(msgId);
        messageDialog(context, msg, okRunnable);
    }

    /**
     * Execute a toast in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param msg the message to show.
     * @param okRunnable optional {@link Runnable} to trigger after ok was pressed. 
     */
    public static void toast( final Context context, final String msg, final int length ) {
        new AsyncTask<String, Void, String>(){
            protected String doInBackground( String... params ) {
                return ""; //$NON-NLS-1$
            }

            protected void onPostExecute( String response ) {
                Toast.makeText(context, msg, length).show();
            }
        }.execute((String) null);
    }

    /**
     * Execute a toast in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param msgId the id of the message to show.
     * @param okRunnable optional {@link Runnable} to trigger after ok was pressed. 
     */
    public static void toast( final Context context, final int msgId, final int length ) {
        String msg = context.getString(msgId);
        toast(context, msg, length);
    }

    /**
     * Execute a message dialog in an {@link AsyncTask}.
     * 
     * @param context the {@link Context} to use.
     * @param title a title for the input dialog.
     * @param message a message to show.
     * @param defaultText a default text to fill in. 
     * @param textRunnable optional {@link TextRunnable} to trigger after ok was pressed. 
     */
    public static void inputMessageDialog( final Context context, final String title, final String message,
            final String defaultText, final TextRunnable textRunnable ) {
        final EditText input = new EditText(context);
        input.setText(defaultText);
        Builder builder = new AlertDialog.Builder(context).setTitle(title);
        builder.setMessage(message);
        builder.setView(input);
        builder.setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog, int whichButton ) {
                    }
                }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
                    public void onClick( DialogInterface dialog, int whichButton ) {
                        Editable value = input.getText();
                        String newText = value.toString();
                        if (newText == null || newText.length() < 1) {
                            newText = defaultText;
                        }
                        if (textRunnable != null) {
                            textRunnable.setText(newText);
                            new Thread(textRunnable).start();
                        }
                    }
                }).setCancelable(false).show();
    }

    public static void ring( Context context ) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();

    }

    /**
     * Converts TMS tile coordinates to Google Tile coordinates.
     * 
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     * 
     * @param tx the x tile number.
     * @param ty the y tile number.
     * @param zoom the current zoom level.
     * @return the converted values.
     */
    public static int[] tmsTile2GoogleTile( int tx, int ty, int zoom ) {
        return new int[]{tx, (int) ((Math.pow(2, zoom) - 1) - ty)};
    }

    /**
     * Converts Google tile coordinates to TMS Tile coordinates.
     * 
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     * 
     * @param tx the x tile number.
     * @param ty the y tile number.
     * @param zoom the current zoom level.
     * @return the converted values.
     */
    public static int[] googleTile2TmsTile( int tx, int ty, int zoom ) {
        return new int[]{tx, (int) ((Math.pow(2, zoom) - 1) - ty)};
    }

    /**
     * Converts TMS tile coordinates to Microsoft QuadTree.
     * 
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     * 
     * @return the quadtree key.
     */
    public static String quadTree( int tx, int ty, int zoom ) {
        String quadKey = ""; //$NON-NLS-1$
        ty = (int) ((Math.pow(2, zoom) - 1) - ty);
        for( int i = zoom; i < 0; i-- ) {
            int digit = 0;
            int mask = 1 << (i - 1);
            if ((tx & mask) != 0) {
                digit += 1;
            }
            if ((ty & mask) != 0) {
                digit += 2;
            }
            quadKey += (digit + ""); //$NON-NLS-1$
        }
        return quadKey;
    }

    /**
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     *
     * @param tx
     * @param ty
     * @param zoom
     * @param tileSize
     * @return [minx, miny, maxx, maxy]
     */
    public static double[] tileLatLonBounds( int tx, int ty, int zoom, int tileSize ) {
        double[] bounds = tileBounds(tx, ty, zoom, tileSize);
        double[] mins = metersToLatLon(bounds[0], bounds[1]);
        double[] maxs = metersToLatLon(bounds[2], bounds[3]);
        return new double[]{mins[1], maxs[0], maxs[1], mins[0]};
    }

    /**
     * Returns bounds of the given tile in EPSG:900913 coordinates
     *
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     *
     * @param tx
     * @param ty
     * @param zoom
     * @return [minx, miny, maxx, maxy]
     */
    public static double[] tileBounds( int tx, int ty, int zoom, int tileSize ) {
        double[] min = pixelsToMeters(tx * tileSize, ty * tileSize, zoom, tileSize);
        double minx = min[0], miny = min[1];
        double[] max = pixelsToMeters((tx + 1) * tileSize, (ty + 1) * tileSize, zoom, tileSize);
        double maxx = max[0], maxy = max[1];
        return new double[]{minx, miny, maxx, maxy};
    }

    /**
     * Converts XY point from Spherical Mercator EPSG:900913 to lat/lon in WGS84
     * Datum
     *
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     *
     * @return
     */
    public static double[] metersToLatLon( double mx, double my ) {

        double lon = (mx / originShift) * 180.0;
        double lat = (my / originShift) * 180.0;

        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180.0)) - Math.PI / 2.0);
        return new double[]{-lat, lon};
    }

    /**
     * Converts pixel coordinates in given zoom level of pyramid to EPSG:900913
     *
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     *
     * @return
     */
    public static double[] pixelsToMeters( double px, double py, int zoom, int tileSize ) {
        double res = getResolution(zoom, tileSize);
        double mx = px * res - originShift;
        double my = py * res - originShift;
        return new double[]{mx, my};
    }

    /**
     * Resolution (meters/pixel) for given zoom level (measured at Equator)
     *
     * <p>Code copied from: http://code.google.com/p/gmap-tile-generator/</p>
     *
     * @return
     */
    public static double getResolution( int zoom, int tileSize ) {
        // return (2 * Math.PI * 6378137) / (this.tileSize * 2**zoom)
        double initialResolution = 2 * Math.PI * 6378137 / tileSize;
        return initialResolution / Math.pow(2, zoom);
    }

    @SuppressWarnings("nls")
    public static String makeXmlSafe( String string ) {
        if (string == null)
            return "";
        string = string.replaceAll("&", "&amp;");
        return string;
    }

    /**
     * A string formatter. 
     * 
     * <p>This method exists, because the method to use is not definitive yet.</p>
     * <p>
     * Currently the format of the substitutes in the message are: 
     * <ul>
     *   <li>%1$d = for numeric</li>
     *   <li>%1$s = for strings</li>
     * </ul> 
     * The %1, %2, etc refer to the number of the args.
     * </p>
     * 
     * @param msg the message.
     * @param args the args to substitute.
     * @return the formatted string.
     */
    public static String format( String msg, String... args ) {
        String msgFormat = String.format(msg, (Object[]) args);
        return msgFormat;
    }

    /**
     * Convert bytes to hex string.
     * 
     * @param b the bytes array to convert.
     * @param size the size of the array to consider.
     * @return the hex string.
     */
    public static String getHexString( byte[] b, int size ) {
        if (size < 1) {
            size = b.length;
        }
        StringBuilder sb = new StringBuilder();
        for( int i = size - 1; i >= 0; i-- ) {
            if (i >= 0)
                sb.append(Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    /**
     * Get the megabytes available in the filesystem at 'file'.
     * 
     * @param file the filesystem's path.
     * @return the available space in mb.
     */
    public static float getAvailableMegabytes( File file ) {
        StatFs stat = new StatFs(file.getPath());
        long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getAvailableBlocks();
        return bytesAvailable / (1024.f * 1024.f);
    }

    /**
     * Get the size in megabytes of the filesystem at 'file'.
     * 
     * @param file the filesystem's path.
     * @return the size in mb.
     */
    public static float getFilesystemMegabytes( File file ) {
        StatFs stat = new StatFs(file.getPath());
        long bytes = (long) stat.getBlockSize() * (long) stat.getBlockCount();
        return bytes / (1024.f * 1024.f);
    }

    /** 
     * Convert meters to feet. 
      * 
      * @param meters the value in meters to convert to feet.
      * @return meters converted to feet. 
      */
    public static double toFeet( final double meters ) {
        return meters * METER_TO_FEET_CONVERSION_FACTOR;
    }

    /**
     * Create an OSM url from coordinates.
     * 
     * @param lat
     * @param lon
     * @param withMarker
     * @param withGeosmsParam
     * @return
     */
    public static String osmUrlFromLatLong( float lat, float lon, boolean withMarker, boolean withGeosmsParam ) {
        StringBuilder sB = new StringBuilder();
        sB.append("http://www.osm.org/?lat=");
        sB.append(lat);
        sB.append("&lon=");
        sB.append(lon);
        sB.append("&zoom=14");
        if (withMarker) {
            sB.append("&layers=M&mlat=");
            sB.append(lat);
            sB.append("&mlon=");
            sB.append(lon);
        }
        if (withGeosmsParam) {
            sB.append("&GeoSMS");
        }
        return sB.toString();
    }

}
