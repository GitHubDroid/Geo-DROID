package eu.geodroid.library.util;

import android.graphics.PointF;
import android.location.Location;

/**
 * Add a third dimension and description to {@link PointF}.
 * 
 * <p>Note that only functions added here implement the third dimension.</p>
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class PointF3D extends PointF {
    private float z = Float.NaN;
    private boolean hasZ = false;

    private String description = ""; //$NON-NLS-1$

    public PointF3D( float x, float y ) {
        super(x, y);
        hasZ = false;
    }

    public PointF3D( float x, float y, float z ) {
        super(x, y);
        this.z = z;
        hasZ = true;
    }

    public PointF3D( float x, float y, float z, String description ) {
        this(x, y, z);
        this.z = z;
        if (description != null)
            this.description = description;
        hasZ = true;
    }

    public void setZ( float z ) {
        this.z = z;
        hasZ = true;
    }

    public float getZ() {
        return z;
    }

    public boolean hasZ() {
        return hasZ;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Calculates the 2d distance between two points.
     * 
     * @param p the {@link PointF} from which to calculate from.
     * @return the 2d distance.
     */
    @SuppressWarnings("nls")
    public float distance2d( PointF p ) {
        Location thisLoc = new Location("dummy");
        thisLoc.setLongitude(x);
        thisLoc.setLatitude(y);
        Location thatLoc = new Location("dummy");
        thatLoc.setLongitude(p.x);
        thatLoc.setLatitude(p.y);

        return thisLoc.distanceTo(thatLoc);
    }

    /**
     * Calculates the 3d distance between two points if z is available.
     * 
     * @param p the {@link PointF3D} from which to calculate from.
     * @return the 3d distance (or 2d if no elevation info is available).
     */
    public float distance3d( PointF3D p ) {
        float distance2d = distance2d(p);
        if (hasZ && p.hasZ()) {
            double distance3d = Utilities.pythagoras(distance2d, Math.abs(z - p.getZ()));
            return (float) distance3d;
        }
        return distance2d;
    }

}
