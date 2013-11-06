package eu.hydrologis.geodroid.util;

import java.util.Collections;
import java.util.List;

import eu.geopaparazzi.library.kml.KmlRepresenter;
import eu.geodroid.library.util.Utilities;

/**
 * Represents a bookmark.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class Bookmark implements KmlRepresenter {
    private String name;
    private double lon;
    private double lat;
    private long id;
    private double zoom;
    private double north;
    private double south;
    private double west;
    private double east;

    /**
     * A wrapper for a Bookmark.
     * 
     * @param id 
     * @param name the name of the Bookmark.
     * @param lon
     * @param lat
     */
    public Bookmark( long id, String name, double lon, double lat ) {
        this.id = id;
        if (name != null) {
            this.name = name;
        } else {
            this.name = ""; //$NON-NLS-1$
        }
        this.lon = lon;
        this.lat = lat;
    }

    public Bookmark( long id, String name, double lon, double lat, double zoom, double north, double south, double west,
            double east ) {
        this.id = id;
        this.zoom = zoom;
        this.north = north;
        this.south = south;
        this.west = west;
        this.east = east;
        if (name != null) {
            this.name = name;
        } else {
            this.name = ""; //$NON-NLS-1$
        }
        this.lon = lon;
        this.lat = lat;
    }

    @SuppressWarnings("nls")
    public String toKmlString() throws Exception {
        String name = Utilities.makeXmlSafe(this.name);
        StringBuilder sB = new StringBuilder();
        sB.append("<Placemark>\n");
        // sB.append("<styleUrl>#red-pushpin</styleUrl>\n");
        sB.append("<styleUrl>#bookmark-icon</styleUrl>\n");
        sB.append("<name>").append(name).append("</name>\n");
        sB.append("<description>\n");
        sB.append(name);
        sB.append("</description>\n");
        sB.append("<gx:balloonVisibility>1</gx:balloonVisibility>\n");
        sB.append("<Point>\n");
        sB.append("<coordinates>").append(lon).append(",").append(lat).append(",0</coordinates>\n");
        sB.append("</Point>\n");
        sB.append("</Placemark>\n");

        return sB.toString();
    }

    public long getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getName() {
        return name;
    }

    public double getZoom() {
        return zoom;
    }

    public double getNorth() {
        return north;
    }

    public double getSouth() {
        return south;
    }

    public double getWest() {
        return west;
    }

    public double getEast() {
        return east;
    }

    @SuppressWarnings("nls")
    public String toString() {
        return "Bookmark [name=" + name + ", lon=" + lon + ", lat=" + lat + ", id=" + id + ", zoom=" + zoom + ", north=" + north
                + ", south=" + south + ", west=" + west + ", east=" + east + "]";
    }

    public boolean hasImages() {
        return false;
    }

    public List<String> getImagePaths() {
        return Collections.emptyList();
    }
}
