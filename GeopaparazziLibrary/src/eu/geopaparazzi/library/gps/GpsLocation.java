package eu.geopaparazzi.library.gps;

import java.util.Date;

import android.location.Location;
import eu.geodroid.library.util.LibraryConstants;

/**
 * Extends the location with some infos.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class GpsLocation extends Location {

    private Location previousLoc = null;

    public GpsLocation( Location l ) {
        super(l);
    }

    public Location getPreviousLoc() {
        return previousLoc;
    }

    public void setPreviousLoc( Location previousLoc ) {
        this.previousLoc = previousLoc;
    }

    public String getTimeString() {
        String timeString = LibraryConstants.TIME_FORMATTER.format(new Date(getTime()));
        return timeString;
    }

    public String getTimeStringSql() {
        String timeString = LibraryConstants.TIME_FORMATTER_SQLITE.format(new Date(getTime()));
        return timeString;
    }

    public Date getTimeDate() {
        return new Date(getTime());
    }

    public java.sql.Date getSqlDate() {
        return new java.sql.Date(getTime());
    }

}
