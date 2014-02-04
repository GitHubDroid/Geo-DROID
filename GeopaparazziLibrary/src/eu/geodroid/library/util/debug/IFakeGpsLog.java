package eu.geodroid.library.util.debug;

/**
 * A fake gps log interface to support demo log mockings.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 */
public interface IFakeGpsLog {

    /**
     * @return <code>true</code> if a record is available.
     */
    public abstract boolean hasNext();

    /**
     * Get the next gps record.
     * 
     * @return the record in the csv format: 
     *          time(long),lon,lat,altimetry,speed,accuracy(meters) 
     */
    public abstract String next();

    /**
     * Resets to start new.
     */
    public abstract void reset();

}