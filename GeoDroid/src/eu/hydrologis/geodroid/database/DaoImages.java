package eu.hydrologis.geodroid.database;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.model.GeoPoint;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.drawable.Drawable;
import android.util.Log;
import eu.geopaparazzi.library.database.GPLog;
import eu.geodroid.library.util.LibraryConstants;
import eu.hydrologis.geodroid.util.Image;

/**
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class DaoImages {

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LON = "lon";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_ALTIM = "altim";
    private static final String COLUMN_AZIM = "azim";
    private static final String COLUMN_PATH = "path";
    private static final String COLUMN_TS = "ts";
    private static final String COLUMN_TEXT = "text";

    public static final String TABLE_IMAGES = "images";

    private static long LASTINSERTEDIMAGE_ID = -1;

    private static SimpleDateFormat dateFormatter = LibraryConstants.TIME_FORMATTER_SQLITE;

    public static void addImage( double lon, double lat, double altim, double azim, Date timestamp, String text, String path ) throws IOException {
        SQLiteDatabase sqliteDatabase = DatabaseManager.getInstance().getDatabase();
        sqliteDatabase.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_LON, lon);
            values.put(COLUMN_LAT, lat);
            values.put(COLUMN_ALTIM, altim);
            values.put(COLUMN_TS, dateFormatter.format(timestamp));
            values.put(COLUMN_TEXT, text);
            values.put(COLUMN_PATH, path);
            values.put(COLUMN_AZIM, azim);
            LASTINSERTEDIMAGE_ID = sqliteDatabase.insertOrThrow(TABLE_IMAGES, null, values);

            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            GPLog.error("DAOIMAGES", e.getLocalizedMessage(), e);
            throw new IOException(e.getLocalizedMessage());
        } finally {
            sqliteDatabase.endTransaction();
        }
    }

    /**
     * Deletes the image reference without actually deleting teh image from disk.
     * 
     * @param id
     * @throws IOException
     */
    public static void deleteImage( long id ) throws IOException {
        SQLiteDatabase sqliteDatabase = DatabaseManager.getInstance().getDatabase();
        sqliteDatabase.beginTransaction();
        try {
            // delete note
            String query = "delete from " + TABLE_IMAGES + " where " + COLUMN_ID + " = " + id;
            SQLiteStatement sqlUpdate = sqliteDatabase.compileStatement(query);
            sqlUpdate.execute();

            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            GPLog.error("DAOIMAGES", e.getLocalizedMessage(), e);
            throw new IOException(e.getLocalizedMessage());
        } finally {
            sqliteDatabase.endTransaction();
        }
    }

    public static void deleteLastInsertedImage() throws IOException {
        if (LASTINSERTEDIMAGE_ID != -1) {
            deleteImage(LASTINSERTEDIMAGE_ID);
        }
    }

    /**
     * Get the collected notes from the database inside a given bound.
     * @param n
     * @param s
     * @param w
     * @param e
     * 
     * @return the list of notes inside the bounds.
     * @throws IOException
     */
    public static List<Image> getImagesInWorldBounds( float n, float s, float w, float e ) throws IOException {

        SQLiteDatabase sqliteDatabase = DatabaseManager.getInstance().getDatabase();
        String query = "SELECT _id, lon, lat, altim, azim, path, text, ts FROM XXX WHERE (lon BETWEEN XXX AND XXX) AND (lat BETWEEN XXX AND XXX)";
        // String[] args = new String[]{TABLE_NOTES, String.valueOf(w), String.valueOf(e),
        // String.valueOf(s), String.valueOf(n)};

        query = query.replaceFirst("XXX", TABLE_IMAGES);
        query = query.replaceFirst("XXX", String.valueOf(w));
        query = query.replaceFirst("XXX", String.valueOf(e));
        query = query.replaceFirst("XXX", String.valueOf(s));
        query = query.replaceFirst("XXX", String.valueOf(n));

        // if (Debug.D) Logger.i("DAOIMAGES", "Query: " + query);

        Cursor c = sqliteDatabase.rawQuery(query, null);
        List<Image> images = new ArrayList<Image>();
        c.moveToFirst();
        while( !c.isAfterLast() ) {
            long id = c.getLong(0);
            double lon = c.getDouble(1);
            double lat = c.getDouble(2);
            double altim = c.getDouble(3);
            double azim = c.getDouble(4);
            String path = c.getString(5);
            String text = c.getString(6);
            String date = c.getString(7);

            Image image = new Image(id, text, lon, lat, altim, azim, path, date);
            images.add(image);
            c.moveToNext();
        }
        c.close();
        return images;
    }

    /**
     * Get the list of notes from the db.
     * 
     * @return list of notes.
     * @throws IOException
     */
    public static List<Image> getImagesList( ) throws IOException {
        SQLiteDatabase sqliteDatabase = DatabaseManager.getInstance().getDatabase();
        List<Image> images = new ArrayList<Image>();
        String asColumnsToReturn[] = {COLUMN_ID, COLUMN_LON, COLUMN_LAT, COLUMN_ALTIM, COLUMN_AZIM, COLUMN_PATH, COLUMN_TS,
                COLUMN_TEXT};
        String strSortOrder = "_id ASC";
        Cursor c = sqliteDatabase.query(TABLE_IMAGES, asColumnsToReturn, null, null, null, null, strSortOrder);
        c.moveToFirst();
        while( !c.isAfterLast() ) {
            long id = c.getLong(0);
            double lon = c.getDouble(1);
            double lat = c.getDouble(2);
            double altim = c.getDouble(3);
            double azim = c.getDouble(4);
            String path = c.getString(5);
            String date = c.getString(6);
            String text = c.getString(7);

            Image image = new Image(id, text, lon, lat, altim, azim, path, date);
            images.add(image);
            c.moveToNext();
        }
        c.close();
        return images;
    }

    public static List<OverlayItem> getImagesOverlayList( Drawable marker ) throws IOException {
        SQLiteDatabase sqliteDatabase = DatabaseManager.getInstance().getDatabase();
        List<OverlayItem> images = new ArrayList<OverlayItem>();
        String asColumnsToReturn[] = {COLUMN_LON, COLUMN_LAT, COLUMN_PATH, COLUMN_TEXT};
        String strSortOrder = "_id ASC";
        Cursor c = sqliteDatabase.query(TABLE_IMAGES, asColumnsToReturn, null, null, null, null, strSortOrder);
        c.moveToFirst();
        while( !c.isAfterLast() ) {
            double lon = c.getDouble(0);
            double lat = c.getDouble(1);
            String path = c.getString(2);
            String text = c.getString(3);

            OverlayItem image = new OverlayItem(new GeoPoint(lat, lon), path, text, marker);
            images.add(image);
            c.moveToNext();
        }
        c.close();
        return images;
    }

    public static void createTables( ) throws IOException {
        StringBuilder sB = new StringBuilder();

        sB = new StringBuilder();
        sB.append("CREATE TABLE ");
        sB.append(TABLE_IMAGES);
        sB.append(" (");
        sB.append(COLUMN_ID);
        sB.append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sB.append(COLUMN_LON).append(" REAL NOT NULL, ");
        sB.append(COLUMN_LAT).append(" REAL NOT NULL,");
        sB.append(COLUMN_ALTIM).append(" REAL NOT NULL,");
        sB.append(COLUMN_AZIM).append(" REAL NOT NULL,");
        sB.append(COLUMN_PATH).append(" TEXT NOT NULL,");
        sB.append(COLUMN_TS).append(" DATE NOT NULL,");
        sB.append(COLUMN_TEXT).append(" TEXT NOT NULL");
        sB.append(");");
        String CREATE_TABLE_IMAGES = sB.toString();

        sB = new StringBuilder();
        sB.append("CREATE INDEX images_ts_idx ON ");
        sB.append(TABLE_IMAGES);
        sB.append(" ( ");
        sB.append(COLUMN_TS);
        sB.append(" );");
        String CREATE_INDEX_IMAGES_TS = sB.toString();

        sB = new StringBuilder();
        sB.append("CREATE INDEX images_x_by_y_idx ON ");
        sB.append(TABLE_IMAGES);
        sB.append(" ( ");
        sB.append(COLUMN_LON);
        sB.append(", ");
        sB.append(COLUMN_LAT);
        sB.append(" );");
        String CREATE_INDEX_IMAGES_X_BY_Y = sB.toString();

        SQLiteDatabase sqliteDatabase = DatabaseManager.getInstance().getDatabase();
        if (GPLog.LOG_HEAVY)
            Log.i("DAOIMAGES", "Create the images table.");

        sqliteDatabase.beginTransaction();
        try {
            sqliteDatabase.execSQL(CREATE_TABLE_IMAGES);
            sqliteDatabase.execSQL(CREATE_INDEX_IMAGES_TS);
            sqliteDatabase.execSQL(CREATE_INDEX_IMAGES_X_BY_Y);

            sqliteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DAOIMAGES", e.getLocalizedMessage(), e);
            throw new IOException(e.getLocalizedMessage());
        } finally {
            sqliteDatabase.endTransaction();
        }
    }

}
