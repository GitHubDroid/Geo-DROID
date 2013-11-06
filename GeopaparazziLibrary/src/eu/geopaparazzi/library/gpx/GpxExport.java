package eu.geopaparazzi.library.gpx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import eu.geopaparazzi.library.database.GPLog;
import eu.geodroid.library.util.LibraryConstants;

/**
 * A kmz exporter for notes, logs and pics.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
@SuppressWarnings("nls")
public class GpxExport {

    private final File outputFile;
    private String name;

    /**
     * Constructor.
     * 
     * @param name a name for the gpx.
     * @param outputFile the path in which to create the gpx.
     */
    public GpxExport( String name, File outputFile ) {
        this.name = name;
        this.outputFile = outputFile;
    }

    public void export( Context context, List<GpxRepresenter> gpxRepresenters ) throws IOException {
        if (name == null) {
            name = "Geopaparazzi Gpx Export";
        }

        BufferedWriter bW = null;
        try {
            bW = new BufferedWriter(new FileWriter(outputFile));
            bW.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bW.write("<gpx\n");
            bW.write("  version=\"1.0\"\n");
            bW.write("  creator=\"Geopaparazzi - http://www.geopaparazzi.eu\"\n");
            bW.write("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            bW.write("  xmlns=\"http://www.topografix.com/GPX/1/0\"\n");
            bW.write("  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">\n");

            String formattedTime = LibraryConstants.TIME_FORMATTER_GPX.format(new Date());
            bW.write("<time>" + formattedTime + "</time>\n");

            double minLat = 0.0;
            double minLon = 0.0;
            double maxLat = 0.0;
            double maxLon = 0.0;
            for( GpxRepresenter gpxRepresenter : gpxRepresenters ) {
                minLat = Math.min(minLat, gpxRepresenter.getMinLat());
                minLon = Math.min(minLon, gpxRepresenter.getMinLon());
                maxLat = Math.max(maxLat, gpxRepresenter.getMaxLat());
                maxLon = Math.max(maxLon, gpxRepresenter.getMaxLon());
            }

            bW.write("<bounds minlat=\"" + minLat + "\" minlon=\"" + minLon + "\" maxlat=\"" + maxLat + "\" maxlon=\"" + maxLon
                    + "\"/>\n");
            for( GpxRepresenter gpxRepresenter : gpxRepresenters ) {
                try {
                    bW.write(gpxRepresenter.toGpxString());
                } catch (Exception e) {
                    GPLog.error(this, e.getLocalizedMessage(), e);
                    e.printStackTrace();
                }
            }
            bW.write("</gpx>\n");
        } finally {
            if (bW != null)
                bW.close();
        }
    }
}
