package eu.geodroid.library.util;
/**
 * @author Andrea Antonello (www.hydrologis.com)
 * 
 * A bounding box that keeps both world and screen space.
 */
public class BoundingBox {
    public float north;
    public float south;
    public float east;
    public float west;
    public int left;
    public int bottom;
    public int right;
    public int top;

    @SuppressWarnings("nls")
    public String toString() {
        StringBuilder sB = new StringBuilder();
        sB.append("left=").append(left).append("/");
        sB.append("right=").append(right).append("/");
        sB.append("top=").append(top).append("/");
        sB.append("bottom=").append(bottom);
        return sB.toString();
    }
}