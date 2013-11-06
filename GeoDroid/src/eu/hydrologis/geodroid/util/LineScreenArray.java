package eu.hydrologis.geodroid.util;


/**
 * Represents a line in 2d based on screen coords.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public class LineScreenArray {

    private final String fileName;

    private int[] xArray;
    private int[] yArray;
    private int index = 0;

    private int maxArraySize = 100;

    public LineScreenArray( String logid, int initialCount ) {
        this.fileName = logid;
        maxArraySize = initialCount;
        xArray = new int[maxArraySize];
        yArray = new int[maxArraySize];
    }

    public LineScreenArray( String logid ) {
        this(logid, 100);
    }

    public void addPoint( int x, int y ) {
        if (index == maxArraySize) {
            // enlarge array
            int[] tmpLon = new int[maxArraySize + 100];
            int[] tmpLat = new int[maxArraySize + 100];
            System.arraycopy(xArray, 0, tmpLon, 0, maxArraySize);
            System.arraycopy(yArray, 0, tmpLat, 0, maxArraySize);
            xArray = tmpLon;
            yArray = tmpLat;
            maxArraySize = maxArraySize + 100;
        }

        xArray[index] = x;
        yArray[index] = y;
        index++;

    }

    public String getfileName() {
        return fileName;
    }

    public int[] getLatArray() {
        return yArray;
    }

    public int[] getLonArray() {
        return xArray;
    }

    public int getIndex() {
        return index;
    }

}
